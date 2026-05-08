package common.network;

import common.models.Organization;
import common.models.OrganizationType;
import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private CommandType commandType;
    private Integer key;
    private Long id;
    private Organization organization;
    private OrganizationType organizationType;

    public Request(CommandType commandType) {
        this.commandType = commandType;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public OrganizationType getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(OrganizationType organizationType) {
        this.organizationType = organizationType;
    }
}