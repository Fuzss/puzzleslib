package fuzs.puzzleslib.api.data.v2;

import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class AbstractAdvancementProvider implements DataProvider, AdvancementSubProvider {
    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> registries;
    protected final String modId;

    public AbstractAdvancementProvider(DataProviderContext context) {
        this(context.getModId(), context.getPackOutput(), context.getLookupProvider());
    }

    public AbstractAdvancementProvider(String modId, PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "advancements");
        this.registries = registries;
        this.modId = modId;
    }

    protected static DisplayInfo display(ItemStack itemStack, ResourceLocation resourceLocation) {
        return display(itemStack, resourceLocation, AdvancementType.TASK);
    }

    protected static DisplayInfo display(ItemStack itemStack, ResourceLocation resourceLocation, AdvancementType advancementType) {
        return display(itemStack, resourceLocation, null, advancementType, false);
    }

    protected static DisplayInfo display(ItemStack itemStack, ResourceLocation resourceLocation, @Nullable ResourceLocation background, AdvancementType advancementType, boolean hidden) {
        return display(itemStack, resourceLocation, background, advancementType, true, true, hidden);
    }

    protected static DisplayInfo display(ItemStack itemStack, ResourceLocation resourceLocation, @Nullable ResourceLocation background, AdvancementType advancementType, boolean showToast, boolean announceChat, boolean hidden) {
        AdvancementToken advancementToken = new AdvancementToken(resourceLocation);
        return new DisplayInfo(itemStack,
                advancementToken.title(),
                advancementToken.description(),
                Optional.ofNullable(background),
                advancementType,
                true,
                true,
                hidden
        );
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return this.registries.thenCompose((HolderLookup.Provider provider) -> {
            Set<ResourceLocation> set = new HashSet<>();
            List<CompletableFuture<?>> list = new ArrayList<>();
            Consumer<AdvancementHolder> consumer = (AdvancementHolder advancementHolder) -> {
                ResourceLocation resourceLocation = new ResourceLocation(this.modId, advancementHolder.id().getPath());
                if (!set.add(resourceLocation)) {
                    throw new IllegalStateException("Duplicate advancement " + resourceLocation);
                } else {
                    Path path = this.pathProvider.json(resourceLocation);
                    list.add(DataProvider.saveStable(output, Advancement.CODEC, advancementHolder.value(), path));
                }
            };

            this.generate(provider, consumer);
            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public final void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> writer) {
        this.addAdvancements(registries, writer);
    }

    public abstract void addAdvancements(HolderLookup.Provider registries, Consumer<AdvancementHolder> writer);

    @Override
    public String getName() {
        return "Advancements";
    }

    public record AdvancementToken(ResourceLocation id) {

        public Component title() {
            return Component.translatable(this.id.toLanguageKey("advancements", "title"));
        }

        public Component description() {
            return Component.translatable(this.id.toLanguageKey("advancements", "description"));
        }

        public String name() {
            return this.id.getPath();
        }
    }
}
