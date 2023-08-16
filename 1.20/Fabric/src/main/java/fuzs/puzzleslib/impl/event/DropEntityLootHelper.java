package fuzs.puzzleslib.impl.event;

import fuzs.puzzleslib.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;

public final class DropEntityLootHelper {

    private DropEntityLootHelper() {

    }

    public static MutableInt onLootingLevel(LivingEntity entity, DamageSource damageSource, int lootingLevel) {
        MutableInt mutableInt = MutableInt.fromValue(lootingLevel);
        FabricLivingEvents.LOOTING_LEVEL.invoker().onLootingLevel(entity, damageSource, mutableInt);
        return mutableInt;
    }

    public static boolean tryOnLivingDrops(LivingEntity entity, DamageSource damageSource, int lastHurtByPlayerTime, Supplier<MutableInt> supplier) {
        Collection<ItemEntity> capturedDrops = ((CapturedDropsEntity) entity).puzzleslib$acceptCapturedDrops(null);
        if (capturedDrops != null) {
            MutableInt lootingLevel = supplier.get();
            Objects.requireNonNull(lootingLevel, "looting level is null");
            EventResult result = FabricLivingEvents.LIVING_DROPS.invoker().onLivingDrops(entity, damageSource, capturedDrops, lootingLevel.getAsInt(), lastHurtByPlayerTime > 0);
            if (result.isPass()) capturedDrops.forEach(itemEntity -> entity.level().addFreshEntity(itemEntity));
            return true;
        } else {
            return false;
        }
    }
}
