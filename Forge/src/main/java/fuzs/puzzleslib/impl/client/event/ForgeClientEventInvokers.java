package fuzs.puzzleslib.impl.client.event;

import fuzs.puzzleslib.api.client.event.v1.ClientTickEvents;
import fuzs.puzzleslib.api.client.event.v1.RenderGuiCallback;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent;

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
    }
}
