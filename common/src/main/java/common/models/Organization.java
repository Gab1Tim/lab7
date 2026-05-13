package common.models;

import java.io.Serializable;
import java.util.Date;

public class Organization implements Comparable<Organization>, Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Coordinates coordinates;
    private Date creationDate;
    private int annualTurnover;
    private OrganizationType type;
    private Address officialAddress;
    private Long ownerId;

    public Organization(
            String name,
            Coordinates coordinates,
            int annualTurnover,
            OrganizationType type,
            Address officialAddress
    ) {
        this(null, name, coordinates, new Date(), annualTurnover, type, officialAddress, null);
    }

    public Organization(
            Long id,
            String name,
            Coordinates coordinates,
            Date creationDate,
            int annualTurnover,
            OrganizationType type,
            Address officialAddress,
            Long ownerId
    ) {
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
        this.ownerId = ownerId;
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

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
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
                ", ownerId=" + ownerId +
                '}';
    }
}