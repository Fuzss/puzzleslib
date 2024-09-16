package fuzs.puzzleslib.fabric.impl.capability.data;

import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.LevelCapabilityKey;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class FabricLevelCapabilityKey<C extends CapabilityComponent<Level>> extends FabricCapabilityKey<Level, C> implements LevelCapabilityKey<C> {

    public FabricLevelCapabilityKey(Supplier<AttachmentType<C>> attachmentType, Predicate<Object> filter, Supplier<C> factory) {
        super(attachmentType, filter, factory);
    }
}
