package fuzs.puzzleslib.api.item.v2;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * A builder class for {@link ArmorMaterial}.
 */
public final class ArmorMaterialBuilder {
    private static final ResourceLocation NO_RENDER_MODEL_ID = PuzzlesLibMod.id("no_render");

    private int durability;
    private Map<ArmorType, Integer> defense = Util.make(new EnumMap<>(ArmorType.class),
            (EnumMap<ArmorType, Integer> map) -> {
                for (ArmorType armorType : ArmorType.values()) {
                    map.put(armorType, 0);
                }
            });
    private int enchantmentValue = 1;
    private Holder<SoundEvent> equipSound = SoundEvents.ARMOR_EQUIP_GENERIC;
    private float toughness;
    private float knockbackResistance;
    private TagKey<Item> repairIngredient;
    private ResourceLocation modelId;

    private ArmorMaterialBuilder() {
        // NO-OP
    }

    /**
     * @param modelId          the location for the equipment model definition file at
     *                         {@code assets/<namespace>/models/equipment/<path>.json}
     * @param repairIngredient the repair material used in an anvil for restoring item durability
     * @return the builder
     */
    public static ArmorMaterialBuilder of(ResourceLocation modelId, TagKey<Item> repairIngredient) {
        return new ArmorMaterialBuilder().setModelId(modelId).setRepairIngredient(repairIngredient);
    }

    /**
     * @param armorMaterial the armor material
     * @return the builder
     */
    public static ArmorMaterialBuilder copyOf(ArmorMaterial armorMaterial) {
        return of(armorMaterial.modelId(), armorMaterial.repairIngredient()).setDurability(armorMaterial.durability())
                .setDefense(armorMaterial.defense())
                .setEnchantmentValue(armorMaterial.enchantmentValue())
                .setEquipSound(armorMaterial.equipSound())
                .setToughness(armorMaterial.toughness())
                .setKnockbackResistance(armorMaterial.knockbackResistance());
    }

    /**
     * @param durability multiplier for internal base durability per slot type
     * @return the builder
     */
    public ArmorMaterialBuilder setDurability(int durability) {
        this.durability = durability;
        return this;
    }

    /**
     * @param defense the defense value for this armor set
     * @return the builder
     */
    public ArmorMaterialBuilder setDefense(int defense) {
        return this.setDefense(defense, defense, defense, defense, defense);
    }

    /**
     * @param boots      the boots defense value
     * @param leggings   the leggings defense value
     * @param chestplate the chestplate defense value
     * @param helmet     the helmet defense value
     * @return the builder
     */
    public ArmorMaterialBuilder setDefense(int boots, int leggings, int chestplate, int helmet) {
        return this.setDefense(boots, leggings, chestplate, helmet, 0);
    }

    /**
     * @param boots      the boots defense value
     * @param leggings   the leggings defense value
     * @param chestplate the chestplate defense value
     * @param helmet     the helmet defense value
     * @param body       the body defense value
     * @return the builder
     */
    public ArmorMaterialBuilder setDefense(int boots, int leggings, int chestplate, int helmet, int body) {
        return this.setDefense(ArmorType.BOOTS, boots)
                .setDefense(ArmorType.LEGGINGS, leggings)
                .setDefense(ArmorType.CHESTPLATE, chestplate)
                .setDefense(ArmorType.HELMET, helmet)
                .setDefense(ArmorType.BODY, body);
    }

    /**
     * @param armorType the armor type
     * @param defense   the defense value
     * @return the builder
     */
    public ArmorMaterialBuilder setDefense(ArmorType armorType, int defense) {
        this.defense.put(armorType, defense);
        return this;
    }

    private ArmorMaterialBuilder setDefense(Map<ArmorType, Integer> defense) {
        this.defense = defense;
        return this;
    }

    /**
     * @param enchantmentValue the item enchantment value
     * @return the builder
     */
    public ArmorMaterialBuilder setEnchantmentValue(int enchantmentValue) {
        this.enchantmentValue = enchantmentValue;
        return this;
    }

    /**
     * @param equipSound the sound played when putting a piece of armor in the correct equipment slot
     * @return the builder
     */
    public ArmorMaterialBuilder setEquipSound(Holder<SoundEvent> equipSound) {
        Objects.requireNonNull(equipSound, "equip sound is null");
        this.equipSound = equipSound;
        return this;
    }

    /**
     * @param toughness the armor toughness value for this armor set
     * @return the builder
     */
    public ArmorMaterialBuilder setToughness(float toughness) {
        this.toughness = toughness;
        return this;
    }

    /**
     * @param knockbackResistance the knockback resistance value for this armor set
     * @return the builder
     */
    public ArmorMaterialBuilder setKnockbackResistance(float knockbackResistance) {
        this.knockbackResistance = knockbackResistance;
        return this;
    }

    /**
     * @param repairIngredient the repair material used in an anvil for restoring item durability
     * @return the builder
     */
    public ArmorMaterialBuilder setRepairIngredient(TagKey<Item> repairIngredient) {
        Objects.requireNonNull(repairIngredient, "repair ingredient is null");
        this.repairIngredient = repairIngredient;
        return this;
    }

    /**
     * @param modelId the location for the equipment model definition file at
     *                {@code assets/<namespace>/models/equipment/<path>.json}
     * @return the builder
     */
    public ArmorMaterialBuilder setModelId(ResourceLocation modelId) {
        Objects.requireNonNull(modelId, "model id is null");
        this.modelId = modelId;
        return this;
    }

    /**
     * Prevent the armor set from rendering by setting an invalid equipment model definition.
     *
     * @return the builder
     */
    public ArmorMaterialBuilder setNoModelId() {
        return this.setModelId(NO_RENDER_MODEL_ID);
    }

    /**
     * @return the armor material
     */
    public ArmorMaterial build() {
        return new ArmorMaterial(this.durability,
                Maps.immutableEnumMap(this.defense),
                this.enchantmentValue,
                this.equipSound,
                this.toughness,
                this.knockbackResistance,
                this.repairIngredient,
                this.modelId);
    }
}
