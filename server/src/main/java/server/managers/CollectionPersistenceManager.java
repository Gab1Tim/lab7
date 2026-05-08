package server.managers;

import com.google.gson.Gson;
import common.models.Organization;

import java.util.LinkedHashMap;
import java.util.Map;

public class CollectionPersistenceManager {
    private final server.managers.FileManager fileManager;
    private final WalManager walManager;
    private final Gson gson = new Gson();

    public CollectionPersistenceManager(String fileName) {
        this.fileManager = new FileManager(fileName);
        this.walManager = new WalManager(fileName + ".wal");
    }

    public LinkedHashMap<Integer, Organization> loadCollection() {
        LinkedHashMap<Integer, Organization> collections;

        if (fileManager.tempFileExists() && fileManager.isMainFileAccessible()) {
            System.out.println("Temp file and main file both found. Merging...");
            collections = fileManager.loadAndMerge();
            System.out.println("Merge complete.");
        } else if (fileManager.tempFileExists()) {
            System.out.println("Main file not found. Loading from temp file...");
            collections = fileManager.loadFromTemp();
        } else {
            collections = fileManager.load();
        }

        if (collections == null) {
            collections = new LinkedHashMap<>();
        }

        if (walManager.exists()) {
            System.out.println("WAL log found. Recovering...");
            replayWal(collections);
            System.out.println("Recovery complete.");
        }

        return collections;
    }

    private void replayWal(LinkedHashMap<Integer, Organization> collections) {
        for (String line : walManager.readLog()) {
            try {
                if (line.startsWith("INSERT ")) {
                    String[] parts = line.substring(7).split(" ", 2);
                    Integer key = Integer.parseInt(parts[0]);
                    Organization org = gson.fromJson(parts[1], Organization.class);

                    if (!collections.containsKey(key)) {
                        collections.put(key, org);
                    }

                } else if (line.startsWith("REMOVE ")) {
                    Integer key = Integer.parseInt(line.substring(7).trim());
                    collections.remove(key);

                } else if (line.startsWith("UPDATE ")) {
                    String[] parts = line.substring(7).split(" ", 2);
                    Long id = Long.parseLong(parts[0]);
                    Organization orgFromWal = gson.fromJson(parts[1], Organization.class);

                    Integer keyToUpdate = collections.entrySet().stream()
                            .filter(entry -> entry.getValue().getId().equals(id))
                            .map(Map.Entry::getKey)
                            .findFirst()
                            .orElse(null);

                    if (keyToUpdate != null) {
                        Organization oldOrg = collections.get(keyToUpdate);

                        Organization updatedOrg = new Organization(
                                oldOrg.getId(),
                                orgFromWal.getName(),
                                orgFromWal.getCoordinates(),
                                oldOrg.getCreationDate(),
                                orgFromWal.getAnnualTurnover(),
                                orgFromWal.getType(),
                                orgFromWal.getOfficialAddress()
                        );

                        collections.put(keyToUpdate, updatedOrg);
                    }

                } else if (line.equals("CLEAR")) {
                    collections.clear();

                } else if (line.startsWith("REMOVE_GREATER_KEY ")) {
                    Integer refKey = Integer.parseInt(line.substring(19).trim());
                    collections.entrySet().removeIf(e -> e.getKey() > refKey);

                } else if (line.startsWith("REMOVE_LOWER ")) {
                    Organization ref = gson.fromJson(line.substring(13), Organization.class);
                    collections.entrySet().removeIf(e -> e.getValue().compareTo(ref) < 0);
                }
            } catch (Exception e) {
                System.out.println("WAL replay error: " + line + " -> " + e.getMessage());
            }
        }
    }

    public void logInsert(Integer key, Organization organization) {
        walManager.log("INSERT " + key + " " + gson.toJson(organization));
    }

    public void logRemove(Integer key) {
        walManager.log("REMOVE " + key);
    }

    public void logClear() {
        walManager.log("CLEAR");
    }

    public void logUpdate(Long id, Organization organization) {
        walManager.log("UPDATE " + id + " " + gson.toJson(organization));
    }

    public void logRemoveGreaterKey(Integer referenceKey) {
        walManager.log("REMOVE_GREATER_KEY " + referenceKey);
    }

    public void logRemoveLower(Organization reference) {
        walManager.log("REMOVE_LOWER " + gson.toJson(reference));
    }

    public boolean saveToMain(LinkedHashMap<Integer, Organization> collections) {
        boolean saved = fileManager.saveToMain(collections);
        if (saved) {
            walManager.clear();
        }
        return saved;
    }

    public void saveToTemp(LinkedHashMap<Integer, Organization> collections) {
        fileManager.saveToTemp(collections);
    }

    public void saveSafely(LinkedHashMap<Integer, Organization> collections) {
        boolean saved = saveToMain(collections);
        if (!saved) {
            saveToTemp(collections);
        }
    }
}