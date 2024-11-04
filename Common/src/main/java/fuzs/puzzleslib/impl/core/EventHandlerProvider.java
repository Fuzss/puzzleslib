package fuzs.puzzleslib.impl.core;

public interface EventHandlerProvider {

    void registerEventHandlers();

    static void tryRegister(Object o) {
        if (o instanceof EventHandlerProvider provider) {
            provider.registerEventHandlers();
        }
    }
}
