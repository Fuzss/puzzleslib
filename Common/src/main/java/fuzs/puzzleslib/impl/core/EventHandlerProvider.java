package fuzs.puzzleslib.impl.core;

public interface EventHandlerProvider {

    void registerProvidedEventHandlers();

    static void tryRegister(Object o) {
        if (o instanceof EventHandlerProvider provider) {
            provider.registerProvidedEventHandlers();
        }
    }
}
