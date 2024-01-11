package fuzs.puzzleslib.api.event.v1.entity.living;

import com.google.common.collect.Multimap;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface ItemAttributeModifiersCallback {
    EventInvoker<ItemAttributeModifiersCallback> EVENT = EventInvoker.lookup(ItemAttributeModifiersCallback.class);

    /**
     * Runs after attribute modifiers have been built for an {@link ItemStack} in a specific {@link EquipmentSlot}, allows for modifying those attributes.
     *
     * @param stack                      the stack item attribute modifiers are being generated for
     * @param equipmentSlot              the equipment slot <code>stack</code> is currently placed in, most attributes will only apply for a specific slot, like armor points
     * @param attributeModifiers         a modifiable attribute modifiers map
     * @param originalAttributeModifiers an unmodifiable via of vanilla's original attribute modifiers map
     */
    void onItemAttributeModifiers(ItemStack stack, EquipmentSlot equipmentSlot, Multimap<Attribute, AttributeModifier> attributeModifiers, Multimap<Attribute, AttributeModifier> originalAttributeModifiers);
}
