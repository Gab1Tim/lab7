package server.replication;

import common.models.Organization;
import common.replication.SyncMessage;
import server.managers.CollectionManager;

import java.util.LinkedHashMap;

public class SlaveSyncService {

    private final CollectionManager collectionManager;

    public SlaveSyncService(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public void applySync(SyncMessage syncMessage) {
        if (syncMessage == null) {
            throw new IllegalArgumentException("Sync message cannot be null");
        }

        LinkedHashMap<Integer, Organization> incoming = syncMessage.getCollection();
        collectionManager.replaceAll(incoming);
    }
}