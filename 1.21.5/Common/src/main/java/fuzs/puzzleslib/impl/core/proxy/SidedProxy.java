package fuzs.puzzleslib.impl.core.proxy;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.Collections;
import java.util.List;

public interface SidedProxy {

    void registerLoadingHandlers();

    void registerEventHandlers();

    @Deprecated
    default Player getClientPlayer() {
        throw new RuntimeException("Client player accessed for wrong side!");
    }

    @Deprecated
    default Level getClientLevel() {
        throw new RuntimeException("Client level accessed for wrong side!");
    }

    default boolean hasControlDown() {
        return false;
    }

    default boolean hasShiftDown() {
        return false;
    }

    default boolean hasAltDown() {
        return false;
    }

    default List<Component> splitTooltipLines(Component component) {
        return Collections.singletonList(component);
    }

    default void registerConfigurationScreen(String modId, String... otherModIds) {
        // NO-OP
    }

    @MustBeInvokedByOverriders
    default void registerConfigurationScreenForHolder(String modId) {
        this.registerConfigurationScreen(modId);
    }
}
