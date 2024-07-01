package fuzs.puzzleslib.forge.mixin.accessor;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(MobSpawnSettings.Builder.class)
public interface MobSpawnSettingsBuilderForgeAccessor {

    @Accessor("mobSpawnCosts")
    Map<EntityType<?>, MobSpawnSettings.MobSpawnCost> puzzleslib$getMobSpawnCosts();
}
