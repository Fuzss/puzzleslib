package fuzs.puzzleslib.api.data.v1;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public final class AbstractTagProvider {

    public abstract static class Blocks extends BlockTagsProvider {

        public Blocks(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, ExistingFileHelper fileHelper) {
            super(packOutput, lookupProvider, modId, fileHelper);
        }

        @Override
        protected abstract void addTags(HolderLookup.Provider provider);
    }

    public abstract static class Fluids extends FluidTagsProvider {

        public Fluids(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, ExistingFileHelper fileHelper) {
            super(packOutput, lookupProvider, modId, fileHelper);
        }

        @Override
        protected abstract void addTags(HolderLookup.Provider provider);
    }

    public abstract static class Items extends ItemTagsProvider {

        public Items(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, ExistingFileHelper fileHelper) {
            super(packOutput, lookupProvider, CompletableFuture.completedFuture(null), modId, fileHelper);
        }

        public Items(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTagsProvider, String modId, ExistingFileHelper fileHelper) {
            super(packOutput, lookupProvider, blockTagsProvider, modId, fileHelper);
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

        public EntityTypes(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, ExistingFileHelper fileHelper) {
            super(packOutput, lookupProvider, modId, fileHelper);
        }

        @Override
        protected abstract void addTags(HolderLookup.Provider provider);
    }

    public abstract static class GameEvents extends GameEventTagsProvider {

        public GameEvents(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, ExistingFileHelper fileHelper) {
            super(packOutput, lookupProvider, modId, fileHelper);
        }

        @Override
        protected abstract void addTags(HolderLookup.Provider provider);
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
            super(packOutput, Registries.ENCHANTMENT, lookupProvider, modId, fileHelper);
        }
    }

    public abstract static class Simple<T> extends TagsProvider<T> {

        public Simple(PackOutput packOutput, ResourceKey<? extends Registry<T>> registryKey, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, ExistingFileHelper fileHelper) {
            super(packOutput, registryKey, lookupProvider, modId, fileHelper);
        }

        @Override
        protected abstract void addTags(HolderLookup.Provider provider);
    }

    public abstract static class Intrinsic<T> extends IntrinsicHolderTagsProvider<T> {

        public Intrinsic(PackOutput packOutput, ResourceKey<? extends Registry<T>> registryKey, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, ExistingFileHelper fileHelper) {
            super(packOutput, registryKey, lookupProvider, entity -> {
                // alternatively use this, but the current method is definitely preferred
//                return extractKey(entity, registryKey);
                try {
                    return lookupProvider.get().lookupOrThrow(registryKey).listElements().filter(holder -> holder.value() == entity).map(Holder.Reference::key).findAny().orElseThrow();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }, modId, fileHelper);
        }

        @SuppressWarnings("unchecked")
        private static <T> ResourceKey<T> extractKey(T object, ResourceKey<? extends Registry<T>> registryKey) {
            return ((Registry<Registry<T>>) BuiltInRegistries.REGISTRY).getOrThrow((ResourceKey<Registry<T>>) registryKey).getResourceKey(object).orElseThrow();
        }

        @Override
        protected abstract void addTags(HolderLookup.Provider provider);
    }
}
