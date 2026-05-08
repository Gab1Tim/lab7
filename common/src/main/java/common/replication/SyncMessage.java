package common.replication;

import common.models.Organization;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class SyncMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private final LinkedHashMap<Integer, Organization> collection;

    public SyncMessage(LinkedHashMap<Integer, Organization> collection) {
        this.collection = collection;
    }

    public LinkedHashMap<Integer, Organization> getCollection() {
        return collection;
    }
}