package fuzs.puzzleslib.impl.event;

import net.minecraft.world.entity.item.ItemEntity;

import java.util.Collection;

public interface CapturedDropsEntity {

    Collection<ItemEntity> puzzleslib$acceptCapturedDrops(Collection<ItemEntity> capturedDrops);
}
