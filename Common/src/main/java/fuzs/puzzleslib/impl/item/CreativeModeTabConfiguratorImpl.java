package fuzs.puzzleslib.impl.item;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.item.v2.CreativeModeTabConfigurator;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class CreativeModeTabConfiguratorImpl implements CreativeModeTabConfigurator {
    private static final Item[] POTION_ITEMS = new Item[]{Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION, Items.TIPPED_ARROW};

    private final ResourceLocation identifier;
    @Nullable
    private Supplier<ItemStack> icon;
    @Nullable
    private Supplier<ItemStack[]> icons;
    private CreativeModeTab.DisplayItemsGenerator displayItemsGenerator = (CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) -> {

    };
    private boolean hasSearchBar;
    private boolean appendEnchantmentsAndPotions;

    public CreativeModeTabConfiguratorImpl(ResourceLocation identifier) {
        this.identifier = identifier;
    }

    public ResourceLocation getIdentifier() {
        return this.identifier;
    }

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
    public CreativeModeTabConfigurator displayItems(CreativeModeTab.DisplayItemsGenerator displayItemsGenerator) {
        this.displayItemsGenerator = displayItemsGenerator;
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
        String translationKey = "itemGroup.%s.%s".formatted(this.identifier.getNamespace(), this.identifier.getPath());
        builder.title(Component.translatable(translationKey));
        if (this.icon != null) {
            builder.icon(this.icon);
        } else {
            Objects.requireNonNull(this.icons, "both icon suppliers are null");
            // since no single icon is set and multiple icons are only processed on Forge, this would otherwise be an empty icon for Fabric / Quilt
            if (!ModLoaderEnvironment.INSTANCE.getModLoader().isForgeLike()) {
                builder.icon(() -> {
                    ItemStack[] icons = this.icons.get();
                    Preconditions.checkPositionIndex(1, icons.length, "icons is empty");
                    return icons[0];
                });
            }
        }
        if (this.appendEnchantmentsAndPotions) {
            builder.displayItems((CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) -> {
                this.displayItemsGenerator.accept(itemDisplayParameters, output);
                appendAllEnchantments(this.identifier.getNamespace(), itemDisplayParameters.holders(), output::accept);
                appendAllPotions(this.identifier.getNamespace(), itemDisplayParameters.holders(), output::accept);
            });
        } else {
            builder.displayItems(this.displayItemsGenerator);
        }
    }

    private static void appendAllEnchantments(String namespace, HolderLookup.Provider holders, Consumer<ItemStack> itemStacks) {
        Comparator<Holder.Reference<Enchantment>> comparator = Comparator.comparing(entry -> entry.key().location().getPath());
        holders.lookup(Registries.ENCHANTMENT).stream()
                .flatMap(HolderLookup::listElements)
                .filter(entry -> entry.key().location().getNamespace().equals(namespace))
                .sorted(comparator)
                .map(Holder.Reference::value)
                .forEach(enchantment -> {
                    itemStacks.accept(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, enchantment.getMaxLevel())));
                });
    }

    private static void appendAllPotions(String namespace, HolderLookup.Provider holders, Consumer<ItemStack> itemStacks) {
        Comparator<Potion> comparator = Comparator.<Potion, String>comparing(potion -> {
            if (potion.getEffects().isEmpty()) throw new IllegalArgumentException("Cannot compare potions with empty effects!");
            MobEffect effect = potion.getEffects().get(0).getEffect();
            ResourceLocation key = BuiltInRegistries.MOB_EFFECT.getKey(effect);
            Objects.requireNonNull(key, "Mob effect key for class %s is null".formatted(effect.getClass()));
            return key.getPath();
        }).thenComparingInt(potion -> potion.getEffects().get(0).getAmplifier())
                .thenComparingInt(potion -> potion.getEffects().get(0).getDuration());
        Potion[] potions = holders.lookup(Registries.POTION).stream()
                .flatMap(HolderLookup::listElements)
                .filter(entry -> entry.key().location().getNamespace().equals(namespace))
                .map(Holder.Reference::value)
                .filter(potion -> !potion.getEffects().isEmpty())
                .sorted(comparator)
                .toArray(Potion[]::new);
        for (Item item : POTION_ITEMS) {
            for (Potion potion : potions) {
                itemStacks.accept(PotionUtils.setPotion(new ItemStack(item), potion));
            }
        }
    }
}
