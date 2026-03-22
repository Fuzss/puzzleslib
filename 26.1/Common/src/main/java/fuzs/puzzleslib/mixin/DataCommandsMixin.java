package fuzs.puzzleslib.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import fuzs.puzzleslib.impl.content.ItemDataAccessor;
import net.minecraft.server.commands.data.DataCommands;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.function.Function;

@Mixin(DataCommands.class)
abstract class DataCommandsMixin {

    @ModifyExpressionValue(
            method = "<clinit>", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/server/commands/data/DataCommands;ALL_PROVIDERS:Ljava/util/List;",
            opcode = Opcodes.GETSTATIC
    )
    )
    private static List<Function<String, DataCommands.DataProvider>> clinit(List<Function<String, DataCommands.DataProvider>> allProviders) {
        return ImmutableList.<Function<String, DataCommands.DataProvider>>builder()
                .addAll(allProviders)
                .add(ItemDataAccessor.PROVIDER)
                .build();
    }
}
