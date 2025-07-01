package fuzs.puzzleslib.fabric.api.client.gui.v2;

import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * A registry for {@link ToIntFunction ToIntFunction&lt;Player&gt;} instances, known as height
 * providers. These providers define the vertical space occupied by HUD elements, known as status bars, which are
 * positioned on the left and right sides above the player's hotbar.
 *
 * <p>Registering a height provider allows the game to automatically adjust the layout of existing
 * HUD elements, including vanilla ones, to accommodate new bars without overlap. The system calculates the cumulative
 * height from registered providers on each side and shifts elements like health, armor, food, and air bars
 * accordingly.
 *
 * <p>Height providers are associated with a {@link ResourceLocation ResourceLocation}
 * identifier. The identifier must also be registered with a corresponding
 * {@link net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement} in
 * {@link net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry}. The relative positioning to other HUD
 * elements is determined from that registration. For instance, registering a height for
 * {@link net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements#ARMOR_BAR} via
 * {@link #addLeft(ResourceLocation, ToIntFunction)} implies the custom bar is on the left side and affects the
 * positioning of elements starting from the health bar downwards. The function itself should return the height of the
 * custom bar.
 *
 * <p>The final vertical offset for a HUD element is determined by summing the heights
 * of all custom providers registered for elements that would appear "below" it on the same side.
 *
 * <p>For vanilla HUD element identifiers, see
 * {@link net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements}.
 */
public interface HudStatusBarHeightRegistry {

    /**
     * Adds a height provider for a status bar on the left side above the hotbar.
     *
     * <p>The provided function should return the vertical space (height)
     * that the custom element associated with the given {@code id} occupies. This height contributes to the total
     * offset applied to elements positioned above it on the right side. Conditions implemented for the rendering of the
     * actual element must also be taken into account here; so when an element currently does not actually render
     * {@code 0} must be returned.
     *
     * <p>Vanilla height providers for this side are: {@link HudStatusBarHeightRegistryImpl#HEALTH_BAR},
     * {@link HudStatusBarHeightRegistryImpl#ARMOR_BAR}
     *
     * <p>Existing height providers (like vanilla) can be replaced to coincide with
     * {@link net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry#replaceElement(ResourceLocation,
     * Function)}.
     *
     * <p>Registration is frozen once the client has fully started.
     *
     * @param id             the {@link ResourceLocation} identifier; must be registered with a
     *                       corresponding {@link net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement} in
     *                       {@link net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry}.
     * @param heightProvider a {@link ToIntFunction} that takes a
     *                       {@link Player} from {@link Gui#getCameraPlayer()} and
     *                       returns the height.
     */
    static void addLeft(ResourceLocation id, ToIntFunction<Player> heightProvider) {
        Objects.requireNonNull(id, "id is null");
        Objects.requireNonNull(heightProvider, "height provider is null");
        HudStatusBarHeightRegistryImpl.addLeft(id, heightProvider);
    }

    /**
     * Adds a height provider for a status bar on the right side above the hotbar.
     *
     * <p>The provided function should return the vertical space (height)
     * that the custom element associated with the given {@code id} occupies. This height contributes to the total
     * offset applied to elements positioned above it on the right side. Conditions implemented for the rendering of the
     * actual element must also be taken into account here; so when an element currently does not actually render
     * {@code 0} must be returned.
     *
     * <p>Vanilla height providers for this side are: {@link HudStatusBarHeightRegistryImpl#MOUNT_HEALTH},
     * {@link HudStatusBarHeightRegistryImpl#FOOD_BAR}, {@link HudStatusBarHeightRegistryImpl#AIR_BAR}
     *
     * <p>Existing height providers (like vanilla) can be replaced to coincide with
     * {@link net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry#replaceElement(ResourceLocation,
     * Function)}.
     *
     * <p>Registration is frozen once the client has fully started.
     *
     * @param id             the {@link ResourceLocation} identifier; must be registered with a
     *                       corresponding {@link net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement} in *
     *                       {@link net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry}.
     * @param heightProvider a {@link ToIntFunction} that takes a
     *                       {@link Player} from {@link Gui#getCameraPlayer()} and
     *                       returns the height.
     */
    static void addRight(ResourceLocation id, ToIntFunction<Player> heightProvider) {
        Objects.requireNonNull(id, "id is null");
        Objects.requireNonNull(heightProvider, "height provider is null");
        HudStatusBarHeightRegistryImpl.addRight(id, heightProvider);
    }

    /**
     * Gets the total calculated height offset for a given HUD element ID.
     *
     * <p>This method is typically used by the rendering system to determine how much
     * to shift a HUD element. It returns the default HUD height which is {@code 39} plus the sum of all registered
     * provider heights that are considered "below" the position of the element associated with the given {@code id}.
     *
     * <p>Note: The registry must be initialized (frozen) before this method returns
     * meaningful values beyond a default. This initialization typically happens during the Minecraft client setup.
     *
     * @param id the {@link ResourceLocation} identifier of the HUD element.
     * @return the total height offset.
     */
    static int getHeight(ResourceLocation id) {
        Objects.requireNonNull(id, "id is null");
        return HudStatusBarHeightRegistryImpl.getHeight(id);
    }
}