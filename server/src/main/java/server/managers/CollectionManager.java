package server.managers;

import common.models.Organization;
import common.models.OrganizationType;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CollectionManager {

    private final LinkedHashMap<Integer, Organization> collections;
    private final LocalDateTime creationDate;

    public CollectionManager(LinkedHashMap<Integer, Organization> collections) {
        this.collections = (collections != null) ? collections : new LinkedHashMap<>();
        this.creationDate = LocalDateTime.now();
    }

    public synchronized void insert(Integer key, Organization organization) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        if (organization == null) {
            throw new IllegalArgumentException("Organization cannot be null");
        }
        if (collections.containsKey(key)) {
            throw new IllegalArgumentException("Key already exists");
        }

        collections.put(key, organization);
    }

    public synchronized void remove(Integer key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        if (!collections.containsKey(key)) throw new IllegalArgumentException("Key does not exist");

        collections.remove(key);
    }

    public boolean containsKey(Integer key) {
        return collections.containsKey(key);
    }

    public synchronized void clear() {
        collections.clear();
    }

    public synchronized void clearUser(Long userId) {
        collections.entrySet().removeIf(entry ->
                entry.getValue().getOwnerId() != null &&
                        entry.getValue().getOwnerId().equals(userId)
        );
    }

    public synchronized void update(Long id, Organization newOrg) {
        if (id == null) throw new IllegalArgumentException("Id cannot be null");
        if (newOrg == null) throw new IllegalArgumentException("Organization cannot be null");

        Integer keyToUpdate = collections.entrySet().stream()
                .filter(entry -> entry.getValue().getId().equals(id))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Organization with id " + id + " does not exist"));

        Organization oldOrg = collections.get(keyToUpdate);

        Organization updatedOrg = new Organization(
                oldOrg.getId(),
                newOrg.getName(),
                newOrg.getCoordinates(),
                oldOrg.getCreationDate(),
                newOrg.getAnnualTurnover(),
                newOrg.getType(),
                newOrg.getOfficialAddress(),
                oldOrg.getOwnerId()
        );

        collections.put(keyToUpdate, updatedOrg);
    }

    public synchronized void removeGreaterKey(Integer referenceKey) {
        if (referenceKey == null) {
            throw new IllegalArgumentException("Reference key cannot be null");
        }

        collections.entrySet().removeIf(e -> e.getKey() > referenceKey);
    }

    public synchronized void removeGreaterKey(Integer referenceKey, Long userId) {
        collections.entrySet().removeIf(e ->
                e.getKey() > referenceKey &&
                        e.getValue().getOwnerId() != null &&
                        e.getValue().getOwnerId().equals(userId)
        );
    }

    public synchronized int removeLower(Organization reference) {
        if (reference == null) {
            throw new IllegalArgumentException("Reference organization cannot be null");
        }

        List<Integer> keysToRemove = collections.entrySet().stream()
                .filter(entry -> entry.getValue().compareTo(reference) < 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        keysToRemove.forEach(collections::remove);
        return keysToRemove.size();
    }

    public synchronized int removeLower(Organization reference, Long userId) {
        List<Integer> keysToRemove = collections.entrySet().stream()
                .filter(entry -> entry.getValue().compareTo(reference) < 0)
                .filter(entry -> entry.getValue().getOwnerId() != null &&
                        entry.getValue().getOwnerId().equals(userId))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        keysToRemove.forEach(collections::remove);
        return keysToRemove.size();
    }

    public Organization getMinByName() {
        return collections.values().stream()
                .min(Comparator.comparing(Organization::getName))
                .orElse(null);
    }

    public LinkedHashMap<Integer, Organization> getCollection() {
        return collections;
    }

    public String getInfoData() {
        return "Collection type: " + collections.getClass().getSimpleName() + "\n" +
                "Creation date: " + creationDate + "\n" +
                "Number of elements: " + collections.size();
    }

    public String getShowData() {
        if (collections.isEmpty()) {
            return "Collection is empty";
        }

        return collections.entrySet().stream()
                .map(entry -> "Key: " + entry.getKey() + " | " + entry.getValue())
                .collect(Collectors.joining("\n"));
    }

    public List<Organization> getFilteredGreaterThanType(OrganizationType referenceType) {
        if (referenceType == null) {
            throw new IllegalArgumentException("Reference type cannot be null");
        }

        return collections.values().stream()
                .filter(org -> org.getType() != null)
                .filter(org -> org.getType().ordinal() > referenceType.ordinal())
                .collect(Collectors.toList());
    }

    public Set<Integer> getUniqueAnnualTurnovers() {
        return collections.values().stream()
                .map(Organization::getAnnualTurnover)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public synchronized void replaceAll(LinkedHashMap<Integer, Organization> newCollection) {
        collections.clear();

        if (newCollection != null) {
            collections.putAll(newCollection);
        }
    }
}