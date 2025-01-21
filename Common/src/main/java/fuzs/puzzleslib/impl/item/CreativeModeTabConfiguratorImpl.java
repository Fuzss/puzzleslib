package fuzs.puzzleslib.impl.item;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.item.v2.CreativeModeTabConfigurator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public final class CreativeModeTabConfiguratorImpl implements CreativeModeTabConfigurator {
    private final ResourceLocation resourceLocation;
    @Nullable
    private Supplier<ItemStack> icon;
    @Nullable
    private Supplier<ItemStack[]> icons;
    @Nullable
    private CreativeModeTab.DisplayItemsGenerator displayItemsGenerator;
    private boolean hasSearchBar;
    private boolean appendEnchantmentsAndPotions;

    public CreativeModeTabConfiguratorImpl(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    public ResourceLocation getResourceLocation() {
        return this.resourceLocation;
    }

    @Nullable
    public Supplier<ItemStack[]> getIcons() {
        return this.icons;
    }

    public boolean hasSearchBar() {
        return this.hasSearchBar;
    }

    @Override
    public CreativeModeTabConfigurator icon(Supplier<ItemStack> icon) {
        this.icon = icon;
        return this;
    }

    @Override
    public CreativeModeTabConfigurator icons(Supplier<ItemStack[]> icons) {
        this.icons = icons;
        return this;
    }

    @Override
    public CreativeModeTabConfigurator displayItems(CreativeModeTab.DisplayItemsGenerator generator) {
        this.displayItemsGenerator = generator;
        return this;
    }

    @Override
    public CreativeModeTabConfigurator withSearchBar() {
        this.hasSearchBar = true;
        return this;
    }

    @Override
    public CreativeModeTabConfigurator appendEnchantmentsAndPotions() {
        this.appendEnchantmentsAndPotions = true;
        return this;
    }

    public void configure(CreativeModeTab.Builder builder) {
        String translationKey = "itemGroup.%s.%s".formatted(this.resourceLocation.getNamespace(),
                this.resourceLocation.getPath());
        builder.title(Component.translatable(translationKey));
        if (this.icon != null) {
            builder.icon(this.icon);
        } else {
            Objects.requireNonNull(this.icons, "both icon suppliers are null");
            // since no single icon is set and multiple icons are only processed on Forge, this would otherwise be an empty icon for Fabric / Quilt
            if (ModLoaderEnvironment.INSTANCE.getModLoader().isFabricLike()) {
                builder.icon(() -> {
                    ItemStack[] icons = this.icons.get();
                    Preconditions.checkState(icons.length > 0, "icons is empty");
                    return icons[0];
                });
            }
        }
        builder.displayItems((CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) -> {
            String modId = this.resourceLocation.getNamespace();
            if (this.displayItemsGenerator != null) {
                this.displayItemsGenerator.accept(itemDisplayParameters, output);
            } else {
                CreativeModeTabHelper.appendAllItems(modId, itemDisplayParameters.holders(), output::accept);
            }
            if (this.appendEnchantmentsAndPotions) {
                CreativeModeTabHelper.appendAllEnchantments(modId, itemDisplayParameters.holders(), output::accept);
                CreativeModeTabHelper.appendAllPotions(modId, itemDisplayParameters.holders(), output::accept);
            }
        });
    }
}
