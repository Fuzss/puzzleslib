package fuzs.puzzleslib.mixin.client;

import fuzs.puzzleslib.api.client.event.v1.BuildCreativeContentsCallback;
import fuzs.puzzleslib.impl.client.core.event.CreativeModeTabContentsEvent;
import fuzs.puzzleslib.impl.client.event.ScreenCloseEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoader;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
abstract class MinecraftForgeMixin extends ReentrantBlockableEventLoop<Runnable> {
    @Shadow
    public Screen screen;

    public MinecraftForgeMixin(String name) {
        super(name);
    }

    @Inject(method = "setScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;removed()V", shift = At.Shift.AFTER))
    public void setScreen(@Nullable Screen screen, CallbackInfo callback) {
        MinecraftForge.EVENT_BUS.post(new ScreenCloseEvent(this.screen));
    }

    @ModifyVariable(method = "createSearchTrees", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/NonNullList;forEach(Ljava/util/function/Consumer;)V", shift = At.Shift.BEFORE))
    public NonNullList<ItemStack> createSearchTrees(NonNullList<ItemStack> items) {
        for (CreativeModeTab tab : CreativeModeTab.TABS) {
            ModLoader.get().postEvent(new CreativeModeTabContentsEvent(tab, BuildCreativeContentsCallback.checkedOutput(items)));
        }
        return items;
    }
}
