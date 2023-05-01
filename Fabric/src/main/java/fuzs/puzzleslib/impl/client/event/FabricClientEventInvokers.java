package fuzs.puzzleslib.impl.client.event;

import com.mojang.blaze3d.platform.Window;
import fuzs.puzzleslib.api.client.event.v1.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static fuzs.puzzleslib.impl.event.FabricEventInvokerRegistryImpl.INSTANCE;

public final class FabricClientEventInvokers {

    public static void register() {
        INSTANCE.register(ClientTickEvents.Start.class, net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.START_CLIENT_TICK, callback -> {
            return callback::onStartTick;
        });
        INSTANCE.register(ClientTickEvents.End.class, net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK, callback -> {
            return callback::onEndTick;
        });
        INSTANCE.register(RenderGuiCallback.class, HudRenderCallback.EVENT, callback -> {
            return (matrixStack, tickDelta) -> {
                Minecraft minecraft = Minecraft.getInstance();
                Window window = minecraft.getWindow();
                callback.onRenderGui(minecraft, matrixStack, tickDelta, window.getGuiScaledWidth(), window.getGuiScaledHeight());
            };
        });
        INSTANCE.register(ItemTooltipCallback.class, net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback.EVENT, callback -> {
            return (stack, context, lines) -> callback.onItemTooltip(stack, Minecraft.getInstance().player, lines, context);
        });
        INSTANCE.register(RenderNameTagCallback.class, FabricClientEvents.RENDER_NAME_TAG);
        INSTANCE.register(ContainerScreenEvents.Background.class, FabricScreenEvents.CONTAINER_SCREEN_BACKGROUND);
        INSTANCE.register(ContainerScreenEvents.Foreground.class, FabricScreenEvents.CONTAINER_SCREEN_FOREGROUND);
        INSTANCE.register(InventoryMobEffectsCallback.class, FabricScreenEvents.INVENTORY_MOB_EFFECTS);
        INSTANCE.register(ScreenOpeningCallback.class, FabricScreenEvents.SCREEN_OPENING);
        INSTANCE.register(ComputeFovModifierCallback.class, FabricClientEvents.COMPUTE_FOV_MODIFIER);
        INSTANCE.register(ScreenEvents.BeforeInit.class, net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.BEFORE_INIT, callback -> {
            return (client, screen, scaledWidth, scaledHeight) -> {
                callback.onBeforeInit(client, screen, scaledWidth, scaledHeight, Collections.unmodifiableList(Screens.getButtons(screen)));
            };
        });
        INSTANCE.register(ScreenEvents.AfterInit.class, net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AFTER_INIT, callback -> {
            return (client, screen, scaledWidth, scaledHeight) -> {
                List<AbstractWidget> widgets = Screens.getButtons(screen);
                callback.onAfterInit(client, screen, scaledWidth, scaledHeight, Collections.unmodifiableList(widgets), widgets::add, widgets::remove);
            };
        });
        // TODO expand screen events
        registerScreenEvent(MouseScreenEvents.BeforeMouseScroll.class, ScreenMouseEvents.AllowMouseScroll.class, callback -> {
            return (screen, mouseX, mouseY, horizontalAmount, verticalAmount) -> {
                return callback.onBeforeMouseScroll(screen, mouseX, mouseY, horizontalAmount, verticalAmount).isPass();
            };
        }, ScreenMouseEvents::allowMouseScroll);
        registerScreenEvent(MouseScreenEvents.AfterMouseScroll.class, ScreenMouseEvents.AfterMouseScroll.class, callback -> {
            return callback::onAfterMouseScroll;
        }, ScreenMouseEvents::afterMouseScroll);
        INSTANCE.register(RenderGuiElementEvents.Before.class, (context, applyToInvoker, removeInvoker) -> {
            applyToInvoker.accept(FabricClientEvents.beforeRenderGuiElement((ResourceLocation) context));
        });
        INSTANCE.register(RenderGuiElementEvents.After.class, (context, applyToInvoker, removeInvoker) -> {
            applyToInvoker.accept(FabricClientEvents.afterRenderGuiElement((ResourceLocation) context));
        });
        INSTANCE.register(CustomizeChatPanelCallback.class, FabricClientEvents.CUSTOMIZE_CHAT_PANEL);
        INSTANCE.register(ClientEntityLevelEvents.Load.class, ClientEntityEvents.ENTITY_LOAD, callback -> {
            return (Entity entity, ClientLevel world) -> {
                if (callback.onLoad(entity, world).isInterrupt()) {
                    entity.setRemoved(Entity.RemovalReason.DISCARDED);
                }
            };
        });
        INSTANCE.register(ClientEntityLevelEvents.Unload.class, ClientEntityEvents.ENTITY_UNLOAD, callback -> {
            return callback::onUnload;
        });
    }

    private static <T, E> void registerScreenEvent(Class<T> clazz, Class<E> eventType, Function<T, E> converter, Function<Screen, Event<E>> eventGetter) {
        INSTANCE.register(clazz, eventType, converter, (context, applyToInvoker, removeInvoker) -> {
            net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
                if (((Class<?>) context).isInstance(screen)) {
                    Event<E> event = eventGetter.apply(screen);
                    applyToInvoker.accept(event);
                    // TODO use LAST event phase
                    net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.remove(screen).register($ -> {
                        removeInvoker.accept(event);
                    });
                }
            });
        });
    }
}
