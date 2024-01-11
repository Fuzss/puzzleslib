package fuzs.puzzleslib.mixin.client.accessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.searchtree.SearchRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftFabricAccessor {

    @Accessor
    SearchRegistry getSearchRegistry();
}
