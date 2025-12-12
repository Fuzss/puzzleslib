package fuzs.puzzleslib.fabric.impl.event;

import net.minecraft.world.entity.item.ItemEntity;
import org.jspecify.annotations.Nullable;

import java.util.Collection;

public interface CapturedDropsEntity {

    @Nullable Collection<ItemEntity> puzzleslib$acceptCapturedDrops(@Nullable Collection<ItemEntity> capturedDrops);

    @Nullable Collection<ItemEntity> puzzleslib$getCapturedDrops();
}
