package fuzs.puzzleslib.api.data.v1;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public final class AbstractTagProvider {

    public abstract static class Blocks extends BlockTagsProvider {

        public Blocks(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, ExistingFileHelper fileHelper) {
            super(packOutput, lookupProvider, modId, fileHelper);
        }

        @Override
        protected abstract void addTags(HolderLookup.Provider provider);
    }

    public abstract static class Items extends ItemTagsProvider {

        public Items(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, ExistingFileHelper fileHelper) {
            super(packOutput, lookupProvider, new Blocks(packOutput, lookupProvider, modId, fileHelper) {

                @Override
                protected void addTags(HolderLookup.Provider provider) {

                }
            }.contentsGetter(), modId, fileHelper);
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

    public abstract static class DamageTypes extends DamageTypeTagsProvider {

        public DamageTypes(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(packOutput, lookupProvider);
        }

        @Override
        protected abstract void addTags(HolderLookup.Provider provider);
    }
}
