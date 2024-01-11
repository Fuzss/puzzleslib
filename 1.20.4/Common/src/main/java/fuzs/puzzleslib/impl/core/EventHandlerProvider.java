package fuzs.puzzleslib.impl.core;

public interface EventHandlerProvider {

    void registerHandlers();

    static void tryRegister(Object o) {
        if (o instanceof EventHandlerProvider provider) {
            provider.registerHandlers();
        }
    }
}
