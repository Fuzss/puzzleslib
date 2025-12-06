package fuzs.puzzleslib.api.item.v2;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A builder class for {@link ArmorMaterial}.
 */
public final class ArmorMaterialBuilder {
    private int durability;
    private Map<ArmorItem.Type, Integer> defense = Util.make(new EnumMap<>(ArmorItem.Type.class),
            (EnumMap<ArmorItem.Type, Integer> map) -> {
                for (ArmorItem.Type armorType : ArmorItem.Type.values()) {
                    map.put(armorType, 0);
                }
            });
    private int enchantmentValue = 1;
    private Holder<SoundEvent> equipSound = SoundEvents.ARMOR_EQUIP_GENERIC;
    private float toughness;
    private float knockbackResistance;
    private Either<TagKey<Item>, Supplier<Ingredient>> repairIngredient;
    private Either<ResourceLocation, List<ArmorMaterial.Layer>> assetId;

    private ArmorMaterialBuilder() {
        // NO-OP
    }

    /**
     * @param repairIngredient the repair material used in an anvil for restoring item durability
     * @return the builder
     */
    public static ArmorMaterialBuilder of(TagKey<Item> repairIngredient) {
        return new ArmorMaterialBuilder().setRepairIngredient(repairIngredient);
    }

    /**
     * @param assetId          the location for the equipment model definition file at
     *                         {@code assets/<namespace>/equipment/<path>.json}
     * @param repairIngredient the repair material used in an anvil for restoring item durability
     * @return the builder
     */
    public static ArmorMaterialBuilder of(ResourceLocation assetId, TagKey<Item> repairIngredient) {
        return new ArmorMaterialBuilder().setRepairIngredient(repairIngredient).setAssetId(assetId);
    }

    /**
     * @param armorMaterial the armor material
     * @return the builder
     */
    public static ArmorMaterialBuilder copyOf(ArmorMaterial armorMaterial) {
        return new ArmorMaterialBuilder().setRepairIngredient(armorMaterial.repairIngredient())
                .setAssetId(armorMaterial.layers())
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
    @Deprecated
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
        return this.setDefense(ArmorItem.Type.BOOTS, boots)
                .setDefense(ArmorItem.Type.LEGGINGS, leggings)
                .setDefense(ArmorItem.Type.CHESTPLATE, chestplate)
                .setDefense(ArmorItem.Type.HELMET, helmet)
                .setDefense(ArmorItem.Type.BODY, body);
    }

    /**
     * @param armorType the armor type
     * @param defense   the defense value
     * @return the builder
     */
    public ArmorMaterialBuilder setDefense(ArmorItem.Type armorType, int defense) {
        this.defense.put(armorType, defense);
        return this;
    }

    private ArmorMaterialBuilder setDefense(Map<ArmorItem.Type, Integer> defense) {
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
    @Deprecated
    public ArmorMaterialBuilder setRepairIngredient(Supplier<Ingredient> repairIngredient) {
        Objects.requireNonNull(repairIngredient, "repair ingredient is null");
        this.repairIngredient = Either.right(repairIngredient);
        return this;
    }

    /**
     * @param repairIngredient the repair material used in an anvil for restoring item durability
     * @return the builder
     */
    public ArmorMaterialBuilder setRepairIngredient(TagKey<Item> repairIngredient) {
        Objects.requireNonNull(repairIngredient, "repair ingredient is null");
        this.repairIngredient = Either.left(repairIngredient);
        return this;
    }

    /**
     * @param assetId the location for the equipment model definition file at
     *                {@code assets/<namespace>/equipment/<path>.json}
     * @return the builder
     */
    @Deprecated
    public ArmorMaterialBuilder setAssetId(List<ArmorMaterial.Layer> layers) {
        Objects.requireNonNull(layers, "layers is null");
        this.assetId = Either.right(layers);
        return this;
    }

    /**
     * @param assetId the location for the equipment model definition file at
     *                {@code assets/<namespace>/equipment/<path>.json}
     * @return the builder
     */
    public ArmorMaterialBuilder setAssetId(ResourceLocation assetId) {
        Objects.requireNonNull(assetId, "asset id is null");
        this.assetId = Either.left(assetId);
        return this;
    }

    /**
     * Prevent the armor set from rendering by setting an invalid equipment model definition.
     *
     * @return the builder
     */
    public ArmorMaterialBuilder setNoAssetId() {
        this.assetId = Either.right(Collections.emptyList());
        return this;
    }

    /**
     * @return the armor material
     */
    public ArmorMaterial build() {
        Objects.requireNonNull(this.defense, "defense map is null");
        Objects.requireNonNull(this.equipSound, "equip sound is null");
        Objects.requireNonNull(this.repairIngredient, "repair ingredient is null");
        Objects.requireNonNull(this.assetId, "asset id is null");
        return new ArmorMaterial(Maps.immutableEnumMap(this.defense),
                this.enchantmentValue,
                this.equipSound,
                this.repairIngredient.map((TagKey<Item> tagKey) -> Suppliers.memoize(() -> Ingredient.of(tagKey)),
                        Function.identity()),
                this.assetId.map((ResourceLocation resourceLocation) -> Collections.singletonList(new ArmorMaterial.Layer(
                        resourceLocation) {
                    @Override
                    protected ResourceLocation resolveTexture(boolean innerTexture) {
                        return this.assetName.withPath((String string) -> "textures/entity/equipment/" + (innerTexture ?
                                "humanoid_leggings" : "humanoid") + "/" + this.assetName.getPath() + ".png");
                    }
                }), Function.identity()),
                this.toughness,
                this.knockbackResistance);
    }
}
