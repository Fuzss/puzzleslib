package fuzs.puzzleslib.impl.client.event;

import fuzs.puzzleslib.api.client.event.v1.*;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

import static fuzs.puzzleslib.impl.event.ForgeEventInvokerRegistryImpl.INSTANCE;

public final class ForgeClientEventInvokers {

    public static void register() {
        INSTANCE.register(ClientTickEvents.Start.class, TickEvent.ClientTickEvent.class, (ClientTickEvents.Start callback, TickEvent.ClientTickEvent evt) -> {
            if (evt.phase == TickEvent.Phase.START) callback.onStartTick(Minecraft.getInstance());
        });
        INSTANCE.register(ClientTickEvents.End.class, TickEvent.ClientTickEvent.class, (ClientTickEvents.End callback, TickEvent.ClientTickEvent evt) -> {
            if (evt.phase == TickEvent.Phase.END) callback.onEndTick(Minecraft.getInstance());
        });
        INSTANCE.register(RenderGuiCallback.class, RenderGuiEvent.Post.class, (RenderGuiCallback callback, RenderGuiEvent.Post evt) -> {
            callback.onRenderGui(Minecraft.getInstance(), evt.getPoseStack(), evt.getPartialTick(), evt.getWindow().getGuiScaledWidth(), evt.getWindow().getGuiScaledHeight());
        });
        INSTANCE.register(ItemTooltipCallback.class, ItemTooltipEvent.class, (ItemTooltipCallback callback, ItemTooltipEvent evt) -> {
            callback.onItemTooltip(evt.getItemStack(), evt.getEntity(), evt.getToolTip(), evt.getFlags());
        });
        INSTANCE.register(RenderNameTagCallback.class, RenderNameTagEvent.class, (RenderNameTagCallback callback, RenderNameTagEvent evt) -> {
            DefaultedValue<Component> content = DefaultedValue.fromEvent(evt::setContent, evt::getContent, evt::getOriginalContent);
            EventResult result = callback.onRenderNameTag(evt.getEntity(), content, evt.getEntityRenderer(), evt.getPoseStack(), evt.getMultiBufferSource(), evt.getPackedLight(), evt.getPartialTick());
            if (result.isInterrupt()) evt.setResult(result.getAsBoolean() ? Event.Result.ALLOW : Event.Result.DENY);
        });
        INSTANCE.register(ContainerScreenEvents.Background.class, ContainerScreenEvent.Render.Background.class, (ContainerScreenEvents.Background callback, ContainerScreenEvent.Render.Background evt) -> {
            callback.onDrawBackground(evt.getContainerScreen(), evt.getPoseStack(), evt.getMouseX(), evt.getMouseY());
        });
        INSTANCE.register(ContainerScreenEvents.Foreground.class, ContainerScreenEvent.Render.Foreground.class, (ContainerScreenEvents.Foreground callback, ContainerScreenEvent.Render.Foreground evt) -> {
            callback.onDrawForeground(evt.getContainerScreen(), evt.getPoseStack(), evt.getMouseX(), evt.getMouseY());
        });
        INSTANCE.register(InventoryMobEffectsCallback.class, ScreenEvent.RenderInventoryMobEffects.class, (InventoryMobEffectsCallback callback, ScreenEvent.RenderInventoryMobEffects evt) -> {
            MutableBoolean fullSizeRendering = MutableBoolean.fromEvent(evt::setCompact, evt::isCompact);
            MutableInt horizontalOffset = MutableInt.fromEvent(evt::setHorizontalOffset, evt::getHorizontalOffset);
            EventResult result = callback.onInventoryMobEffects(evt.getScreen(), evt.getAvailableSpace(), fullSizeRendering, horizontalOffset);
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(ScreenOpeningCallback.class, ScreenEvent.Opening.class, (ScreenOpeningCallback callback, ScreenEvent.Opening evt) -> {
            DefaultedValue<Screen> newScreen = DefaultedValue.fromEvent(evt::setNewScreen, evt::getNewScreen, evt::getScreen);
            EventResult result = callback.onScreenOpening(evt.getCurrentScreen(), newScreen);
            // setting current screen again already prevents Screen#remove from running as implemented by Forge, but Screen#init still runs again,
            // we just manually fully cancel the event to deal in a more 'proper' way with this, the same is implemented on Fabric
            if (result.isInterrupt() || newScreen.getAsOptional().filter(screen -> screen == evt.getCurrentScreen()).isPresent()) evt.setCanceled(true);
        });
        INSTANCE.register(ComputeFovModifierCallback.class, ComputeFovModifierEvent.class, (ComputeFovModifierCallback callback, ComputeFovModifierEvent evt) -> {
            final float fovEffectScale = Minecraft.getInstance().options.fovEffectScale().get().floatValue();
            // reverse fovEffectScale calculations applied by vanilla in return statement
            float fieldOfViewModifierValue = evt.getNewFovModifier() + 1.0F - 1.0F / fovEffectScale;
            MutableFloat fieldOfViewModifier$Internal = MutableFloat.fromValue(fieldOfViewModifierValue);
            // this approach is chosen so the callback may work with the actual fov modifier, and does not have to deal with the fovEffectScale option
            DefaultedFloat fieldOfViewModifier = DefaultedFloat.fromEvent(fieldOfViewModifier$Internal::accept, fieldOfViewModifier$Internal::getAsFloat, evt::getFovModifier);
            callback.onComputeFovModifier(evt.getPlayer(), fieldOfViewModifier);
            fieldOfViewModifier.getAsOptionalFloat().filter(value -> value != fieldOfViewModifierValue).map(value -> Mth.lerp(fovEffectScale, 1.0F, value)).ifPresent(evt::setNewFovModifier);
        });
        INSTANCE.register(ScreenEvents.BeforeInit.class, ScreenEvent.Init.Pre.class, (ScreenEvents.BeforeInit callback, ScreenEvent.Init.Pre evt) -> {
            List<AbstractWidget> widgets = evt.getListenersList().stream().filter(listener -> listener instanceof AbstractWidget).map(listener -> (AbstractWidget) listener).toList();
            callback.onBeforeInit(evt.getScreen().getMinecraft(), evt.getScreen(), evt.getScreen().width, evt.getScreen().height, widgets);
        });
        INSTANCE.register(ScreenEvents.AfterInit.class, ScreenEvent.Init.Post.class, (ScreenEvents.AfterInit callback, ScreenEvent.Init.Post evt) -> {
            List<AbstractWidget> widgets = evt.getListenersList().stream().filter(listener -> listener instanceof AbstractWidget).map(listener -> (AbstractWidget) listener).toList();
            callback.onAfterInit(evt.getScreen().getMinecraft(), evt.getScreen(), evt.getScreen().width, evt.getScreen().height, widgets, evt::addListener, evt::removeListener);
        });
        // TODO implement this properly
//        INSTANCE.register(MouseScreenEvents.BeforeMouseScroll.class, ScreenEvent.MouseScrolled.Pre.class, (callback, evt) -> {
//            EventResult result = callback.onBeforeMouseScroll(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getScrollDelta(), evt.getScrollDelta());
//            if (result.isInterrupt()) evt.setCanceled(true);
//        }, (Object context, Runnable applyToInvoker, Runnable removeInvoker) -> {
//            MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, ScreenEvent.Init.Pre.class, (final ScreenEvent.Init.Pre evt) -> {
//                if (((Class<?>) context).isInstance(evt.getScreen())) {
//                    applyToInvoker.run();
//                    // lowest priority since screen closing / remove callback is also implemented this way and won't run if this is called before
////                    MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, ScreenEvent.Init.Pre.class, (final ScreenEvent.Closing evt1) -> {
////                        if (evt.getScreen() == evt1.getScreen()) {
////                            removeInvoker.run();
//////                            MinecraftForge.EVENT_BUS.unregister();
////                        }
////                    });
//                }
//            });
//        });
        INSTANCE.register(CustomizeChatPanelCallback.class, CustomizeGuiOverlayEvent.Chat.class, (CustomizeChatPanelCallback callback, CustomizeGuiOverlayEvent.Chat evt) -> {
            MutableInt posX = MutableInt.fromEvent(evt::setPosX, evt::getPosX);
            MutableInt posY = MutableInt.fromEvent(evt::setPosY, evt::getPosY);
            callback.onRenderChatPanel(evt.getWindow(), evt.getPoseStack(), evt.getPartialTick(), posX, posY);
        });
        INSTANCE.register(ClientEntityLevelEvents.Load.class, EntityJoinLevelEvent.class, (ClientEntityLevelEvents.Load callback, EntityJoinLevelEvent evt) -> {
            if (!evt.getLevel().isClientSide) return;
            if (callback.onLoad(evt.getEntity(), (ClientLevel) evt.getLevel()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(ClientEntityLevelEvents.Unload.class, EntityLeaveLevelEvent.class, (ClientEntityLevelEvents.Unload callback, EntityLeaveLevelEvent evt) -> {
            if (!evt.getLevel().isClientSide) return;
            callback.onUnload(evt.getEntity(), (ClientLevel) evt.getLevel());
        });
    }
}
