package fuzs.puzzleslib.api.core.v1.context;

import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.ServerFunctionLibrary;
import net.minecraft.world.item.crafting.RecipeManager;

/**
 * Adds listeners to the server (data packs) resource manager to reload together with other resources.
 */
public interface DataPackReloadListenersContext extends ReloadListenersContext {
    /**
     * The {@link RecipeManager} reload listener.
     */
    ResourceLocation RECIPES = ResourceLocationHelper.withDefaultNamespace("recipes");
    /**
     * The {@link ServerFunctionLibrary} reload listener.
     */
    ResourceLocation FUNCTIONS = ResourceLocationHelper.withDefaultNamespace("functions");
    /**
     * The {@link ServerAdvancementManager} reload listener.
     */
    ResourceLocation ADVANCEMENTS = ResourceLocationHelper.withDefaultNamespace("advancements");

    /**
     * @return the server resources being reloaded
     */
    ReloadableServerResources getServerResources();

    /**
     * @return the registry context for the currently active reload
     */
    RegistryAccess getRegistryAccess();
}
