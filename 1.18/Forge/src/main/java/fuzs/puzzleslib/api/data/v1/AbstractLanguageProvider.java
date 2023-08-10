package fuzs.puzzleslib.api.data.v1;

import fuzs.puzzleslib.api.init.v2.RegistryReference;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

public abstract class AbstractLanguageProvider extends LanguageProvider {
    protected final String modId;

    public AbstractLanguageProvider(GatherDataEvent evt, String modId) {
        this(evt.getGenerator(), modId);
    }

    public AbstractLanguageProvider(DataGenerator packOutput, String modId) {
        super(packOutput, modId, "en_us");
        this.modId = modId;
    }

    @Override
    protected abstract void addTranslations();

    public void addCreativeModeTab(String value) {
        this.addCreativeModeTab("main", value);
    }

    public void addCreativeModeTab(String tabId, String value) {
        this.add(String.format("itemGroup.%s.%s", this.modId, tabId), value);
    }

    public void add(Attribute attribute, String value) {
        this.add(attribute.getDescriptionId(), value);
    }

    public void add(StatType<?> statType, String value) {
        this.add(statType.getTranslationKey(), value);
    }

    public void add(Potion potion, String value) {
        String potionName = potion.getName("");
        this.add("item.minecraft.tipped_arrow.effect." + potionName, "Arrow of " + value);
        this.add("item.minecraft.potion.effect." + potionName, "Potion of " + value);
        this.add("item.minecraft.splash_potion.effect." + potionName, "Splash Potion of " + value);
        this.add("item.minecraft.lingering_potion.effect." + potionName, "Lingering Potion of " + value);
    }

    public void add(SoundEvent soundEvent, String value) {
        this.add("subtitles." + soundEvent.getLocation().getPath(), value);
    }

    public void add(KeyMapping keyMapping, String value) {
        this.add(keyMapping.getName(), value);
    }

    public void add(ResourceLocation identifier, String value) {
        this.add(identifier.getNamespace() + "." + identifier.getPath(), value);
    }

    @Deprecated(forRemoval = true)
    public void addAdditional(Block block, String key, String value) {
        this.add(block.getDescriptionId() + "." + key, value);
    }

    public void add(Block block, String additionalKey, String value) {
        this.add(block.getDescriptionId() + "." + additionalKey, value);
    }

    @Deprecated(forRemoval = true)
    public void addAdditional(Item item, String key, String value) {
        this.add(item.getDescriptionId() + "." + key, value);
    }

    public void add(Item item, String additionalKey, String value) {
        this.add(item.getDescriptionId() + "." + additionalKey, value);
    }

    @Deprecated(forRemoval = true)
    public void addAdditional(Enchantment enchantment, String key, String value) {
        this.add(enchantment.getDescriptionId() + "." + key, value);
    }

    public void add(Enchantment enchantment, String additionalKey, String value) {
        this.add(enchantment.getDescriptionId() + "." + additionalKey, value);
    }

    @Deprecated(forRemoval = true)
    public void addAdditional(MobEffect mobEffect, String key, String value) {
        this.add(mobEffect.getDescriptionId() + "." + key, value);
    }

    public void add(MobEffect mobEffect, String additionalKey, String value) {
        this.add(mobEffect.getDescriptionId() + "." + additionalKey, value);
    }

    @Deprecated(forRemoval = true)
    public void addAdditional(EntityType<?> entityType, String key, String value) {
        this.add(entityType.getDescriptionId() + "." + key, value);
    }

    public void add(EntityType<?> entityType, String additionalKey, String value) {
        this.add(entityType.getDescriptionId() + "." + additionalKey, value);
    }

    public void add(Attribute attribute, String additionalKey, String value) {
        this.add(attribute.getDescriptionId() + "." + additionalKey, value);
    }

    public void add(StatType<?> statType, String additionalKey, String value) {
        this.add(statType.getTranslationKey() + "." + additionalKey, value);
    }

    public void add(String key, String additionalKey, String value) {
        this.add(key + "." + additionalKey, value);
    }

    public void add(ResourceLocation identifier, String additionalKey, String value) {
        this.add(identifier.getNamespace() + "." + identifier.getPath() + "." + additionalKey, value);
    }

    /**
     * @deprecated migrate to {@link #addDamageType(DamageSource, String)}
     */
    @Deprecated(forRemoval = true)
    public void addDamageSource(String damageSource, String value) {
        this.add("death.attack." + damageSource, value);
    }

    public void addDamageType(DamageSource damageSource, String value) {
        this.add("death.attack." + damageSource.msgId, value);
    }

    public void addPlayerDamageType(DamageSource damageSource, String value) {
        this.add("death.attack." + damageSource.msgId + ".player", value);
    }

    public void addItemDamageType(DamageSource damageSource, String value) {
        this.add("death.attack." + damageSource.msgId + ".item", value);
    }

    public void add(String registry, RegistryReference<?> registryReference, String value) {
        this.add(registry, registryReference.getResourceLocation(), value);
    }

    public void add(String registry, ResourceKey<?> resourceKey, String value) {
        this.add(registry, resourceKey.location(), value);
    }

    public void add(String registry, ResourceLocation resourceLocation, String value) {
        this.add(Util.makeDescriptionId(registry, resourceLocation), value);
    }
}
