package fuzs.puzzleslib.mixin.accessor;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.BiConsumer;

@Mixin(GameRules.IntegerValue.class)
public interface IntegerValueForgeAccessor {

    @Invoker("create")
    static GameRules.Type<GameRules.IntegerValue> puzzleslib$callCreate(int i, BiConsumer<MinecraftServer, GameRules.IntegerValue> biConsumer) {
        throw new RuntimeException();
    }
}
