package fuzs.puzzleslib.api.data.v1;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public final class AbstractTagProvider {

    public abstract static class Blocks extends BlockTagsProvider {

        public Blocks(GatherDataEvent evt, String modId) {
            this(evt.getGenerator(), evt.getExistingFileHelper(), modId);
        }

        public Blocks(DataGenerator packOutput, ExistingFileHelper fileHelper, String modId) {
            super(packOutput, modId, fileHelper);
        }

        @Deprecated(forRemoval = true)
        public Blocks(DataGenerator packOutput, String modId, ExistingFileHelper fileHelper) {
            this(packOutput, fileHelper, modId);
        }

        @Override
        protected abstract void addTags();
    }

    public abstract static class Fluids extends FluidTagsProvider {

        public Fluids(GatherDataEvent evt, String modId) {
            this(evt.getGenerator(), evt.getExistingFileHelper(), modId);
        }

        public Fluids(DataGenerator packOutput, ExistingFileHelper fileHelper, String modId) {
            super(packOutput, modId, fileHelper);
        }

        @Deprecated(forRemoval = true)
        public Fluids(DataGenerator packOutput, String modId, ExistingFileHelper fileHelper) {
            this(packOutput, fileHelper, modId);
        }

        @Override
        protected abstract void addTags();
    }

    public abstract static class Items extends ItemTagsProvider {

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
    }

    public abstract static class EntityTypes extends EntityTypeTagsProvider {

        public EntityTypes(GatherDataEvent evt, String modId) {
            this(evt.getGenerator(), evt.getExistingFileHelper(), modId);
        }

        public EntityTypes(DataGenerator packOutput, ExistingFileHelper fileHelper, String modId) {
            super(packOutput, modId, fileHelper);
        }

        @Deprecated(forRemoval = true)
        public EntityTypes(DataGenerator packOutput, String modId, ExistingFileHelper fileHelper) {
            this(packOutput, fileHelper, modId);
        }

        @Override
        protected abstract void addTags();
    }

    public abstract static class GameEvents extends GameEventTagsProvider {

        public GameEvents(GatherDataEvent evt, String modId) {
            this(evt.getGenerator(), evt.getExistingFileHelper(), modId);
        }

        public GameEvents(DataGenerator packOutput, ExistingFileHelper fileHelper, String modId) {
            super(packOutput, modId, fileHelper);
        }

        @Deprecated(forRemoval = true)
        public GameEvents(DataGenerator packOutput, String modId, ExistingFileHelper fileHelper) {
            this(packOutput, fileHelper, modId);
        }

        @Override
        protected abstract void addTags();
    }

    @Deprecated(forRemoval = true)
    public abstract static class Enchantments extends Intrinsic<Enchantment> {

        public Enchantments(DataGenerator packOutput, String modId, ExistingFileHelper fileHelper) {
            super(Registry.ENCHANTMENT_REGISTRY, packOutput, fileHelper, modId);
        }
    }

    public abstract static class Simple<T> extends TagsProvider<T> {

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
    }

    public abstract static class Intrinsic<T> extends IntrinsicHolderTagsProvider<T> {

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
    }
}
