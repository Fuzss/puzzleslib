package fuzs.puzzleslib.api.event.v1.server;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;

import java.util.function.Consumer;

@FunctionalInterface
public interface RegisterConfigurationTasksCallback {
    EventInvoker<RegisterConfigurationTasksCallback> EVENT = EventInvoker.lookup(RegisterConfigurationTasksCallback.class);

    /**
     * Called on the server when a client is connecting before the configuration phase for setting up tasks to be run
     * during that phase.
     *
     * @param minecraftServer           the minecraft server
     * @param packetListener            the server configuration packet listener
     * @param configurationTaskConsumer the consumer for adding configuration tasks
     */
    void onRegisterConfigurationTasks(MinecraftServer minecraftServer, ServerConfigurationPacketListenerImpl packetListener, Consumer<ConfigurationTask> configurationTaskConsumer);
}
