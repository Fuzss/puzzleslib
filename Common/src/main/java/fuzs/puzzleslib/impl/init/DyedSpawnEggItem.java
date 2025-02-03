package fuzs.puzzleslib.impl.init;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.SpawnEggItem;

public class DyedSpawnEggItem extends SpawnEggItem {
    private final int backgroundColor;
    private final int highlightColor;

    public DyedSpawnEggItem(EntityType<? extends Mob> defaultType, int backgroundColor, int highlightColor, Properties properties) {
        super(defaultType, properties);
        this.backgroundColor = backgroundColor;
        this.highlightColor = highlightColor;
    }

    public int backgroundColor() {
        return this.backgroundColor;
    }

    public int highlightColor() {
        return this.highlightColor;
    }
}
