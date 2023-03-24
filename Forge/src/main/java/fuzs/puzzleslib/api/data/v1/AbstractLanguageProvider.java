package fuzs.puzzleslib.api.data.v1;

import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.CreativeModeTabRegistry;
import net.minecraftforge.common.data.LanguageProvider;

import java.util.Objects;

public abstract class AbstractLanguageProvider extends LanguageProvider {
    protected final String modId;

    public AbstractLanguageProvider(PackOutput packOutput, String modId) {
        super(packOutput, modId, "en_us");
        this.modId = modId;
    }

    @Override
    protected abstract void addTranslations();

    public void addCreativeModeTab(String value) {
        this.addCreativeModeTab("main", value);
    }

    public void addCreativeModeTab(String tabId, String value) {
        this.add(CreativeModeTabRegistry.getTab(new ResourceLocation(this.modId, tabId)), value);
    }

    public void add(CreativeModeTab tab, String value) {
        Objects.requireNonNull(tab, "tab is null");
        if (tab.getDisplayName().getContents() instanceof TranslatableContents contents) {
            this.add(contents.getKey(), value);
        } else {
            throw new UnsupportedOperationException("Cannot add language entry for tab %s".formatted(tab.getDisplayName().getString()));
        }
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

    public void add(ResourceLocation identifier, String value) {
        this.add(identifier.toLanguageKey(), value);
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

    public void addDamageSource(String damageSource, String value) {
        this.add("death.attack." + damageSource, value);
    }
}
