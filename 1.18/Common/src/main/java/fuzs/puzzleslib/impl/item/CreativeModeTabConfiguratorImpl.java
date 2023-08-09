package fuzs.puzzleslib.impl.item;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.item.v2.CreativeModeTabConfigurator;
import fuzs.puzzleslib.api.item.v2.DisplayItemsOutput;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class CreativeModeTabConfiguratorImpl implements CreativeModeTabConfigurator {
    private static final Item[] POTION_ITEMS = new Item[]{Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION, Items.TIPPED_ARROW};

    private final ResourceLocation identifier;
    @Nullable
    private Supplier<ItemStack> icon;
    @Nullable
    private Supplier<ItemStack[]> icons;
    private Consumer<DisplayItemsOutput> displayItemsGenerator = (DisplayItemsOutput displayItemsOutput) -> {

    };
    private boolean hasSearchBar;
    private boolean appendEnchantmentsAndPotions;

    public CreativeModeTabConfiguratorImpl(ResourceLocation identifier) {
        this.identifier = identifier;
    }

    public ResourceLocation getIdentifier() {
        return this.identifier;
    }

    public Supplier<ItemStack> getIcon() {
        if (this.icon != null) {
            return this.icon;
        } else {
            Objects.requireNonNull(this.icons, "both icon suppliers are null");
            // since no single icon is set and multiple icons are only processed on Forge, this would otherwise be an empty icon for Fabric / Quilt
            if (!ModLoaderEnvironment.INSTANCE.isForge()) {
                return () -> {
                    ItemStack[] icons = this.icons.get();
                    Preconditions.checkPositionIndex(1, icons.length, "icons is empty");
                    return icons[0];
                };
            }
        }
        return this.icon;
    }

    @Nullable
    public Supplier<ItemStack[]> getIcons() {
        return this.icons;
    }

    public Consumer<DisplayItemsOutput> getDisplayItemsGenerator() {
        if (this.appendEnchantmentsAndPotions) {
            return (DisplayItemsOutput output) -> {
                this.displayItemsGenerator.accept(output);
                appendAllEnchantments(this.identifier.getNamespace(), output::accept);
                appendAllPotions(this.identifier.getNamespace(), output::accept);
            };
        } else {
            return this.displayItemsGenerator;
        }
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
    public CreativeModeTabConfigurator displayItems(Consumer<DisplayItemsOutput> displayItemsGenerator) {
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

    private static void appendAllEnchantments(String namespace, Consumer<ItemStack> itemStacks) {
        Comparator<Map.Entry<ResourceKey<Enchantment>, Enchantment>> comparator = Comparator.comparing(entry -> entry.getKey().location().getPath());
        getNamespacedEntries(Registry.ENCHANTMENT, namespace).sorted(comparator).map(Map.Entry::getValue).forEach(enchantment -> {
            itemStacks.accept(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, enchantment.getMaxLevel())));
        });
    }

    private static void appendAllPotions(String namespace, Consumer<ItemStack> itemStacks) {
        Comparator<Potion> comparator = Comparator.<Potion, String>comparing(potion -> {
            if (potion.getEffects().isEmpty()) throw new IllegalArgumentException("Cannot compare potions with empty effects!");
            MobEffect effect = potion.getEffects().get(0).getEffect();
            ResourceLocation key = Registry.MOB_EFFECT.getKey(effect);
            Objects.requireNonNull(key, "Mob effect key for class %s is null".formatted(effect.getClass()));
            return key.getPath();
        }).thenComparingInt(potion -> potion.getEffects().get(0).getAmplifier()).thenComparingInt(potion -> potion.getEffects().get(0).getDuration());
        Potion[] potions = getNamespacedEntries(Registry.POTION, namespace).map(Map.Entry::getValue).filter(potion -> !potion.getEffects().isEmpty()).sorted(comparator).toArray(Potion[]::new);
        for (Item item : POTION_ITEMS) {
            for (Potion potion : potions) {
                itemStacks.accept(PotionUtils.setPotion(new ItemStack(item), potion));
            }
        }
    }

    private static <T> Stream<Map.Entry<ResourceKey<T>, T>> getNamespacedEntries(Registry<T> registry, String namespace) {
        return registry.entrySet().stream().filter(entry -> entry.getKey().location().getNamespace().equals(namespace));
    }
}
