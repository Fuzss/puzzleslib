package fuzs.puzzleslib.api.data.v1;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public final class AbstractTagProvider {

    public abstract static class Blocks extends BlockTagsProvider implements TagProviderExtension<Block, IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block>> {

        public Blocks(GatherDataEvent evt, String modId) {
            this(evt.getGenerator().getPackOutput(), evt.getExistingFileHelper(), modId, evt.getLookupProvider());
        }

        public Blocks(PackOutput packOutput, ExistingFileHelper fileHelper, String modId, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(packOutput, lookupProvider, modId, fileHelper);
        }

        @Deprecated(forRemoval = true)
        public Blocks(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, ExistingFileHelper fileHelper) {
            this(packOutput, fileHelper, modId, lookupProvider);
        }

        @Override
        protected abstract void addTags(HolderLookup.Provider provider);

        @Override
        public ResourceKey<? extends Registry<Block>> registryKey() {
            return this.registryKey;
        }

        @Override
        public IntrinsicTagAppender<Block> tag(TagKey<Block> tagKey) {
            return super.tag(tagKey);
        }
    }

    public abstract static class Fluids extends FluidTagsProvider implements TagProviderExtension<Fluid, IntrinsicHolderTagsProvider.IntrinsicTagAppender<Fluid>> {

        public Fluids(GatherDataEvent evt, String modId) {
            this(evt.getGenerator().getPackOutput(), evt.getExistingFileHelper(), modId, evt.getLookupProvider());
        }

        public Fluids(PackOutput packOutput, ExistingFileHelper fileHelper, String modId, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(packOutput, lookupProvider, modId, fileHelper);
        }

        @Deprecated(forRemoval = true)
        public Fluids(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, ExistingFileHelper fileHelper) {
            this(packOutput, fileHelper, modId, lookupProvider);
        }

        @Override
        protected abstract void addTags(HolderLookup.Provider provider);

        @Override
        public ResourceKey<? extends Registry<Fluid>> registryKey() {
            return this.registryKey;
        }

        @Override
        public IntrinsicTagAppender<Fluid> tag(TagKey<Fluid> tagKey) {
            return super.tag(tagKey);
        }
    }

    public abstract static class Items extends ItemTagsProvider implements TagProviderExtension<Item, IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item>> {

        public Items(GatherDataEvent evt, String modId) {
            this(evt.getGenerator().getPackOutput(), evt.getExistingFileHelper(), modId, evt.getLookupProvider());
        }

        public Items(PackOutput packOutput, ExistingFileHelper fileHelper, String modId, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            this(packOutput, fileHelper, modId, lookupProvider, CompletableFuture.completedFuture(null));
        }

        public Items(GatherDataEvent evt, String modId, CompletableFuture<TagsProvider.TagLookup<Block>> blockTagsProvider) {
            this(evt.getGenerator().getPackOutput(), evt.getExistingFileHelper(), modId, evt.getLookupProvider(), blockTagsProvider);
        }

        public Items(PackOutput packOutput, ExistingFileHelper fileHelper, String modId, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTagsProvider) {
            super(packOutput, lookupProvider, blockTagsProvider, modId, fileHelper);
        }

        @Deprecated(forRemoval = true)
        public Items(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, ExistingFileHelper fileHelper) {
            this(packOutput, fileHelper, modId, lookupProvider);
        }

        @Deprecated(forRemoval = true)
        public Items(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTagsProvider, String modId, ExistingFileHelper fileHelper) {
            this(packOutput, fileHelper, modId, lookupProvider, blockTagsProvider);
        }

        @Override
        protected abstract void addTags(HolderLookup.Provider provider);

        @Deprecated
        @Override
        protected void copy(TagKey<Block> blockTagKey, TagKey<Item> itemTagKey) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ResourceKey<? extends Registry<Item>> registryKey() {
            return this.registryKey;
        }

        @Override
        public IntrinsicTagAppender<Item> tag(TagKey<Item> tagKey) {
            return super.tag(tagKey);
        }
    }

    public abstract static class EntityTypes extends EntityTypeTagsProvider implements TagProviderExtension<EntityType<?>, IntrinsicHolderTagsProvider.IntrinsicTagAppender<EntityType<?>>> {

        public EntityTypes(GatherDataEvent evt, String modId) {
            this(evt.getGenerator().getPackOutput(), evt.getExistingFileHelper(), modId, evt.getLookupProvider());
        }

        public EntityTypes(PackOutput packOutput, ExistingFileHelper fileHelper, String modId, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(packOutput, lookupProvider, modId, fileHelper);
        }

        @Deprecated(forRemoval = true)
        public EntityTypes(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, ExistingFileHelper fileHelper) {
            this(packOutput, fileHelper, modId, lookupProvider);
        }

        @Override
        protected abstract void addTags(HolderLookup.Provider provider);

        @Override
        public ResourceKey<? extends Registry<EntityType<?>>> registryKey() {
            return this.registryKey;
        }

        @Override
        public IntrinsicTagAppender<EntityType<?>> tag(TagKey<EntityType<?>> tagKey) {
            return super.tag(tagKey);
        }
    }

    public abstract static class GameEvents extends GameEventTagsProvider implements TagProviderExtension<GameEvent, IntrinsicHolderTagsProvider.IntrinsicTagAppender<GameEvent>> {

        public GameEvents(GatherDataEvent evt, String modId) {
            this(evt.getGenerator().getPackOutput(), evt.getExistingFileHelper(), modId, evt.getLookupProvider());
        }

        public GameEvents(PackOutput packOutput, ExistingFileHelper fileHelper, String modId, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(packOutput, lookupProvider, modId, fileHelper);
        }

        @Deprecated(forRemoval = true)
        public GameEvents(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, ExistingFileHelper fileHelper) {
            this(packOutput, fileHelper, modId, lookupProvider);
        }

        @Override
        protected abstract void addTags(HolderLookup.Provider provider);

        @Override
        public ResourceKey<? extends Registry<GameEvent>> registryKey() {
            return this.registryKey;
        }

        @Override
        public IntrinsicTagAppender<GameEvent> tag(TagKey<GameEvent> tagKey) {
            return super.tag(tagKey);
        }
    }

    @Deprecated(forRemoval = true)
    public abstract static class DamageTypes extends Simple<DamageType> {

        public DamageTypes(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, ExistingFileHelper fileHelper) {
            super(packOutput, Registries.DAMAGE_TYPE, lookupProvider, modId, fileHelper);
        }
    }

    @Deprecated(forRemoval = true)
    public abstract static class Enchantments extends Intrinsic<Enchantment> {

        public Enchantments(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, ExistingFileHelper fileHelper) {
            super(Registries.ENCHANTMENT, packOutput, fileHelper, modId, lookupProvider);
        }
    }

    public abstract static class Simple<T> extends TagsProvider<T> implements TagProviderExtension<T, TagsProvider.TagAppender<T>> {

        public Simple(ResourceKey<? extends Registry<T>> registryKey, GatherDataEvent evt, String modId) {
            this(registryKey, evt.getGenerator().getPackOutput(), evt.getExistingFileHelper(), modId, evt.getLookupProvider());
        }

        public Simple(ResourceKey<? extends Registry<T>> registryKey, PackOutput packOutput, ExistingFileHelper fileHelper, String modId, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(packOutput, registryKey, lookupProvider, modId, fileHelper);
        }

        @Deprecated(forRemoval = true)
        public Simple(PackOutput packOutput, ResourceKey<? extends Registry<T>> registryKey, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, ExistingFileHelper fileHelper) {
            this(registryKey, packOutput, fileHelper, modId, lookupProvider);
        }

        @Override
        protected abstract void addTags(HolderLookup.Provider provider);

        @Override
        public ResourceKey<? extends Registry<T>> registryKey() {
            return this.registryKey;
        }

        @Override
        public TagAppender<T> tag(TagKey<T> tagKey) {
            return super.tag(tagKey);
        }
    }

    public abstract static class Intrinsic<T> extends IntrinsicHolderTagsProvider<T> implements TagProviderExtension<T, IntrinsicHolderTagsProvider.IntrinsicTagAppender<T>> {

        public Intrinsic(ResourceKey<? extends Registry<T>> registryKey, GatherDataEvent evt, String modId) {
            this(registryKey, evt.getGenerator().getPackOutput(), evt.getExistingFileHelper(), modId, evt.getLookupProvider());
        }

        public Intrinsic(ResourceKey<? extends Registry<T>> registryKey, PackOutput packOutput, ExistingFileHelper fileHelper, String modId, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(packOutput, registryKey, lookupProvider, entity -> {
                try {
                    return lookupProvider.get().lookupOrThrow(registryKey).listElements().filter(holder -> holder.value() == entity).map(Holder.Reference::key).findAny().orElseThrow();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
                // alternatively use this, if the other one breaks
//                return extractKey(entity, registryKey);
            }, modId, fileHelper);
        }

        @Deprecated(forRemoval = true)
        public Intrinsic(ResourceKey<? extends Registry<T>> registryKey, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, ExistingFileHelper fileHelper) {
            this(registryKey, packOutput, fileHelper, modId, lookupProvider);
        }

        @SuppressWarnings("unchecked")
        private static <T> ResourceKey<T> extractKey(T object, ResourceKey<? extends Registry<T>> registryKey) {
            return ((Registry<Registry<T>>) BuiltInRegistries.REGISTRY).getOrThrow((ResourceKey<Registry<T>>) registryKey).getResourceKey(object).orElseThrow();
        }

        @Override
        protected abstract void addTags(HolderLookup.Provider provider);

        @Override
        public ResourceKey<? extends Registry<T>> registryKey() {
            return this.registryKey;
        }

        @Override
        public IntrinsicTagAppender<T> tag(TagKey<T> tagKey) {
            return super.tag(tagKey);
        }
    }

    public interface TagProviderExtension<T, A extends TagsProvider.TagAppender<T>> {

        ResourceKey<? extends Registry<T>> registryKey();

        A tag(TagKey<T> tagKey);

        default A tag(String resourceLocation) {
            return this.tag(new ResourceLocation(resourceLocation));
        }

        default A tag(ResourceLocation resourceLocation) {
            return this.tag(TagKey.create(this.registryKey(), resourceLocation));
        }
    }
}
