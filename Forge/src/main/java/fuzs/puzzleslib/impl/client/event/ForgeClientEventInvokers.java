package fuzs.puzzleslib.impl.client.event;

import fuzs.puzzleslib.api.client.event.v1.ClientTickEvents;
import fuzs.puzzleslib.api.client.event.v1.ItemTooltipCallback;
import fuzs.puzzleslib.api.client.event.v1.RenderGuiCallback;
import fuzs.puzzleslib.api.client.event.v1.RenderNameTagCallback;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.Event;

import static fuzs.puzzleslib.impl.event.ForgeEventInvokerRegistryImpl.INSTANCE;

public class ForgeClientEventInvokers {

    public static void register() {
        INSTANCE.register(ClientTickEvents.Start.class, TickEvent.ClientTickEvent.class, (ClientTickEvents.Start callback, TickEvent.ClientTickEvent evt) -> {
            if (evt.phase != TickEvent.Phase.START) return;
            callback.onStartTick(Minecraft.getInstance());
        });
        INSTANCE.register(ClientTickEvents.End.class, TickEvent.ClientTickEvent.class, (ClientTickEvents.End callback, TickEvent.ClientTickEvent evt) -> {
            if (evt.phase != TickEvent.Phase.END) return;
            callback.onEndTick(Minecraft.getInstance());
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
            if (result.isInterrupt()) {
                evt.setResult(result.getAsBoolean() ? Event.Result.ALLOW : Event.Result.DENY);
            }
        });
    }
}
