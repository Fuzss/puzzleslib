package fuzs.puzzleslib.common.impl.core.proxy;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public interface EntityProxy {

    boolean canEquip(ItemStack itemStack, EquipmentSlot equipmentSlot, LivingEntity livingEntity);

    boolean isMobGriefingAllowed(ServerLevel serverLevel, @Nullable Entity entity);

    Entity getPartEntityParent(Entity entity);

    boolean isFakePlayer(ServerPlayer serverPlayer);

    boolean isPiglinCurrency(ItemStack itemStack);
}
