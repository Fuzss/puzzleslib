package fuzs.puzzleslib.api.data.v1;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

public final class AbstractTagProvider {

    public abstract static class Blocks extends BlockTagsProvider implements TagProviderExtension<Block> {

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

        @Override
        public Registry<Block> registry() {
            return this.registry;
        }

        @Override
        public TagAppender<Block> tag(TagKey<Block> tagKey) {
            return super.tag(tagKey);
        }
    }

    public abstract static class Fluids extends FluidTagsProvider implements TagProviderExtension<Fluid> {

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

        @Override
        public Registry<Fluid> registry() {
            return this.registry;
        }

        @Override
        public TagAppender<Fluid> tag(TagKey<Fluid> tagKey) {
            return super.tag(tagKey);
        }
    }

    public abstract static class Items extends ItemTagsProvider implements TagProviderExtension<Item> {

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

        @Override
        public Registry<Item> registry() {
            return this.registry;
        }

        @Override
        public TagAppender<Item> tag(TagKey<Item> tagKey) {
            return super.tag(tagKey);
        }
    }

    public abstract static class EntityTypes extends EntityTypeTagsProvider implements TagProviderExtension<EntityType<?>> {

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

        @Override
        public Registry<EntityType<?>> registry() {
            return this.registry;
        }

        @Override
        public TagAppender<EntityType<?>> tag(TagKey<EntityType<?>> tagKey) {
            return super.tag(tagKey);
        }
    }

    public abstract static class GameEvents extends GameEventTagsProvider implements TagProviderExtension<GameEvent> {

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

        @Override
        public Registry<GameEvent> registry() {
            return this.registry;
        }

        @Override
        public TagAppender<GameEvent> tag(TagKey<GameEvent> tagKey) {
            return super.tag(tagKey);
        }
    }

    public abstract static class Simple<T> extends TagsProvider<T> implements TagProviderExtension<T> {

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
            return "Tags for " + this.registryKey().location();
        }

        @Override
        public Registry<T> registry() {
            return this.registry;
        }

        @Override
        public TagAppender<T> tag(TagKey<T> tagKey) {
            return super.tag(tagKey);
        }
    }

    public interface TagProviderExtension<T> {

        Registry<T> registry();

        @SuppressWarnings("unchecked")
        default ResourceKey<? extends Registry<T>> registryKey() {
            return ((Registry<Registry<T>>) Registry.REGISTRY).getResourceKey(this.registry()).orElseThrow();
        }

        TagsProvider.TagAppender<T> tag(TagKey<T> tagKey);

        default TagsProvider.TagAppender<T> tag(String resourceLocation) {
            return this.tag(new ResourceLocation(resourceLocation));
        }

        default TagsProvider.TagAppender<T> tag(ResourceLocation resourceLocation) {
            return this.tag(TagKey.create(this.registryKey(), resourceLocation));
        }
    }
}
