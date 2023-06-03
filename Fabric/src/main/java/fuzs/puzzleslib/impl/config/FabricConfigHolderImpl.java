package fuzs.puzzleslib.impl.config;

import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.api.fml.event.config.ModConfigEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.function.UnaryOperator;

public class FabricConfigHolderImpl extends ConfigHolderImpl {

    public FabricConfigHolderImpl(String modId) {
        super(modId);
    }

    @Override
    void bake(ConfigDataHolderImpl<?> holder, String modId) {
        ModConfigEvent.LOADING.register((ModConfig config) -> {
            if (!config.getModId().equals(modId)) return;
            holder.onModConfig(config, false);
        });
        ModConfigEvent.RELOADING.register((ModConfig config) -> {
            if (!config.getModId().equals(modId)) return;
            holder.onModConfig(config, true);
        });
        holder.register((ModConfig.Type type, ForgeConfigSpec spec, UnaryOperator<String> fileName) -> {
            return ModLoadingContext.registerConfig(modId, type, spec, fileName.apply(modId));
        });
    }
}
