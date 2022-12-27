package fuzs.puzzleslib.network.v2;

import com.google.common.collect.Sets;

import java.util.Set;

public interface NetworkHandlerRegistry extends NetworkHandler {

    /**
     * register a message that will be sent to clients
     *
     * @param clazz message class type
     * @param <T>   message implementation
     */
    <T extends Record & ClientboundMessage<T>> void registerClientbound(Class<?> clazz);

    /**
     * register a message that will be sent to servers
     *
     * @param clazz message class type
     * @param <T>   message implementation
     */
    <T extends Record & ServerboundMessage<T>> void registerServerbound(Class<?> clazz);

    abstract class Builder {
        protected final String modId;
        protected final Set<Class<?>> clientboundMessages = Sets.newHashSet();
        protected final Set<Class<?>> serverboundMessages = Sets.newHashSet();
        protected boolean clientAcceptsVanillaOrMissing;
        protected boolean serverAcceptsVanillaOrMissing;

        protected Builder(String modId) {
            this.modId = modId;
        }

        /**
         * register a message that will be sent to clients
         *
         * @param clazz message class type
         * @param <T>   message implementation
         */
        public <T extends Record & ClientboundMessage<T>> Builder registerClientbound(Class<T> clazz) {
            if (!this.clientboundMessages.add(clazz)) throw new IllegalStateException("Duplicate message of type %s".formatted(clazz));
            return this;
        }

        /**
         * register a message that will be sent to servers
         *
         * @param clazz message class type
         * @param <T>   message implementation
         */
        public <T extends Record & ServerboundMessage<T>> Builder registerServerbound(Class<T> clazz) {
            if (!this.serverboundMessages.add(clazz)) throw new IllegalStateException("Duplicate message of type %s".formatted(clazz));
            return this;
        }

        public Builder clientAcceptsVanillaOrMissing() {
            this.clientAcceptsVanillaOrMissing = true;
            return this;
        }

        public Builder serverAcceptsVanillaOrMissing() {
            this.serverAcceptsVanillaOrMissing = true;
            return this;
        }

        public NetworkHandler build() {
            NetworkHandlerRegistry networkHandler = this.getHandler();
            this.registerAll(networkHandler);
            return networkHandler;
        }

        public abstract NetworkHandlerRegistry getHandler();

        private void registerAll(NetworkHandlerRegistry networkHandler) {
            for (Class<?> message : this.clientboundMessages) {
                networkHandler.registerClientbound(message);
            }
            for (Class<?> message : this.serverboundMessages) {
                networkHandler.registerServerbound(message);
            }
        }
    }
}
