package fuzs.puzzleslib.common.api.data.v2;

import fuzs.puzzleslib.common.api.data.v2.core.DataProviderContext;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class AbstractAdvancementProvider implements DataProvider, AdvancementSubProvider {
    private final String modId;
    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public AbstractAdvancementProvider(DataProviderContext context) {
        this(context.getModId(), context.getPackOutput(), context.getRegistries());
    }

    public AbstractAdvancementProvider(String modId, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        this.modId = modId;
        this.pathProvider = packOutput.createRegistryElementsPathProvider(Registries.ADVANCEMENT);
        this.registries = registries;
    }

    protected static DisplayInfoBuilder display(ItemLike icon, Identifier id) {
        return displayV2(new ItemStackTemplate(icon.asItem()), id);
    }

    /**
     * TODO rename method as {@code display}
     */
    protected static DisplayInfoBuilder displayV2(ItemStackTemplate icon, Identifier id) {
        return display(icon, new AdvancementToken(id));
    }

    protected static DisplayInfoBuilder display(ItemLike icon, AdvancementToken token) {
        return display(new ItemStackTemplate(icon.asItem()), token);
    }

    protected static DisplayInfoBuilder display(ItemStackTemplate icon, AdvancementToken token) {
        return display(icon, token.title(), token.description());
    }

    protected static DisplayInfoBuilder display(ItemLike icon, Component title, Component description) {
        return display(new ItemStackTemplate(icon.asItem()), title, description);
    }

    protected static DisplayInfoBuilder display(ItemStackTemplate icon, Component title, Component description) {
        return new DisplayInfoBuilder(icon, title, description);
    }

    @Deprecated(forRemoval = true)
    protected static DisplayInfo display(ItemStackTemplate icon, Identifier id) {
        return displayV2(icon, id).build();
    }

    @Deprecated(forRemoval = true)
    protected static DisplayInfo display(ItemStackTemplate icon, Identifier id, AdvancementType type) {
        return displayV2(icon, id).setType(type).build();
    }

    @Deprecated(forRemoval = true)
    protected static DisplayInfo display(ItemStackTemplate icon, Identifier id, @Nullable Identifier background, AdvancementType type, boolean hidden) {
        return displayV2(icon, id).setBackground(background).setType(type).setHidden(hidden).build();
    }

    @Deprecated(forRemoval = true)
    protected static DisplayInfo display(ItemStackTemplate icon, Identifier id, @Nullable Identifier background, AdvancementType type, boolean showToast, boolean announceChat, boolean hidden) {
        return displayV2(icon, id).setBackground(background)
                .setType(type)
                .setShowToast(showToast)
                .setAnnounceChat(announceChat)
                .setHidden(hidden)
                .build();
    }

    @Override
    public final CompletableFuture<?> run(CachedOutput output) {
        return this.registries.thenCompose((HolderLookup.Provider context) -> {
            Set<Identifier> set = new HashSet<>();
            List<CompletableFuture<?>> futures = new ArrayList<>();
            Consumer<AdvancementHolder> writer = (AdvancementHolder holder) -> {
                Identifier id = Identifier.fromNamespaceAndPath(this.modId, holder.id().getPath());
                if (!set.add(id)) {
                    throw new IllegalStateException("Duplicate advancement " + id);
                } else {
                    Path path = this.pathProvider.json(id);
                    futures.add(DataProvider.saveStable(output, context, Advancement.CODEC, holder.value(), path));
                }
            };

            this.generate(context, writer);
            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public final void generate(HolderLookup.Provider context, Consumer<AdvancementHolder> writer) {
        this.addAdvancements(context, writer);
    }

    public abstract void addAdvancements(HolderLookup.Provider context, Consumer<AdvancementHolder> writer);

    @Override
    public String getName() {
        return "Advancements";
    }

    public record AdvancementToken(Identifier id) {

        public Component title() {
            return Component.translatable(this.id.toLanguageKey("advancements", "title").replace('/', '.'));
        }

        public Component description() {
            return Component.translatable(this.id.toLanguageKey("advancements", "description").replace('/', '.'));
        }

        @Deprecated(forRemoval = true)
        public AdvancementHolder asParent() {
            // workaround for getting the proper parent id,
            // the advancement holder from saving the builder always has the `minecraft` namespace set
            // (which we replace when running the data generator, which happens later though)
            return new AdvancementHolder(this.id, null);
        }

        public String name() {
            return this.id.toString();
        }
    }

    /**
     * @see DisplayInfo
     */
    public static class DisplayInfoBuilder {
        private final Component title;
        private final Component description;
        private final ItemStackTemplate icon;
        private Optional<ClientAsset.ResourceTexture> background = Optional.empty();
        private AdvancementType type = AdvancementType.TASK;
        private boolean showToast = true;
        private boolean announceChat = true;
        private boolean hidden = false;

        DisplayInfoBuilder(ItemStackTemplate icon, Component title, Component description) {
            this.icon = icon;
            this.title = title;
            this.description = description;
        }

        /**
         * @param background texture location for the advancement tab background
         * @return the builder
         */
        public DisplayInfoBuilder setBackground(@Nullable Identifier background) {
            this.background = Optional.ofNullable(background).map(ClientAsset.ResourceTexture::new);
            return this;
        }

        /**
         * @param background texture for the advancement tab background
         * @return the builder
         */
        public DisplayInfoBuilder setBackground(ClientAsset.@Nullable ResourceTexture background) {
            this.background = Optional.ofNullable(background);
            return this;
        }

        /**
         * @param type the advancement type, controlling frame and reward appearance
         * @return the builder
         */
        public DisplayInfoBuilder setType(AdvancementType type) {
            Objects.requireNonNull(type, "type is null");
            this.type = type;
            return this;
        }

        /**
         * @param showToast if a toast is shown when the advancement is completed
         * @return the builder
         */
        public DisplayInfoBuilder setShowToast(boolean showToast) {
            this.showToast = showToast;
            return this;
        }

        /**
         * @param announceChat if completing, the advancement is announced in chat
         * @return the builder
         */
        public DisplayInfoBuilder setAnnounceChat(boolean announceChat) {
            this.announceChat = announceChat;
            return this;
        }

        /**
         * @param hidden if the advancement is hidden until it is completed
         * @return the builder
         */
        public DisplayInfoBuilder setHidden(boolean hidden) {
            this.hidden = hidden;
            return this;
        }

        /**
         * Creates the display info from this builder.
         *
         * @return the display info
         */
        public DisplayInfo build() {
            return new DisplayInfo(this.icon,
                    this.title,
                    this.description,
                    this.background,
                    this.type,
                    this.showToast,
                    this.announceChat,
                    this.hidden);
        }
    }
}
