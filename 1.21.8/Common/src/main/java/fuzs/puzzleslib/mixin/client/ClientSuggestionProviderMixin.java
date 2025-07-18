package fuzs.puzzleslib.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;

@Mixin(ClientSuggestionProvider.class)
abstract class ClientSuggestionProviderMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @ModifyReturnValue(method = "getSelectedEntities", at = @At("RETURN"))
    public Collection<String> getSelectedEntities(Collection<String> entities) {
        // include suggestions for nearby entities, if none is highlighted
        if (entities.isEmpty() && this.minecraft.level != null && this.minecraft.player != null) {
            return this.minecraft.level.getEntities(this.minecraft.player,
                    this.minecraft.player.getBoundingBox().inflate(5.0),
                    EntitySelector.CAN_BE_PICKED).stream().map(Entity::getStringUUID).toList();
        } else {
            return entities;
        }
    }
}
