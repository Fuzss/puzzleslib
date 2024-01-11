package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface LivingEquipmentChangeCallback {
    EventInvoker<LivingEquipmentChangeCallback> EVENT = EventInvoker.lookup(LivingEquipmentChangeCallback.class);

    /**
     * Fires whenever equipment changes are detected on an entity in {@link LivingEntity#collectEquipmentChanges()} from {@link LivingEntity#tick()}.
     * This runs before attributes supplied by the equipment are updated on the entity and before the changed equipment is synced to clients.
     *
     * <p>Note that the callback does <b>NOT</b> run immediately when the equipment actually changes like in {@link LivingEntity#onEquipItem(EquipmentSlot, ItemStack, ItemStack)}.
     *
     * @param entity        the entity changing its equipment
     * @param equipmentSlot the equipment slot that is changed
     * @param oldItemStack  the item stack previously found in the slot
     * @param newItemStack  the new item stack placed into the slot
     */
    void onLivingEquipmentChange(LivingEntity entity, EquipmentSlot equipmentSlot, ItemStack oldItemStack, ItemStack newItemStack);
}
