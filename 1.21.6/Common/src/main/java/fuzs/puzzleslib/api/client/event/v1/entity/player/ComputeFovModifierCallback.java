package fuzs.puzzleslib.api.client.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public interface ComputeFovModifierCallback {
    EventInvoker<ComputeFovModifierCallback> EVENT = EventInvoker.lookup(ComputeFovModifierCallback.class);

    /**
     * Called when computing the field of view modifier on the client, mostly depending on
     * {@link Attributes#MOVEMENT_SPEED}, but also changes for certain actions such as when drawing a bow.
     * <p>
     * The modifier value is the actual raw value, effects from the {@code fovEffectScale} option are applied afterward
     * by the callback implementation.
     *
     * @param player              the client player this is calculated for
     * @param fieldOfViewModifier the modifier, as calculated by vanilla, can be modified
     */
    void onComputeFovModifier(Player player, MutableFloat fieldOfViewModifier);
}
