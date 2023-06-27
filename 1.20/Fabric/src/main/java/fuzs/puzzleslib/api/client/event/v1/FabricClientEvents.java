package fuzs.puzzleslib.api.client.event.v1;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.event.v1.core.FabricEventFactory;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Map;
import java.util.Objects;

/**
 * Events originally found on Forge in the <code>net.minecraftforge.client.event</code> package.
 */
public final class FabricClientEvents {
    private static final Map<String, Event<RenderGuiElementEvents.Before>> BEFORE_RENDER_GUI_ELEMENT_EVENTS = Maps.newIdentityHashMap();
    private static final Map<String, Event<RenderGuiElementEvents.After>> AFTER_RENDER_GUI_ELEMENT_EVENTS = Maps.newIdentityHashMap();
    /**
     * Fires before the name tag of an entity is tried to be rendered, in addition to preventing the name tag from rendering, rendering can also be forced.
     */
    public static final Event<RenderNameTagCallback> RENDER_NAME_TAG = FabricEventFactory.createResult(RenderNameTagCallback.class);
    /**
     * Called when computing the field of view modifier on the client, mostly depending on {@link Attributes#MOVEMENT_SPEED},
     * but also changes for certain actions such as when drawing a bow.
     */
    public static final Event<ComputeFovModifierCallback> COMPUTE_FOV_MODIFIER = FabricEventFactory.create(ComputeFovModifierCallback.class);
    /**
     * Called before the chat panel is drawn, allows for changing x- and y-position.
     */
    public static final Event<CustomizeChatPanelCallback> CUSTOMIZE_CHAT_PANEL = FabricEventFactory.create(CustomizeChatPanelCallback.class);
    /**
     * Called before a mouse button is clicked or released without a screen being open.
     */
    public static final Event<InputEvents.BeforeMouseAction> BEFORE_MOUSE_ACTION = FabricEventFactory.createResult(InputEvents.BeforeMouseAction.class);
    /**
     * Called after a mouse button is clicked or released without a screen being open.
     */
    public static final Event<InputEvents.AfterMouseAction> AFTER_MOUSE_ACTION = FabricEventFactory.create(InputEvents.AfterMouseAction.class);
    /**
     * Called before a mouse has scrolled without a screen being open.
     */
    public static final Event<InputEvents.BeforeMouseScroll> BEFORE_MOUSE_SCROLL = FabricEventFactory.createResult(InputEvents.BeforeMouseScroll.class);
    /**
     * Called after a mouse has scrolled without a screen being open.
     */
    public static final Event<InputEvents.AfterMouseScroll> AFTER_MOUSE_SCROLL = FabricEventFactory.create(InputEvents.AfterMouseScroll.class);
    /**
     * Called before a key press, release or repeat action is handled.
     */
    public static final Event<InputEvents.BeforeKeyAction> BEFORE_KEY_ACTION = FabricEventFactory.createResult(InputEvents.BeforeKeyAction.class);
    /**
     * Called after a key press, release or repeat action is handled.
     */
    public static final Event<InputEvents.AfterKeyAction> AFTER_KEY_ACTION = FabricEventFactory.create(InputEvents.AfterKeyAction.class);
    /**
     * Called before the player model is rendered, allows for applying transformations to the {@link PoseStack}, or for completely taking over rendering as a whole.
     */
    public static final Event<RenderPlayerEvents.Before> BEFORE_RENDER_PLAYER = FabricEventFactory.createResult(RenderPlayerEvents.Before.class);
    /**
     * Called after the player model is rendered, allows for cleaning up trnasofmrations applied to the {@link PoseStack}.
     */
    public static final Event<RenderPlayerEvents.After> AFTER_RENDER_PLAYER = FabricEventFactory.create(RenderPlayerEvents.After.class);
    /**
     * Called before one of the player's hands is rendered in first-person mode.
     */
    public static final Event<RenderHandCallback> RENDER_HAND = FabricEventFactory.createResult(RenderHandCallback.class);
    /**
     * Runs before camera angle setup is done, allows for additional control over roll (which vanilla itself does not support) in addition to pitch and yaw.
     */
    public static final Event<ComputeCameraAnglesCallback> COMPUTE_CAMERA_ANGLES = FabricEventFactory.create(ComputeCameraAnglesCallback.class);
    /**
     * Called when a player joins a server, the player is already initialized.
     */
    public static final Event<ClientPlayerEvents.LoggedIn> PLAYER_LOGGED_IN = FabricEventFactory.create(ClientPlayerEvents.LoggedIn.class);
    /**
     * Called whenever {@link Minecraft#clearLevel()} is called, which is the case when a player disconnects from a server,
     * but also occurs before joining a new single player level or server.
     */
    public static final Event<ClientPlayerEvents.LoggedOut> PLAYER_LOGGED_OUT = FabricEventFactory.create(ClientPlayerEvents.LoggedOut.class);
    /**
     * Called when the local player is replaced from respawning.
     */
    public static final Event<ClientPlayerEvents.Copy> PLAYER_COPY = FabricEventFactory.create(ClientPlayerEvents.Copy.class);
    /**
     * Fires before a client level is loaded.
     */
    public static final Event<ClientLevelEvents.Load> LOAD_LEVEL = FabricEventFactory.create(ClientLevelEvents.Load.class);
    /**
     * Fires before a client level is unloaded.
     */
    public static final Event<ClientLevelEvents.Unload> UNLOAD_LEVEL = FabricEventFactory.create(ClientLevelEvents.Unload.class);
    /**
     * Called after {@link Input#tick(boolean, float)} has run for the {@link LocalPlayer}.
     */
    public static final Event<MovementInputUpdateCallback> MOVEMENT_INPUT_UPDATE = FabricEventFactory.create(MovementInputUpdateCallback.class);

    /**
     * Called before a gui element is rendered, allows for cancelling rendering.
     *
     * @param id id of the gui element, all vanilla ids can be found in {@link RenderGuiElementEvents}
     * @return the event instance
     */
    public static Event<RenderGuiElementEvents.Before> beforeRenderGuiElement(ResourceLocation id) {
        Objects.requireNonNull(id, "id is null");
        return BEFORE_RENDER_GUI_ELEMENT_EVENTS.computeIfAbsent(id.toString().intern(), $ -> FabricEventFactory.createResult(RenderGuiElementEvents.Before.class));
    }

    /**
     * Called after a gui element is rendered.
     *
     * @param id id of the gui element, all vanilla ids can be found in {@link RenderGuiElementEvents}
     * @return the event instance
     */
    public static Event<RenderGuiElementEvents.After> afterRenderGuiElement(ResourceLocation id) {
        Objects.requireNonNull(id, "id is null");
        return AFTER_RENDER_GUI_ELEMENT_EVENTS.computeIfAbsent(id.toString().intern(), $ -> FabricEventFactory.create(RenderGuiElementEvents.After.class));
    }
}
