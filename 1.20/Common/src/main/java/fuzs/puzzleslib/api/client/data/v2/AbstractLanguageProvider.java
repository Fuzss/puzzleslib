package fuzs.puzzleslib.api.client.data.v2;

import com.google.gson.JsonObject;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractLanguageProvider implements DataProvider {
    protected final String languageCode;
    protected final String modId;
    protected final PackOutput.PathProvider pathProvider;

    public AbstractLanguageProvider(DataProviderContext context) {
        this(context.getModId(), context.getPackOutput());
    }

    public AbstractLanguageProvider(String languageCode, DataProviderContext context) {
        this(languageCode, context.getModId(), context.getPackOutput());
    }

    public AbstractLanguageProvider(String modId, PackOutput packOutput) {
        this("en_us", modId, packOutput);
    }

    public AbstractLanguageProvider(String languageCode, String modId, PackOutput packOutput) {
        this.languageCode = languageCode;
        this.modId = modId;
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "lang");
    }

    protected abstract void addTranslations(TranslationBuilder builder);

    @Override
    public CompletableFuture<?> run(CachedOutput writer) {
        JsonObject jsonObject = new JsonObject();
        this.addTranslations((String key, String value) -> {
            Objects.requireNonNull(key, "key is null");
            Objects.requireNonNull(value, "value is null");
            if (jsonObject.has(key)) {
                throw new IllegalStateException("Duplicate translation key found: " + key);
            } else {
                jsonObject.addProperty(key, value);
            }
        });
        return DataProvider.saveStable(writer, jsonObject, this.pathProvider.json(new ResourceLocation(this.modId, this.languageCode)));
    }

    @Override
    public String getName() {
        return "Language (%s)".formatted(this.languageCode);
    }

    @ApiStatus.NonExtendable
    @FunctionalInterface
    public interface TranslationBuilder {

        void add(String key, String value);

        default void add(String key, String additionalKey, String value) {
            this.add(key + (additionalKey.isEmpty() ? "" : "." + additionalKey), value);
        }

        default void add(ResourceLocation identifier, String value) {
            this.add(identifier, "", value);
        }

        default void add(ResourceLocation identifier, String additionalKey, String value) {
            this.add(identifier.toLanguageKey(), additionalKey, value);
        }

        default void add(String registry, Holder.Reference<?> holder, String value) {
            this.add(registry, holder.key(), value);
        }

        default void add(String registry, ResourceKey<?> resourceKey, String value) {
            this.add(registry, resourceKey.location(), value);
        }

        default void add(String registry, ResourceLocation resourceLocation, String value) {
            this.add(Util.makeDescriptionId(registry, resourceLocation), value);
        }

        default void add(Block block, String value) {
            this.add(block, "", value);
        }

        default void add(Block block, String additionalKey, String value) {
            this.add(block.getDescriptionId(), additionalKey, value);
        }

        default void add(Item item, String value) {
            this.add(item, "", value);
        }

        default void add(Item item, String additionalKey, String value) {
            this.add(item.getDescriptionId(), additionalKey, value);
        }

        default void add(Enchantment enchantment, String value) {
            this.add(enchantment, "", value);
        }

        default void add(Enchantment enchantment, String additionalKey, String value) {
            this.add(enchantment.getDescriptionId(), additionalKey, value);
        }

        default void add(MobEffect mobEffect, String value) {
            this.add(mobEffect, "", value);
        }

        default void add(MobEffect mobEffect, String additionalKey, String value) {
            this.add(mobEffect.getDescriptionId(), additionalKey, value);
        }

        default void add(EntityType<?> entityType, String value) {
            this.add(entityType, "", value);
        }

        default void add(EntityType<?> entityType, String additionalKey, String value) {
            this.add(entityType.getDescriptionId(), additionalKey, value);
        }

        default void add(Attribute attribute, String value) {
            this.add(attribute, "", value);
        }

        default void add(Attribute attribute, String additionalKey, String value) {
            this.add(attribute.getDescriptionId(), additionalKey, value);
        }

        default void add(StatType<?> statType, String value) {
            this.add(statType, "", value);
        }

        default void add(StatType<?> statType, String additionalKey, String value) {
            this.add(statType.getTranslationKey(), additionalKey, value);
        }

        default void add(GameRules.Key<?> gameRule, String value) {
            this.add(gameRule, "", value);
        }

        default void addGameRuleDescription(GameRules.Key<?> gameRule, String value) {
            this.add(gameRule, "description", value);
        }

        default void add(GameRules.Key<?> gameRule, String additionalKey, String value) {
            this.add(gameRule.getDescriptionId(), additionalKey, value);
        }

        default void add(Potion potion, String value) {
            String potionName = potion.getName("");
            this.add("item.minecraft.tipped_arrow.effect." + potionName, "Arrow of " + value);
            this.add("item.minecraft.potion.effect." + potionName, "Potion of " + value);
            this.add("item.minecraft.splash_potion.effect." + potionName, "Splash Potion of " + value);
            this.add("item.minecraft.lingering_potion.effect." + potionName, "Lingering Potion of " + value);
        }

        default void add(SoundEvent soundEvent, String value) {
            this.add("subtitles." + soundEvent.getLocation().getPath(), value);
        }

        default void add(KeyMapping keyMapping, String value) {
            this.add(keyMapping.getName(), value);
        }

        default void addCreativeModeTab(String modId, String value) {
            this.addCreativeModeTab(modId, "main", value);
        }

        default void addCreativeModeTab(String modId, String tabId, String value) {
            this.addCreativeModeTab(new ResourceLocation(modId, tabId), value);
        }

        default void addCreativeModeTab(ResourceLocation identifier, String value) {
            this.addCreativeModeTab(ResourceKey.create(Registries.CREATIVE_MODE_TAB, identifier), value);
        }

        default void addCreativeModeTab(ResourceKey<CreativeModeTab> resourceKey, String value) {
            this.add(BuiltInRegistries.CREATIVE_MODE_TAB.get(resourceKey), value);
        }

        default void add(CreativeModeTab tab, String value) {
            Objects.requireNonNull(tab, "tab is null");
            if (tab.getDisplayName().getContents() instanceof TranslatableContents contents) {
                this.add(contents.getKey(), value);
            } else {
                throw new UnsupportedOperationException("Cannot add language entry for tab %s".formatted(tab.getDisplayName().getString()));
            }
        }

        default void addGenericDamageType(ResourceKey<DamageType> damageType, String value) {
            this.add("death.attack." + damageType.location().getPath(), value);
        }

        default void addPlayerDamageType(ResourceKey<DamageType> damageType, String value) {
            this.add("death.attack." + damageType.location().getPath() + ".player", value);
        }

        default void addItemDamageType(ResourceKey<DamageType> damageType, String value) {
            this.add("death.attack." + damageType.location().getPath() + ".item", value);
        }
    }
}
