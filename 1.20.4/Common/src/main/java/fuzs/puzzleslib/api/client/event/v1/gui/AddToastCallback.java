package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;

@FunctionalInterface
public interface AddToastCallback {
    EventInvoker<AddToastCallback> EVENT = EventInvoker.lookup(AddToastCallback.class);

    /**
     * Fires when a {@link Toast} is about to be queued in {@link net.minecraft.client.gui.components.toasts.ToastComponent#addToast(Toast)}.
     *
     * @param toastManager the {@link ToastComponent} instance
     * @param toast        the toast instance being queued
     * @return {@link EventResult#INTERRUPT} to prevent the toast from being queued, it will never render,
     * {@link EventResult#PASS} to allow queueing the toast normally
     */
    EventResult onAddToast(ToastComponent toastManager, Toast toast);
}
