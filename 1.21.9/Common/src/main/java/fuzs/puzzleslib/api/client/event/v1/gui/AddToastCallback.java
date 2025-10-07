package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;

@FunctionalInterface
public interface AddToastCallback {
    EventInvoker<AddToastCallback> EVENT = EventInvoker.lookup(AddToastCallback.class);

    /**
     * Fires when a {@link Toast} is about to be queued in
     * {@link net.minecraft.client.gui.components.toasts.ToastManager#addToast(Toast)}.
     *
     * @param toastManager the toast manager
     * @param toast        the toast being queued
     * @return <ul>
     *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent the toast from being queued, it will never render</li>
     *         <li>{@link EventResult#PASS PASS} to allow queueing the toast normally</li>
     *         </ul>
     */
    EventResult onAddToast(ToastManager toastManager, Toast toast);
}
