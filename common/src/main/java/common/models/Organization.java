package common.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Организация (элемент коллекции).
 */
public class Organization implements Comparable<Organization>, Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Coordinates coordinates;
    private Date creationDate;
    private int annualTurnover;
    private OrganizationType type;
    private Address officialAddress;

    private static long nextId = 1;

    /**
     * Обычный конструктор для создания нового объекта.
     */
    public Organization(
            String name,
            Coordinates coordinates,
            int annualTurnover,
            OrganizationType type,
            Address officialAddress
    ) {
        this(generateId(), name, coordinates, new Date(), annualTurnover, type, officialAddress);
    }

    /**
     * Конструктор для восстановления/обновления объекта с сохранением id и creationDate.
     */
    public Organization(
            Long id,
            String name,
            Coordinates coordinates,
            Date creationDate,
            int annualTurnover,
            OrganizationType type,
            Address officialAddress
    ) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Id must be greater than 0");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (coordinates == null) {
            throw new IllegalArgumentException("Coordinates cannot be null");
        }
        if (creationDate == null) {
            throw new IllegalArgumentException("Creation date cannot be null");
        }
        if (annualTurnover <= 0) {
            throw new IllegalArgumentException("Annual turnover must be greater than 0");
        }
        if (officialAddress == null) {
            throw new IllegalArgumentException("Official address cannot be null");
        }

        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.annualTurnover = annualTurnover;
        this.type = type;
        this.officialAddress = officialAddress;
    }

    private static long generateId() {
        return nextId++;
    }

    public static void setNextId(long id) {
        nextId = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public int getAnnualTurnover() {
        return annualTurnover;
    }

    public OrganizationType getType() {
        return type;
    }

    public Address getOfficialAddress() {
        return officialAddress;
    }

    @Override
    public int compareTo(Organization other) {
        return Integer.compare(this.annualTurnover, other.annualTurnover);
    }

    @Override
    public String toString() {
        return "Organization {" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", annualTurnover=" + annualTurnover +
                ", type=" + type +
                ", officialAddress=" + officialAddress +
                '}';
    }
}