package fuzs.puzzleslib.element.side;

import fuzs.puzzleslib.config.ConfigManager;
import fuzs.puzzleslib.config.option.OptionsBuilder;
import fuzs.puzzleslib.element.AbstractElement;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * don't implement this directly, only sub-interfaces, multiple versions can be implemented
 */
public interface ISidedElement {

    static void setup(ISidedElement element) {

        runForSides(element, ICommonElement::setupCommon, IClientElement::setupClient, IServerElement::setupServer);
    }

    /**
     * initialize sided content, this will always happen, even when the element is not loaded
     * @param element this element
     * @param evt setup event this is called from
     */
    static void loadSide(ISidedElement element, ParallelDispatchEvent evt) {

        if (element instanceof ICommonElement && evt instanceof FMLCommonSetupEvent) {

            ((ICommonElement) element).loadCommon();
        } else if (element instanceof IClientElement && evt instanceof FMLClientSetupEvent) {

            ((IClientElement) element).loadClient();
        } else if (element instanceof IServerElement && evt instanceof FMLDedicatedServerSetupEvent) {

            ((IServerElement) element).loadServer();
        }
    }

    /**
     * run code depending on element side type
     * @param element this element
     * @param common consumer if implements {@link ICommonElement}
     * @param client consumer if implements {@link IClientElement}
     * @param server consumer if implements {@link IServerElement}
     */
    static void runForSides(ISidedElement element, Consumer<ICommonElement> common, Consumer<IClientElement> client, Consumer<IServerElement> server) {

        if (element instanceof ICommonElement) {

            common.accept(((ICommonElement) element));
        }

        if (FMLEnvironment.dist.isClient() && element instanceof IClientElement) {

            client.accept(((IClientElement) element));
        }

        if (FMLEnvironment.dist.isDedicatedServer() && element instanceof IServerElement) {

            server.accept(((IServerElement) element));
        }
    }

    static Predicate<Object> getGeneralFilter(ModConfig.Type type) {

        switch (type) {

            case COMMON:

                return element -> element instanceof ICommonElement;
            case CLIENT:

                return element -> !(element instanceof ICommonElement) && element instanceof IClientElement;
            case SERVER:

                return element -> !(element instanceof ICommonElement) && element instanceof IServerElement;
        }

        throw new IllegalStateException();
    }

    static void setupConfig(OptionsBuilder optionsBuilder, ModConfig.Type type, AbstractElement element) {

        switch (type) {

            case COMMON:

                if (element instanceof ICommonElement) {

                    ConfigManager.create(optionsBuilder, element, ((ICommonElement) element)::setupCommonConfig, ((ICommonElement) element).getCommonDescription());
                }

                break;
            case CLIENT:

                if (element instanceof IClientElement) {

                    ConfigManager.create(optionsBuilder, element, ((IClientElement) element)::setupClientConfig, ((IClientElement) element).getClientDescription());
                }

                break;
            case SERVER:

                if (element instanceof IServerElement) {

                    ConfigManager.create(optionsBuilder, element, ((IServerElement) element)::setupServerConfig, ((IServerElement) element).getServerDescription());
                }

                break;
        }
    }

}
