package fuzs.puzzleslib.neoforge.impl.capability.data;

import fuzs.puzzleslib.api.capability.v2.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v2.data.LevelCapabilityKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Predicate;

public class NeoForgeLevelCapabilityKey<C extends CapabilityComponent<Level>> extends NeoForgeCapabilityKey<Level, C> implements LevelCapabilityKey<C> {

    public NeoForgeLevelCapabilityKey(DeferredHolder<AttachmentType<?>, AttachmentType<C>> holder, Predicate<Object> filter) {
        super(holder, filter);
    }
}
