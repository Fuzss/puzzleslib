package fuzs.puzzleslib.api.client.data.v2;

import com.google.gson.JsonObject;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.Optional;
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

    public abstract void addTranslations(TranslationBuilder builder);

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

        return DataProvider.saveStable(writer,
                jsonObject,
                this.pathProvider.json(ResourceLocationHelper.fromNamespaceAndPath(this.modId, this.languageCode))
        );
    }

    @Override
    public String getName() {
        return "Language (%s)".formatted(this.languageCode);
    }

    @ApiStatus.NonExtendable
    @FunctionalInterface
    public interface TranslationBuilder {

        void add(String translationKey, String value);

        default void add(String translationKey, String additionalKey, String value) {
            Objects.requireNonNull(additionalKey, "additional key is null");
            this.add(translationKey + (additionalKey.isEmpty() ? "" : "." + additionalKey), value);
        }

        default void add(ResourceLocation resourceLocation, String value) {
            this.add(resourceLocation, "", value);
        }

        default void add(ResourceLocation resourceLocation, String additionalKey, String value) {
            Objects.requireNonNull(resourceLocation, "resource location is null");
            this.add(resourceLocation.toLanguageKey(), additionalKey, value);
        }

        default void add(String registry, Holder<?> holder, String value) {
            Objects.requireNonNull(registry, "registry is null");
            Objects.requireNonNull(holder, "holder is null");
            this.add(registry, holder.unwrapKey().orElseThrow(), value);
        }

        default void add(String registry, ResourceKey<?> resourceKey, String value) {
            Objects.requireNonNull(registry, "registry is null");
            Objects.requireNonNull(resourceKey, "resource key is null");
            this.add(registry, resourceKey.location(), value);
        }

        default void add(String registry, ResourceLocation resourceLocation, String value) {
            Objects.requireNonNull(registry, "registry is null");
            Objects.requireNonNull(resourceLocation, "resource location is null");
            this.add(Util.makeDescriptionId(registry, resourceLocation), value);
        }

        default void add(TagKey<?> tagKey, String value) {
            this.add("tag." + tagKey.location().toLanguageKey(tagKey.registry().location().getPath()), value);
        }

        default void add(Block block, String value) {
            this.add(block, "", value);
        }

        default void add(Block block, String additionalKey, String value) {
            Objects.requireNonNull(block, "block is null");
            this.add(block.getDescriptionId(), additionalKey, value);
        }

        default void addSpawnEgg(Item item, String value) {
            if (item instanceof SpawnEggItem) {
                this.add(item, value + " Spawn Egg");
            } else {
                throw new IllegalArgumentException("Unsupported item: " + item);
            }
        }

        default void add(Item item, String value) {
            this.add(item, "", value);
        }

        default void add(Item item, String additionalKey, String value) {
            Objects.requireNonNull(item, "item is null");
            this.add(item.getDescriptionId(), additionalKey, value);
        }

        default void addEnchantment(ResourceKey<Enchantment> enchantment, String value) {
            this.addEnchantment(enchantment, "", value);
        }

        default void addEnchantment(ResourceKey<Enchantment> enchantment, String additionalKey, String value) {
            Objects.requireNonNull(enchantment, "enchantment is null");
            String translationKey = Util.makeDescriptionId(enchantment.registry().getPath(), enchantment.location());
            this.add(translationKey, additionalKey, value);
        }

        default void addMobEffect(Holder<MobEffect> mobEffect, String value) {
            Objects.requireNonNull(mobEffect, "mob effect is null");
            this.add(mobEffect.value(), value);
        }

        default void addMobEffect(Holder<MobEffect> mobEffect, String additionalKey, String value) {
            Objects.requireNonNull(mobEffect, "mob effect is null");
            this.add(mobEffect.value(), additionalKey, value);
        }

        default void add(MobEffect mobEffect, String value) {
            this.add(mobEffect, "", value);
        }

        default void add(MobEffect mobEffect, String additionalKey, String value) {
            Objects.requireNonNull(mobEffect, "mob effect is null");
            this.add(mobEffect.getDescriptionId(), additionalKey, value);
        }

        default void add(EntityType<?> entityType, String value) {
            this.add(entityType, "", value);
        }

        default void add(EntityType<?> entityType, String additionalKey, String value) {
            Objects.requireNonNull(entityType, "entity type is null");
            this.add(entityType.getDescriptionId(), additionalKey, value);
        }

        default void add(Attribute attribute, String value) {
            this.add(attribute, "", value);
        }

        default void add(Attribute attribute, String additionalKey, String value) {
            Objects.requireNonNull(attribute, "attribute is null");
            this.add(attribute.getDescriptionId(), additionalKey, value);
        }

        default void add(StatType<?> statType, String value) {
            this.add(statType, "", value);
        }

        default void add(StatType<?> statType, String additionalKey, String value) {
            Objects.requireNonNull(statType, "stat type is null");
            Objects.requireNonNull(statType.getDisplayName(), "component is null");
            if (statType.getDisplayName().getContents() instanceof TranslatableContents contents) {
                this.add(contents.getKey(), additionalKey, value);
            } else {
                throw new IllegalArgumentException("Unsupported component: " + statType.getDisplayName());
            }
        }

        default void add(GameRules.Key<?> gameRule, String value) {
            this.add(gameRule, "", value);
        }

        default void addGameRuleDescription(GameRules.Key<?> gameRule, String value) {
            this.add(gameRule, "description", value);
        }

        default void add(GameRules.Key<?> gameRule, String additionalKey, String value) {
            Objects.requireNonNull(gameRule, "game rule is null");
            this.add(gameRule.getDescriptionId(), additionalKey, value);
        }

        default void addPotion(Holder<Potion> potion, String value) {
            Objects.requireNonNull(potion, "potion is null");
            String potionName = Potion.getName(Optional.of(potion), "");
            this.add("item.minecraft.tipped_arrow.effect." + potionName, "Arrow of " + value);
            this.add("item.minecraft.potion.effect." + potionName, "Potion of " + value);
            this.add("item.minecraft.splash_potion.effect." + potionName, "Splash Potion of " + value);
            this.add("item.minecraft.lingering_potion.effect." + potionName, "Lingering Potion of " + value);
        }

        default void add(SoundEvent soundEvent, String value) {
            Objects.requireNonNull(soundEvent, "sound event is null");
            this.add("subtitles." + soundEvent.getLocation().getPath(), value);
        }

        default void add(KeyMapping keyMapping, String value) {
            Objects.requireNonNull(keyMapping, "key mapping is null");
            this.add(keyMapping.getName(), value);
        }

        default void addKeyCategory(String modId, String value) {
            this.add("key.categories." + modId, value);
        }

        default void addCreativeModeTab(String modId, String value) {
            this.addCreativeModeTab(modId, "main", value);
        }

        default void addCreativeModeTab(String modId, String tabId, String value) {
            Objects.requireNonNull(modId, "mod id is null");
            Objects.requireNonNull(tabId, "tab id is null");
            this.addCreativeModeTab(ResourceLocationHelper.fromNamespaceAndPath(modId, tabId), value);
        }

        default void addCreativeModeTab(ResourceLocation resourceLocation, String value) {
            Objects.requireNonNull(resourceLocation, "resource location is null");
            this.addCreativeModeTab(ResourceKey.create(Registries.CREATIVE_MODE_TAB, resourceLocation), value);
        }

        default void addCreativeModeTab(ResourceKey<CreativeModeTab> resourceKey, String value) {
            Objects.requireNonNull(resourceKey, "resource key is null");
            this.add(BuiltInRegistries.CREATIVE_MODE_TAB.get(resourceKey), value);
        }

        default void add(CreativeModeTab tab, String value) {
            Objects.requireNonNull(tab, "tab is null");
            this.add(tab.getDisplayName(), value);
        }

        default void add(Component component, String value) {
            Objects.requireNonNull(component, "component is null");
            if (component.getContents() instanceof TranslatableContents contents) {
                this.add(contents.getKey(), value);
            } else {
                throw new IllegalArgumentException("Unsupported component: " + component);
            }
        }

        default void addGenericDamageType(ResourceKey<DamageType> damageType, String value) {
            Objects.requireNonNull(damageType, "damage type is null");
            this.add("death.attack." + damageType.location().getPath(), value);
        }

        default void addPlayerDamageType(ResourceKey<DamageType> damageType, String value) {
            Objects.requireNonNull(damageType, "damage type is null");
            this.add("death.attack." + damageType.location().getPath() + ".player", value);
        }

        default void addItemDamageType(ResourceKey<DamageType> damageType, String value) {
            Objects.requireNonNull(damageType, "damage type is null");
            this.add("death.attack." + damageType.location().getPath() + ".item", value);
        }
    }
}
