package fuzs.puzzleslib.neoforge.mixin.client;

import joptsimple.OptionParser;
import net.minecraft.client.data.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Deprecated
@Mixin(Main.class)
abstract class ClientDataMainNeoForgeMixin {

    @ModifyVariable(method = "main", at = @At("STORE"), remap = false)
    private static OptionParser main(OptionParser optionParser) {
        optionParser.allowsUnrecognizedOptions();
        return optionParser;
    }
}
