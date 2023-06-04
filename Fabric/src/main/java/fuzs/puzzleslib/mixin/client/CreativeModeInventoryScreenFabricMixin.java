package fuzs.puzzleslib.mixin.client;

import fuzs.puzzleslib.api.client.event.v1.BuildCreativeContentsCallback;
import fuzs.puzzleslib.api.client.event.v1.FabricClientEvents;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
abstract class CreativeModeInventoryScreenFabricMixin extends EffectRenderingInventoryScreen<CreativeModeInventoryScreen.ItemPickerMenu> {
    @Shadow
    private static int selectedTab;
    @Shadow
    private EditBox searchBox;

    public CreativeModeInventoryScreenFabricMixin(CreativeModeInventoryScreen.ItemPickerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Inject(method = "refreshSearchResults", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen;scrollOffs:F", shift = At.Shift.BEFORE))
    private void refreshSearchResults(CallbackInfo callback) {
        if (!this.searchBox.getValue().isEmpty()) return;
        for (CreativeModeTab tab : CreativeModeTab.TABS) {
            ResourceLocation identifier = BuildCreativeContentsCallback.tryCreateIdentifier(tab);
            FabricClientEvents.BUILD_CREATIVE_CONTENTS.invoker().onBuildCreativeContents(identifier, tab, this.menu.items::add);
        }
    }

    @Inject(method = "selectTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CreativeModeTab;fillItemList(Lnet/minecraft/core/NonNullList;)V", shift = At.Shift.AFTER))
    private void selectTab(CreativeModeTab tab, CallbackInfo callback) {
        ResourceLocation identifier = BuildCreativeContentsCallback.tryCreateIdentifier(tab);
        FabricClientEvents.BUILD_CREATIVE_CONTENTS.invoker().onBuildCreativeContents(identifier, tab, this.menu.items::add);
    }
}
