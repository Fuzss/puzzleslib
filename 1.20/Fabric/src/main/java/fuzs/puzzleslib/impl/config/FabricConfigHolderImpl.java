package fuzs.puzzleslib.impl.config;

import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;
import fuzs.puzzleslib.impl.config.core.FabricModConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.function.UnaryOperator;

public class FabricConfigHolderImpl extends ConfigHolderImpl {

    public FabricConfigHolderImpl(String modId) {
        super(modId);
    }

    @Override
    void bake(ConfigDataHolderImpl<?> holder, String modId) {
        ModConfigEvents.loading(modId).register((ModConfig config) -> {
            holder.onModConfig(config, false);
        });
        ModConfigEvents.reloading(modId).register((ModConfig config) -> {
            holder.onModConfig(config, true);
        });
        holder.register((ModConfig.Type type, ForgeConfigSpec spec, UnaryOperator<String> fileName) -> {
            return new FabricModConfig(type, spec, modId, fileName.apply(modId));
        });
    }
}
