package fuzs.puzzleslib.mixin;

import com.mojang.brigadier.CommandDispatcher;
import fuzs.puzzleslib.impl.command.ModEnchantCommand;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.EnchantCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantCommand.class)
abstract class EnchantCommandMixin {

    @Inject(method = "register", at = @At("HEAD"), cancellable = true)
    private static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, CallbackInfo callback) {
        ModEnchantCommand.register(dispatcher, context);
        callback.cancel();
    }
}
