package fuzs.puzzleslib.neoforge.mixin.client.accessor;

import net.minecraft.client.Options;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RegisterKeyMappingsEvent.class)
public interface RegisterKeyMappingsEventNeoForgeAccessor {
    @Accessor("options")
    Options puzzleslib$getOptions();
}
