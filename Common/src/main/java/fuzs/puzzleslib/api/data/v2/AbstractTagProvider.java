package fuzs.puzzleslib.api.data.v2;

import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.init.v3.RegistryHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;

import java.util.concurrent.CompletableFuture;

public final class AbstractTagProvider {

    public abstract static class Blocks extends VanillaBlockTagsProvider implements TagProviderExtension<Block, IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block>> {

        public Blocks(DataProviderContext context) {
            this(context.getModId(), context.getPackOutput(), context.getLookupProvider());
        }

        public Blocks(String modId, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(packOutput, lookupProvider);
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

        public Fluids(DataProviderContext context) {
            this(context.getPackOutput(), context.getLookupProvider());
        }

        public Fluids(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(packOutput, lookupProvider);
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

        public Items(DataProviderContext context) {
            this(context.getPackOutput(), context.getLookupProvider());
        }

        public Items(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            this(packOutput, lookupProvider, CompletableFuture.completedFuture(null));
        }

        public Items(DataProviderContext context, CompletableFuture<TagLookup<Block>> blockTagsProvider) {
            this(context.getPackOutput(), context.getLookupProvider(), blockTagsProvider);
        }

        public Items(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagsProvider) {
            super(packOutput, lookupProvider, blockTagsProvider);
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

        public EntityTypes(DataProviderContext context) {
            this(context.getPackOutput(), context.getLookupProvider());
        }

        public EntityTypes(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(packOutput, lookupProvider);
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

        public GameEvents(DataProviderContext context) {
            this(context.getPackOutput(), context.getLookupProvider());
        }

        public GameEvents(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(packOutput, lookupProvider);
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

    public abstract static class Simple<T> extends TagsProvider<T> implements TagProviderExtension<T, TagsProvider.TagAppender<T>> {

        public Simple(ResourceKey<? extends Registry<T>> registryKey, DataProviderContext context) {
            this(registryKey, context.getPackOutput(), context.getLookupProvider());
        }

        public Simple(ResourceKey<? extends Registry<T>> registryKey, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(packOutput, registryKey, lookupProvider);
        }

        @Override
        protected abstract void addTags(HolderLookup.Provider provider);

        @Override
        public ResourceKey<? extends Registry<T>> registryKey() {
            return this.registryKey;
        }

        @Override
        public TagsProvider.TagAppender<T> tag(TagKey<T> tagKey) {
            return super.tag(tagKey);
        }
    }

    public abstract static class Intrinsic<T> extends IntrinsicHolderTagsProvider<T> implements TagProviderExtension<T, IntrinsicHolderTagsProvider.IntrinsicTagAppender<T>> {

        public Intrinsic(ResourceKey<? extends Registry<T>> registryKey, DataProviderContext context) {
            this(registryKey, context.getPackOutput(), context.getLookupProvider());
        }

        public Intrinsic(ResourceKey<? extends Registry<T>> registryKey, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(packOutput, registryKey, lookupProvider, object -> {
                return RegistryHelper.getResourceKeyOrThrow(registryKey, object);
            });
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

        default A tag(String identifier) {
            return this.tag(new ResourceLocation(identifier));
        }

        default A tag(ResourceLocation identifier) {
            return this.tag(TagKey.create(this.registryKey(), identifier));
        }
    }
}
