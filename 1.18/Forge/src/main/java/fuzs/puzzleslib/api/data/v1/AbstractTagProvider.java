package fuzs.puzzleslib.api.data.v1;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.tags.GameEventTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

public final class AbstractTagProvider {

    public abstract static class Blocks extends BlockTagsProvider {

        public Blocks(DataGenerator packOutput, String modId, ExistingFileHelper fileHelper) {
            super(packOutput, modId, fileHelper);
        }

        @Override
        protected abstract void addTags();
    }

    public abstract static class Items extends ItemTagsProvider {

        public Items(DataGenerator packOutput, String modId, ExistingFileHelper fileHelper) {
            super(packOutput, new BlockTagsProvider(packOutput, modId, fileHelper) {

                @Override
                protected void addTags() {

                }
            }, modId, fileHelper);
        }

        public Items(DataGenerator packOutput, BlockTagsProvider blockTagsProvider, String modId, ExistingFileHelper fileHelper) {
            super(packOutput, blockTagsProvider, modId, fileHelper);
        }

        @Override
        protected abstract void addTags();

        @Deprecated
        @Override
        protected void copy(TagKey<Block> blockTagKey, TagKey<Item> itemTagKey) {
            throw new UnsupportedOperationException();
        }
    }

    public abstract static class EntityTypes extends EntityTypeTagsProvider {

        public EntityTypes(DataGenerator packOutput, String modId, ExistingFileHelper fileHelper) {
            super(packOutput, modId, fileHelper);
        }

        @Override
        protected abstract void addTags();
    }

    public abstract static class GameEvents extends GameEventTagsProvider {

        public GameEvents(DataGenerator packOutput, String modId, ExistingFileHelper fileHelper) {
            super(packOutput, modId, fileHelper);
        }

        @Override
        protected abstract void addTags();
    }
}
