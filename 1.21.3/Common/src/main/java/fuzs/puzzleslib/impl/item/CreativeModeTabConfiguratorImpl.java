package fuzs.puzzleslib.impl.item;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.item.v2.CreativeModeTabConfigurator;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class CreativeModeTabConfiguratorImpl implements CreativeModeTabConfigurator {
    private static final Item[] POTION_ITEMS = new Item[]{
            Items.POTION,
            Items.SPLASH_POTION,
            Items.LINGERING_POTION,
            Items.TIPPED_ARROW
    };

    private final ResourceLocation resourceLocation;
    @Nullable
    private Supplier<ItemStack> icon;
    @Nullable
    private Supplier<ItemStack[]> icons;
    private CreativeModeTab.DisplayItemsGenerator displayItemsGenerator = (CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) -> {
        // NO-OP
    };
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

    public boolean isHasSearchBar() {
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
                this.resourceLocation.getPath()
        );
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
        if (this.appendEnchantmentsAndPotions) {
            builder.displayItems(
                    (CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) -> {
                        this.displayItemsGenerator.accept(itemDisplayParameters, output);
                        appendAllEnchantments(this.resourceLocation.getNamespace(), itemDisplayParameters.holders(),
                                output::accept
                        );
                        appendAllPotions(this.resourceLocation.getNamespace(), itemDisplayParameters.holders(),
                                output::accept
                        );
                    });
        } else {
            builder.displayItems(this.displayItemsGenerator);
        }
    }

    private static void appendAllEnchantments(String namespace, HolderLookup.Provider holders, Consumer<ItemStack> itemStacks) {
        Comparator<Holder.Reference<Enchantment>> comparator = Comparator.comparing(
                entry -> entry.key().location().getPath());
        holders.lookup(Registries.ENCHANTMENT).stream().flatMap(HolderLookup::listElements).filter(
                entry -> entry.key().location().getNamespace().equals(namespace)).sorted(comparator).forEach(holder -> {
            itemStacks.accept(
                    EnchantmentHelper.createBook(new EnchantmentInstance(holder, holder.value().getMaxLevel())));
        });
    }

    private static void appendAllPotions(String namespace, HolderLookup.Provider holders, Consumer<ItemStack> itemStacks) {
        List<Holder.Reference<Potion>> potions = holders.lookup(Registries.POTION)
                .stream()
                .flatMap(HolderLookup::listElements)
                .filter(entry -> entry.key().location().getNamespace().equals(namespace))
                .filter(holder -> !holder.value().getEffects().isEmpty())
                .sorted(Comparator.comparing(holder -> holder.value().getEffects().getFirst()))
                .toList();
        for (Item item : POTION_ITEMS) {
            for (Holder.Reference<Potion> potion : potions) {
                itemStacks.accept(PotionContents.createItemStack(item, potion));
            }
        }
    }
}
