package fuzs.puzzleslib.neoforge.mixin.client;

import fuzs.puzzleslib.neoforge.impl.client.event.NeoForgeClientEventInvokers;
import net.minecraft.client.MouseHandler;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MouseHandler.class)
abstract class MouseHandlerNeoForgeMixin {

    @ModifyVariable(method = "onScroll", at = @At("STORE"))
    private Vector2i onScroll(Vector2i wheelXY) {
        return NeoForgeClientEventInvokers.wheelXY = wheelXY;
    }
}
