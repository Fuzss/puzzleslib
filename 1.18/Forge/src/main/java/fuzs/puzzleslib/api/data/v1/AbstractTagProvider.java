package fuzs.puzzleslib.api.data.v1;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

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
            this(evt.getGenerator(), evt.getExistingFileHelper(), modId);
        }

        public Items(DataGenerator packOutput, ExistingFileHelper fileHelper, String modId) {
            this(packOutput, fileHelper, modId, new BlockTagsProvider(packOutput, modId, fileHelper) {

                @Override
                protected void addTags() {

                }
            });
        }

        public Items(GatherDataEvent evt, String modId, BlockTagsProvider blockTagsProvider) {
            this(evt.getGenerator(), evt.getExistingFileHelper(), modId, blockTagsProvider);
        }

        public Items(DataGenerator packOutput, ExistingFileHelper fileHelper, String modId, BlockTagsProvider blockTagsProvider) {
            super(packOutput, blockTagsProvider, modId, fileHelper);
        }

        @Deprecated(forRemoval = true)
        public Items(DataGenerator packOutput, String modId, ExistingFileHelper fileHelper) {
            this(packOutput, fileHelper, modId);
        }

        @Deprecated(forRemoval = true)
        public Items(DataGenerator packOutput, BlockTagsProvider blockTagsProvider, String modId, ExistingFileHelper fileHelper) {
            this(packOutput, fileHelper, modId, blockTagsProvider);
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

    public abstract static class Simple<T> extends TagsProvider<T> {

        public Simple(Registry<T> registryKey, GatherDataEvent evt, String modId) {
            this(registryKey, evt.getGenerator(), evt.getExistingFileHelper(), modId);
        }

        public Simple(Registry<T> registryKey, DataGenerator packOutput, ExistingFileHelper fileHelper, String modId) {
            super(packOutput, registryKey, modId, fileHelper);
        }

        @Deprecated(forRemoval = true)
        public Simple(DataGenerator packOutput, Registry<T> registryKey, String modId, ExistingFileHelper fileHelper) {
            this(registryKey, packOutput, fileHelper, modId);
        }

        @Override
        protected abstract void addTags();

        @Override
        public final String getName() {
            return "Tags for " + ((Registry<Registry<T>>) Registry.REGISTRY).getKey(this.registry);
        }
    }
}
