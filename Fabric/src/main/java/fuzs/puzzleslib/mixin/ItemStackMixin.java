package fuzs.puzzleslib.mixin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import fuzs.puzzleslib.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import fuzs.puzzleslib.impl.event.AttributeModifiersMultimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
abstract class ItemStackMixin {

    @Inject(method = "getAttributeModifiers", at = @At("RETURN"), cancellable = true)
    public void getAttributeModifiers(EquipmentSlot slot, CallbackInfoReturnable<Multimap<Attribute, AttributeModifier>> callback) {
        Multimap<Attribute, AttributeModifier> originalAttributeModifiers = callback.getReturnValue();
        MutableValue<Multimap<Attribute, AttributeModifier>> value = MutableValue.fromValue(null);
        Multimap<Attribute, AttributeModifier> attributeModifiers = new AttributeModifiersMultimap(() -> {
            return value.get() != null ? value.get() : originalAttributeModifiers;
        }, (attribute, attributeModifier) -> {
            if (value.get() == null) value.accept(HashMultimap.create(originalAttributeModifiers));
            return value.get().put(attribute, attributeModifier);
        }, (attribute, attributeModifier) -> {
            if (value.get() == null) value.accept(HashMultimap.create(originalAttributeModifiers));
            return value.get().remove(attribute, attributeModifier);
        }, attribute -> {
            if (value.get() == null) value.accept(HashMultimap.create(originalAttributeModifiers));
            return value.get().removeAll(attribute);
        }, () -> {
            if (value.get() == null) value.accept(HashMultimap.create(originalAttributeModifiers));
            value.get().clear();
        });
        FabricLivingEvents.ITEM_ATTRIBUTE_MODIFIERS.invoker().onItemAttributeModifiers(ItemStack.class.cast(this), slot, attributeModifiers, originalAttributeModifiers);
        if (value.get() != null) callback.setReturnValue(value.get());
    }
}
