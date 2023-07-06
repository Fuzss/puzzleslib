package fuzs.puzzleslib.impl.creativetab;

import fuzs.puzzleslib.util.CreativeModeTabBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class CreativeModeTabBuilderImpl implements CreativeModeTabBuilder {
    private static final Item[] POTION_ITEMS = new Item[]{Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION, Items.TIPPED_ARROW};

    final ResourceLocation identifier;
    private Supplier<ItemStack> iconSupplier = () -> ItemStack.EMPTY;
    boolean cacheIcon = true;
    boolean showTitle = true;
    boolean showScrollbar = true;
    boolean alignRight;
    @Nullable
    BiConsumer<NonNullList<ItemStack>, CreativeModeTab> stacksForDisplay;
    boolean appendEnchantments;
    boolean appendPotions;
    boolean showSearch;

    public CreativeModeTabBuilderImpl(String modId, String identifier) {
        this.identifier = new ResourceLocation(modId, identifier);
    }

    ItemStack getIcon() {
        ItemStack stack = this.iconSupplier.get();
        if (stack.isEmpty()) throw new IllegalStateException("Creative tab icon cannot be empty");
        return stack;
    }

    @Override
    public CreativeModeTabBuilder setIcon(Supplier<ItemStack> icon) {
        Objects.requireNonNull(icon, "icon supplier was null");
        this.iconSupplier = icon;
        return this;
    }

    @Override
    public CreativeModeTabBuilder disableIconCache() {
        this.cacheIcon = false;
        return this;
    }

    @Override
    public CreativeModeTabBuilder hideTitle() {
        this.showTitle = false;
        return this;
    }

    @Override
    public CreativeModeTabBuilder hideScroll() {
        this.showScrollbar = false;
        return this;
    }

    @Override
    public CreativeModeTabBuilder alignRight() {
        this.alignRight = true;
        return this;
    }

    @Override
    public CreativeModeTabBuilder appendItemsV2(BiConsumer<NonNullList<ItemStack>, CreativeModeTab> itemStacks) {
        Objects.requireNonNull(itemStacks, "item stacks consumer was null");
        this.stacksForDisplay = itemStacks;
        return this;
    }

    @Override
    public CreativeModeTabBuilder appendAllEnchantments() {
        this.appendEnchantments = true;
        return this;
    }

    @Override
    public CreativeModeTabBuilder appendAllPotions() {
        this.appendPotions = true;
        return this;
    }

    @Override
    public CreativeModeTabBuilder showSearch() {
        this.showSearch = true;
        return this;
    }

    void appendAdditionals(NonNullList<ItemStack> itemStacks) {
        if (this.appendEnchantments) this.appendAllEnchantments(itemStacks::add);
        if (this.appendPotions) this.appendAllPotions(itemStacks::add);
    }

    private void appendAllEnchantments(Consumer<ItemStack> itemStacks) {
        Comparator<Map.Entry<ResourceKey<Enchantment>, Enchantment>> comparator = Comparator.comparing(entry -> entry.getKey().location().getPath());
        this.getNamespacedEntries(Registry.ENCHANTMENT, this.identifier.getNamespace()).sorted(comparator).map(Map.Entry::getValue).forEach(enchantment -> {
            itemStacks.accept(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, enchantment.getMaxLevel())));
        });
    }

    private void appendAllPotions(Consumer<ItemStack> itemStacks) {
        Comparator<Potion> comparator = Comparator.<Potion, String>comparing(potion -> {
            if (potion.getEffects().isEmpty()) throw new IllegalArgumentException("Cannot compare potions with empty effects!");
            MobEffect effect = potion.getEffects().get(0).getEffect();
            ResourceLocation key = Registry.MOB_EFFECT.getKey(effect);
            Objects.requireNonNull(key, "Mob effect key for class %s is null".formatted(effect.getClass()));
            return key.getPath();
        }).thenComparingInt(potion -> potion.getEffects().get(0).getAmplifier()).thenComparingInt(potion -> potion.getEffects().get(0).getDuration());
        Potion[] potions = this.getNamespacedEntries(Registry.POTION, this.identifier.getNamespace()).map(Map.Entry::getValue).filter(potion -> !potion.getEffects().isEmpty()).sorted(comparator).toArray(Potion[]::new);
        for (Item item : POTION_ITEMS) {
            for (Potion potion : potions) {
                itemStacks.accept(PotionUtils.setPotion(new ItemStack(item), potion));
            }
        }
    }

    private <T> Stream<Map.Entry<ResourceKey<T>, T>> getNamespacedEntries(Registry<T> registry, String namespace) {
        return registry.entrySet().stream().filter(entry -> entry.getKey().location().getNamespace().equals(namespace));
    }
}
