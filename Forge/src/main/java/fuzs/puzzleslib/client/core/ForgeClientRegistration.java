package fuzs.puzzleslib.client.core;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fuzs.puzzleslib.PuzzlesLib;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Forge implementation of {@link ClientRegistration}
 * content is collected first and then registered when the appropriate event is fired on the mod event bus
 * registration of this class to the mod event bus is done automatically whenever it is used by a mod
 */
public class ForgeClientRegistration implements ClientRegistration {
    /**
     * all the mod event buses this instance has been registered to,
     * it is important to not register more than once as the events will also run every time, resulting in duplicate content
     */
    private final Set<IEventBus> modEventBuses = Sets.newIdentityHashSet();
    /**
     * collected factories for building {@link ClientTooltipComponent} from {@link TooltipComponent}
     */
    private final Map<Class<? extends TooltipComponent>, Function<TooltipComponent, ClientTooltipComponent>> clientTooltipComponents = Maps.newHashMap();

    @Override
    public <T extends TooltipComponent> void registerClientTooltipComponent(Class<T> type, Function<? super T, ? extends ClientTooltipComponent> factory) {
        this.registerModEventBus();
        this.clientTooltipComponents.put(type, (Function<TooltipComponent, ClientTooltipComponent>) factory);
    }

    /**
     * register this singleton instance to the provided mod event bus in case we haven't done so yet
     * call this in every base method inherited from {@link ClientRegistration}
     */
    private void registerModEventBus() {
        if (this.modEventBuses.add(FMLJavaModLoadingContext.get().getModEventBus())) {
            FMLJavaModLoadingContext.get().getModEventBus().register(this);
            PuzzlesLib.LOGGER.info("Added listener to client registration of mod {}", ModLoadingContext.get().getActiveNamespace());
        }
    }

    @SubscribeEvent
    public void onRegisterClientTooltipComponentFactories(final RegisterClientTooltipComponentFactoriesEvent evt) {
        this.clientTooltipComponents.forEach(evt::register);
    }
}
