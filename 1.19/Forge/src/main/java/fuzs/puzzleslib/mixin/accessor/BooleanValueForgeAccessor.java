package fuzs.puzzleslib.mixin.accessor;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.BiConsumer;

@Mixin(GameRules.BooleanValue.class)
public interface BooleanValueForgeAccessor {

    @Invoker("create")
    static GameRules.Type<GameRules.BooleanValue> puzzleslib$callCreate(boolean bl, BiConsumer<MinecraftServer, GameRules.BooleanValue> biConsumer) {
        throw new IllegalStateException();
    }
}
