package fuzs.puzzleslib.impl.item;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

import java.util.Collection;

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
        return (CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) -> {
            CreativeModeTab.Output filteredOutput = (ItemStack itemStack, CreativeModeTab.TabVisibility tabVisibility) -> {
                if (itemStack.getItemHolder().unwrapKey().orElseThrow().location().getNamespace().equals(modId)) {
                    output.accept(itemStack, tabVisibility);
                }
            };
            itemDisplayParameters.holders().lookup(Registries.ITEM).ifPresent(registryLookup -> {
                registryLookup.listElements()
                        .map(ItemStack::new)
                        .forEach(itemStack -> filteredOutput.accept(itemStack,
                                CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS));
            });
            itemDisplayParameters.holders().lookup(Registries.ENCHANTMENT).ifPresent(registryLookup -> {
                CreativeModeTabs.generateEnchantmentBookTypesOnlyMaxLevel(filteredOutput,
                        registryLookup,
                        CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
            });
            itemDisplayParameters.holders().lookup(Registries.POTION).ifPresent(registryLookup -> {
                for (Item item : POTION_ITEMS) {
                    CreativeModeTabs.generatePotionEffectTypes(filteredOutput,
                            registryLookup,
                            item,
                            CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS,
                            itemDisplayParameters.enabledFeatures());
                }
            });
            itemDisplayParameters.holders()
                    .lookup(Registries.PAINTING_VARIANT)
                    .ifPresent(registryLookup -> CreativeModeTabs.generatePresetPaintings(filteredOutput,
                            itemDisplayParameters.holders(),
                            registryLookup,
                            Predicates.alwaysTrue(),
                            CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS));
        };
    }
}
