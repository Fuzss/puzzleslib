package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.item.v2.DisplayItemsOutput;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Locale;

@FunctionalInterface
public interface BuildCreativeContentsCallback {
    EventInvoker<BuildCreativeContentsCallback> EVENT = EventInvoker.lookup(BuildCreativeContentsCallback.class);

    /**
     * Called whenever the displayed items in {@link net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen} are rebuilt,
     * allows for adding custom items to the end of a tab.
     *
     * @param tab    the {@link CreativeModeTab} item contents are being built for
     * @param output a consumer for appending additional contents
     */
    void onBuildCreativeContents(@Nullable ResourceLocation identifier, CreativeModeTab tab, DisplayItemsOutput output);

    @ApiStatus.Internal
    @Nullable
    static ResourceLocation tryCreateIdentifier(CreativeModeTab tab) {
        return ResourceLocation.tryParse(tab.getRecipeFolderName().toLowerCase(Locale.ROOT).replace(".", ":"));
    }

    @ApiStatus.Internal
    static DisplayItemsOutput checkedOutput(Collection<ItemStack> items) {
        ObjectOpenCustomHashSet<ItemStack> stacks = new ObjectOpenCustomHashSet<>(new Hash.Strategy<>() {
            @Override
            public int hashCode(ItemStack o) {
                if (o != null) {
                    CompoundTag tag = o.getTag();
                    int i = 31 + o.getItem().hashCode();
                    return 31 * i + (tag == null ? 0 : tag.hashCode());
                }
                return 0;
            }

            @Override
            public boolean equals(ItemStack a, ItemStack b) {
                return a == b || a != null && b != null && a.isEmpty() == b.isEmpty() && ItemStack.isSameItemSameTags(a, b);
            }
        });
        return stack -> {
            if (stacks.isEmpty()) {
                stacks.addAll(items);
            }
            if (!stacks.contains(stack)) {
                items.add(stack);
                stacks.add(stack);
            }
        };
    }
}
