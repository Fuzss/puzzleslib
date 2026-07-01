package fuzs.puzzleslib.impl.init.boat;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Copied from Minecraft 26.1.
 */
public class TypedBoatItem extends BoatItem {
    private final EntityType<? extends Boat> entityType;

    public TypedBoatItem(EntityType<? extends Boat> entityType, Properties properties) {
        super(false, Boat.Type.OAK, properties);
        this.entityType = entityType;
    }

    @Override
    public Boat getBoat(Level level, HitResult hitResult, ItemStack itemStack, Player player) {
        Vec3 vec3 = hitResult.getLocation();
        Boat boat = this.entityType.create(level);
        if (boat != null) {
            boat.absMoveTo(vec3.x, vec3.y, vec3.z);
        }

        if (level instanceof ServerLevel serverLevel) {
            EntityType.createDefaultStackConfig(serverLevel, itemStack, player).accept(boat);
        }

        return boat;
    }
}
