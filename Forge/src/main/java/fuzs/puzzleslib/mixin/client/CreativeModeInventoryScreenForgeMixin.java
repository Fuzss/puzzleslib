package fuzs.puzzleslib.mixin.client;

import fuzs.puzzleslib.impl.client.core.event.CreativeModeTabContentsEvent;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
abstract class CreativeModeInventoryScreenForgeMixin extends EffectRenderingInventoryScreen<CreativeModeInventoryScreen.ItemPickerMenu> {
    @Shadow
    private static int selectedTab;

    public CreativeModeInventoryScreenForgeMixin(CreativeModeInventoryScreen.ItemPickerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Inject(method = "refreshSearchResults", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CreativeModeTab;fillItemList(Lnet/minecraft/core/NonNullList;)V", shift = At.Shift.AFTER))
    private void refreshSearchResults(CallbackInfo callback) {
        CreativeModeTab tab = CreativeModeTab.TABS[selectedTab];
        MinecraftForge.EVENT_BUS.post(new CreativeModeTabContentsEvent(tab, this.menu.items::add));
    }

    @Inject(method = "selectTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CreativeModeTab;fillItemList(Lnet/minecraft/core/NonNullList;)V", shift = At.Shift.AFTER))
    private void selectTab(CreativeModeTab tab, CallbackInfo callback) {
        MinecraftForge.EVENT_BUS.post(new CreativeModeTabContentsEvent(tab, this.menu.items::add));
    }
}
