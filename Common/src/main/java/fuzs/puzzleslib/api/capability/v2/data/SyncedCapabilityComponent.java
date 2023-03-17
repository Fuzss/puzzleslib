package fuzs.puzzleslib.api.capability.v2.data;

/**
 * special capability component that can be synced to the remote
 */
public interface SyncedCapabilityComponent extends CapabilityComponent {

    /**
     * @return  does this capability need to synced
     */
    boolean isDirty();

    /**
     * capability data changed and requires syncing
     */
    void markDirty();

    /**
     * the capability has been synced and is equal on both server and remote
     */
    void markClean();
}
