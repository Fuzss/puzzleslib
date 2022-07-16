package fuzs.puzzleslib.core;

import fuzs.puzzleslib.PuzzlesLib;
import fuzs.puzzleslib.client.core.ClientRegistration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Set;

public interface ContainsModEvents {

    Set<IEventBus> getModEventBuses();

    /**
     * register this singleton instance to the provided mod event bus in case we haven't done so yet
     * call this in every base method inherited from {@link ClientRegistration}
     */
    default void registerModEventBus() {
        if (this.getModEventBuses().add(FMLJavaModLoadingContext.get().getModEventBus())) {
            FMLJavaModLoadingContext.get().getModEventBus().register(this);
            PuzzlesLib.LOGGER.info("Added listener to client registration of mod {}", ModLoadingContext.get().getActiveNamespace());
        }
    }
}
