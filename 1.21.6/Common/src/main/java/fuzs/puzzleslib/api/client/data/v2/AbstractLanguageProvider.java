package fuzs.puzzleslib.api.client.data.v2;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.BlockFamily;
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
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

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

    public abstract void addTranslations(TranslationBuilder translationBuilder);

    @Override
    public CompletableFuture<?> run(CachedOutput writer) {

        JsonObject jsonObject = new JsonObject();
        this.addTranslations((String translationKey, String value) -> {
            Objects.requireNonNull(translationKey, "translation key is null");
            Objects.requireNonNull(value, "value is null");
            if (jsonObject.has(translationKey)) {
                throw new IllegalStateException("Created duplicate translation key: " + translationKey);
            } else {
                jsonObject.addProperty(translationKey, value);
            }
        });

        this.verifyRequiredTranslationKeys(jsonObject::has, BuiltInRegistries.BLOCK, TranslationBuilder::addBlock);
        this.verifyRequiredTranslationKeys(jsonObject::has, BuiltInRegistries.ITEM, TranslationBuilder::addItem);
        this.verifyRequiredTranslationKeys(jsonObject::has,
                BuiltInRegistries.ENTITY_TYPE,
                TranslationBuilder::addEntityType);
        this.verifyRequiredTranslationKeys(jsonObject::has,
                BuiltInRegistries.ATTRIBUTE,
                TranslationBuilder::addAttribute);
        this.verifyRequiredTranslationKeys(jsonObject::has,
                BuiltInRegistries.MOB_EFFECT,
                TranslationBuilder::addMobEffect);

        return DataProvider.saveStable(writer,
                jsonObject,
                this.pathProvider.json(ResourceLocationHelper.fromNamespaceAndPath(this.modId, this.languageCode)));
    }

    private <T> void verifyRequiredTranslationKeys(Predicate<String> predicate, Registry<T> registry, HolderTranslationCollector<T> holderTranslationCollector) {
        registry.listElements()
                .filter((Holder.Reference<T> holder) -> holder.key().location().getNamespace().equals(this.modId))
                .forEach((Holder.Reference<T> holder) -> {
                    holderTranslationCollector.accept((String translationKey, String value) -> {
                        Objects.requireNonNull(translationKey, "translation key is null");
                        if (this.mustHaveTranslationKey(holder, translationKey) && !predicate.test(translationKey)) {
                            throw new IllegalStateException("Missing translation key '%s' for '%s'".formatted(
                                    translationKey,
                                    holder));
                        }
                    }, holder, "");
                });
    }

    protected boolean mustHaveTranslationKey(Holder.Reference<?> holder, String translationKey) {
        return true;
    }

    @Override
    public String getName() {
        return "Language (" + this.languageCode + ")";
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

        default void add(Holder<?> holder, String value) {
            this.add(holder, "", value);
        }

        default void add(Holder<?> holder, String additionalKey, String value) {
            Objects.requireNonNull(holder, "holder is null");
            this.add(holder.unwrapKey().orElseThrow(), additionalKey, value);
        }

        default void add(ResourceKey<?> resourceKey, String value) {
            this.add(resourceKey, "", value);
        }

        default void add(ResourceKey<?> resourceKey, String additionalKey, String value) {
            Objects.requireNonNull(resourceKey, "resource key is null");
            this.add(Registries.elementsDirPath(resourceKey.registryKey()),
                    resourceKey.location(),
                    additionalKey,
                    value);
        }

        default void add(String registry, ResourceLocation resourceLocation, String value) {
            this.add(registry, resourceLocation, "", value);
        }

        default void add(String registry, ResourceLocation resourceLocation, String additionalKey, String value) {
            Objects.requireNonNull(registry, "registry is null");
            Objects.requireNonNull(resourceLocation, "resource location is null");
            String translationKey = Util.makeDescriptionId(registry, resourceLocation);
            this.add(translationKey, additionalKey, value);
        }

        default void add(TagKey<?> tagKey, String value) {
            String descriptionId = Util.makeDescriptionId(Registries.elementsDirPath(tagKey.registry()),
                    tagKey.location());
            this.add("tag." + descriptionId, value);
        }

        default BlockFamilyBuilder blockFamily(String blockValue) {
            return new BlockFamilyBuilder(this::add, blockValue);
        }

        default BlockFamilyBuilder blockFamily(String blockValue, String baseBlockValue) {
            return new BlockFamilyBuilder(this::add, blockValue, baseBlockValue);
        }

        default void addBlock(Holder<Block> block, String value) {
            this.add(block.value(), value);
        }

        default void add(Block block, String value) {
            this.add(block, "", value);
        }

        default void add(Block block, String additionalKey, String value) {
            Objects.requireNonNull(block, "block is null");
            this.add(block.getDescriptionId(), additionalKey, value);
        }

        default void addItem(Holder<Item> item, String value) {
            this.add(item.value(), value);
        }

        default void add(Item item, String value) {
            this.add(item, "", value);
        }

        default void add(Item item, String additionalKey, String value) {
            Objects.requireNonNull(item, "item is null");
            this.add(item.getDescriptionId(), additionalKey, value);
        }

        default void addSpawnEgg(Item item, String value) {
            if (item instanceof SpawnEggItem) {
                this.add(item, value + " Spawn Egg");
            } else {
                throw new IllegalArgumentException("Unsupported item: " + item);
            }
        }

        default void addMobEffect(Holder<MobEffect> mobEffect, String value) {
            Objects.requireNonNull(mobEffect, "mob effect is null");
            this.add(mobEffect.value(), value);
        }

        default void add(MobEffect mobEffect, String value) {
            this.add(mobEffect, "", value);
        }

        default void add(MobEffect mobEffect, String additionalKey, String value) {
            Objects.requireNonNull(mobEffect, "mob effect is null");
            this.add(mobEffect.getDescriptionId(), additionalKey, value);
        }

        default void addEntityType(Holder<? extends EntityType<?>> entityType, String value) {
            this.add(entityType.value(), value);
        }

        default void add(EntityType<?> entityType, String value) {
            this.add(entityType, "", value);
        }

        default void add(EntityType<?> entityType, String additionalKey, String value) {
            Objects.requireNonNull(entityType, "entity type is null");
            this.add(entityType.getDescriptionId(), additionalKey, value);
        }

        default void addAttribute(Holder<Attribute> attribute, String value) {
            this.add(attribute.value(), value);
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
            Function<Item, Component> potionNameGetter = (Item item) -> {
                return new PotionContents(potion).getName(item.getDescriptionId() + ".effect.");
            };
            this.add(potionNameGetter.apply(Items.TIPPED_ARROW), "Arrow of " + value);
            this.add(potionNameGetter.apply(Items.POTION), "Potion of " + value);
            this.add(potionNameGetter.apply(Items.SPLASH_POTION), "Splash Potion of " + value);
            this.add(potionNameGetter.apply(Items.LINGERING_POTION), "Lingering Potion of " + value);
        }

        default void addSoundEvent(Holder<SoundEvent> soundEvent, String value) {
            this.add(soundEvent.value(), value);
        }

        default void add(SoundEvent soundEvent, String value) {
            Objects.requireNonNull(soundEvent, "sound event is null");
            this.add("subtitles." + soundEvent.location().getPath(), value);
        }

        default void addCreativeModeTab(Holder<CreativeModeTab> creativeModeTab, String value) {
            Objects.requireNonNull(creativeModeTab, "creative mode tab is null");
            this.add(creativeModeTab.value(), value);
        }

        default void add(CreativeModeTab creativeModeTab, String value) {
            Objects.requireNonNull(creativeModeTab, "creative mode tab is null");
            this.add(creativeModeTab.getDisplayName(), value);
        }

        default void add(KeyMapping keyMapping, String value) {
            Objects.requireNonNull(keyMapping, "key mapping is null");
            this.add(keyMapping.getName(), value);
        }

        default void addKeyCategory(String modId, String value) {
            this.add("key.categories." + modId, value);
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

        default void addPaintingVariant(ResourceKey<PaintingVariant> paintingVariant, String title, String author) {
            Objects.requireNonNull(paintingVariant, "painting variant is null");
            // do not use the registry name, it is "painting_variant", not "painting"
            this.add(paintingVariant.location().toLanguageKey("painting", "title"), title);
            this.add(paintingVariant.location().toLanguageKey("painting", "author"), author);
        }
    }

    public static class BlockFamilyBuilder {
        static final Map<BlockFamily.Variant, BiFunction<BlockFamilyBuilder, Block, BlockFamilyBuilder>> VARIANT_FUNCTIONS = ImmutableMap.<BlockFamily.Variant, BiFunction<BlockFamilyBuilder, Block, BlockFamilyBuilder>>builder()
                .put(BlockFamily.Variant.BUTTON, BlockFamilyBuilder::button)
                .put(BlockFamily.Variant.CHISELED, BlockFamilyBuilder::chiseled)
                .put(BlockFamily.Variant.CRACKED, BlockFamilyBuilder::cracked)
                .put(BlockFamily.Variant.CUT, BlockFamilyBuilder::cut)
                .put(BlockFamily.Variant.DOOR, BlockFamilyBuilder::door)
                .put(BlockFamily.Variant.CUSTOM_FENCE, BlockFamilyBuilder::fence)
                .put(BlockFamily.Variant.FENCE, BlockFamilyBuilder::fence)
                .put(BlockFamily.Variant.CUSTOM_FENCE_GATE, BlockFamilyBuilder::fenceGate)
                .put(BlockFamily.Variant.FENCE_GATE, BlockFamilyBuilder::fenceGate)
                .put(BlockFamily.Variant.MOSAIC, BlockFamilyBuilder::mosaic)
                .put(BlockFamily.Variant.SIGN, BlockFamilyBuilder::sign)
                .put(BlockFamily.Variant.SLAB, BlockFamilyBuilder::slab)
                .put(BlockFamily.Variant.STAIRS, BlockFamilyBuilder::stairs)
                .put(BlockFamily.Variant.PRESSURE_PLATE, BlockFamilyBuilder::pressurePlate)
                .put(BlockFamily.Variant.POLISHED, BlockFamilyBuilder::polished)
                .put(BlockFamily.Variant.TRAPDOOR, BlockFamilyBuilder::trapdoor)
                .put(BlockFamily.Variant.WALL, BlockFamilyBuilder::wall)
                .build();
        private final BiConsumer<Block, String> valueConsumer;
        private final String blockValue;
        private final String baseBlockValue;

        private BlockFamilyBuilder(BiConsumer<Block, String> valueConsumer, String blockValue) {
            this(valueConsumer, blockValue, blockValue);
        }

        private BlockFamilyBuilder(BiConsumer<Block, String> valueConsumer, String blockValue, String baseBlockValue) {
            this.valueConsumer = valueConsumer;
            this.blockValue = blockValue;
            this.baseBlockValue = baseBlockValue;
        }

        public void generateFor(BlockFamily blockFamily) {
            this.baseBlock(blockFamily.getBaseBlock());
            blockFamily.getVariants().forEach((BlockFamily.Variant variant, Block block) -> {
                BiFunction<BlockFamilyBuilder, Block, BlockFamilyBuilder> variantFunction = VARIANT_FUNCTIONS.get(
                        variant);
                if (variantFunction != null) {
                    variantFunction.apply(this, block);
                }
            });
        }

        public BlockFamilyBuilder baseBlock(Block block) {
            this.valueConsumer.accept(block, this.baseBlockValue);
            return this;
        }

        public BlockFamilyBuilder button(Block block) {
            this.valueConsumer.accept(block, this.blockValue + " Button");
            return this;
        }

        public BlockFamilyBuilder chiseled(Block block) {
            this.valueConsumer.accept(block, "Chiseled " + this.blockValue);
            return this;
        }

        public BlockFamilyBuilder cracked(Block block) {
            this.valueConsumer.accept(block, "Cracked " + this.blockValue);
            return this;
        }

        public BlockFamilyBuilder cut(Block block) {
            this.valueConsumer.accept(block, "Cut " + this.blockValue);
            return this;
        }

        public BlockFamilyBuilder door(Block block) {
            this.valueConsumer.accept(block, this.blockValue + " Door");
            return this;
        }

        public BlockFamilyBuilder fence(Block block) {
            this.valueConsumer.accept(block, this.blockValue + " Fence");
            return this;
        }

        public BlockFamilyBuilder fenceGate(Block block) {
            this.valueConsumer.accept(block, this.blockValue + " Fence Gate");
            return this;
        }

        public BlockFamilyBuilder mosaic(Block block) {
            this.valueConsumer.accept(block, "Mosaic " + this.blockValue);
            return this;
        }

        public BlockFamilyBuilder sign(Block block) {
            this.valueConsumer.accept(block, this.blockValue + " Sign");
            return this;
        }

        public BlockFamilyBuilder slab(Block block) {
            this.valueConsumer.accept(block, this.blockValue + " Slab");
            return this;
        }

        public BlockFamilyBuilder hangingSign(Block block) {
            this.valueConsumer.accept(block, this.blockValue + " Hanging Sign");
            return this;
        }

        public BlockFamilyBuilder stairs(Block block) {
            this.valueConsumer.accept(block, this.blockValue + " Stairs");
            return this;
        }

        public BlockFamilyBuilder pressurePlate(Block block) {
            this.valueConsumer.accept(block, this.blockValue + " Pressure Plate");
            return this;
        }

        public BlockFamilyBuilder polished(Block block) {
            this.valueConsumer.accept(block, "Polished " + this.blockValue);
            return this;
        }

        public BlockFamilyBuilder trapdoor(Block block) {
            this.valueConsumer.accept(block, this.blockValue + " Trapdoor");
            return this;
        }

        public BlockFamilyBuilder wall(Block block) {
            this.valueConsumer.accept(block, this.blockValue + " Wall");
            return this;
        }
    }

    @FunctionalInterface
    protected interface HolderTranslationCollector<T> {

        void accept(TranslationBuilder translationBuilder, Holder<T> holder, String value);
    }
}
