package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.impl.init.MinecartTypeRegistryImpl;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractMinecart.class)
abstract class AbstractMinecartMixin extends VehicleEntity {

    public AbstractMinecartMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyVariable(method = "createMinecart", at = @At("STORE"))
    private static AbstractMinecart createMinecart(AbstractMinecart abstractMinecart, ServerLevel level, double x, double y, double z, AbstractMinecart.Type type, ItemStack stack, @Nullable Player player) {
        return MinecartTypeRegistryImpl.createMinecartForType(type, level, x, y, z).orElse(abstractMinecart);
    }
}
