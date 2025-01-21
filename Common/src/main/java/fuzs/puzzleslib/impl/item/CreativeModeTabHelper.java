package fuzs.puzzleslib.impl.item;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
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

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class CreativeModeTabHelper {
    static final Collection<Item> POTION_ITEMS = ImmutableSet.of(Items.POTION,
            Items.SPLASH_POTION,
            Items.LINGERING_POTION,
            Items.TIPPED_ARROW);

    private CreativeModeTabHelper() {
        // NO-OP
    }

    public static Component getTitle(ResourceLocation resourceLocation) {
        String translationKey = "itemGroup.%s.%s".formatted(resourceLocation.getNamespace(),
                resourceLocation.getPath());
        return Component.translatable(translationKey);
    }

    public static CreativeModeTab.DisplayItemsGenerator getDisplayItems(String modId) {
        return getDisplayItems(modId, Predicates.alwaysTrue());
    }

    public static CreativeModeTab.DisplayItemsGenerator getDisplayItems(String modId, Predicate<ItemStack> filter) {
        return (CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) -> {
            Consumer<ItemStack> itemStacks = (ItemStack itemStack) -> {
                if (filter.test(itemStack)) output.accept(itemStack);
            };
            appendAllItems(modId, itemDisplayParameters.holders(), itemStacks);
            appendAllEnchantments(modId, itemDisplayParameters.holders(), itemStacks);
            appendAllPotions(modId, itemDisplayParameters.holders(), itemStacks);
        };
    }

    public static void appendAllItems(String modId, HolderLookup.Provider registries, Consumer<ItemStack> itemStacks) {
        getHoldersFromNamespace(Registries.ITEM, registries, modId).map(ItemStack::new).forEach(itemStacks);
    }

    public static void appendAllEnchantments(String modId, HolderLookup.Provider registries, Consumer<ItemStack> itemStacks) {
        getHoldersFromNamespace(Registries.ENCHANTMENT,
                registries,
                modId).map((Holder.Reference<Enchantment> holder) -> new EnchantmentInstance(holder,
                holder.value().getMaxLevel())).map(EnchantmentHelper::createBook).forEach(itemStacks);
    }

    public static void appendAllPotions(String modId, HolderLookup.Provider registries, Consumer<ItemStack> itemStacks) {
        List<Holder.Reference<Potion>> potions = getHoldersFromNamespace(Registries.POTION,
                registries,
                modId).filter((Holder.Reference<Potion> holder) -> !holder.value().getEffects().isEmpty())
                .sorted(Comparator.comparing((Holder.Reference<Potion> holder) -> holder.value()
                        .getEffects()
                        .getFirst()))
                .toList();
        for (Item item : POTION_ITEMS) {
            for (Holder.Reference<Potion> potion : potions) {
                itemStacks.accept(PotionContents.createItemStack(item, potion));
            }
        }
    }

    public static <T> Stream<Holder.Reference<T>> getHoldersFromNamespace(ResourceKey<? extends Registry<? extends T>> registryKey, HolderLookup.Provider registries, String modId) {
        return registries.lookup(registryKey)
                .stream()
                .flatMap(HolderLookup::listElements)
                .filter((Holder.Reference<T> holder) -> holder.key().location().getNamespace().equals(modId));
    }
}
