package fuzs.puzzleslib.api.data.v1;

import net.minecraft.client.KeyMapping;
import net.minecraft.data.DataGenerator;
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

public abstract class AbstractLanguageProvider extends LanguageProvider {
    protected final String modId;

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

    public void add(Attribute entityAttribute, String value) {
        this.add(entityAttribute.getDescriptionId(), value);
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

    public void addAdditional(Block block, String key, String value) {
        this.add(block.getDescriptionId() + "." + key, value);
    }

    public void addAdditional(Item item, String key, String value) {
        this.add(item.getDescriptionId() + "." + key, value);
    }

    public void addAdditional(Enchantment enchantment, String key, String value) {
        this.add(enchantment.getDescriptionId() + "." + key, value);
    }

    public void addAdditional(MobEffect mobEffect, String key, String value) {
        this.add(mobEffect.getDescriptionId() + "." + key, value);
    }

    public void addAdditional(EntityType<?> entityType, String key, String value) {
        this.add(entityType.getDescriptionId() + "." + key, value);
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
}
