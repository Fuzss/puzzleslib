package fuzs.puzzleslib.neoforge.impl.config;

import fuzs.puzzleslib.neoforge.api.core.v1.ModContainerHelper;
import fuzs.puzzleslib.neoforge.impl.config.core.ForgeModConfig;
import fuzs.puzzleslib.impl.config.ConfigDataHolderImpl;
import fuzs.puzzleslib.impl.config.ConfigHolderImpl;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.Optional;
import java.util.function.UnaryOperator;

public class ForgeConfigHolderImpl extends ConfigHolderImpl {

    public ForgeConfigHolderImpl(String modId) {
        super(modId);
    }

    @Override
    void bake(ConfigDataHolderImpl<?> holder, String modId) {
        Optional<IEventBus> optional = ModContainerHelper.getOptionalModEventBus(modId);
        optional.ifPresent(eventBus -> eventBus.addListener((final ModConfigEvent.Loading evt) -> {
            holder.onModConfig(evt.getConfig(), false);
        }));
        optional.ifPresent(eventBus -> eventBus.addListener((final ModConfigEvent.Reloading evt) -> {
            holder.onModConfig(evt.getConfig(), true);
        }));
        holder.register((ModConfig.Type type, ForgeConfigSpec spec, UnaryOperator<String> fileName) -> {
            ModContainer modContainer = ModContainerHelper.getModContainer(modId);
            ModConfig modConfig = new ForgeModConfig(type, spec, modContainer, fileName.apply(modId));
            modContainer.addConfig(modConfig);
            return modConfig;
        });
    }
}
