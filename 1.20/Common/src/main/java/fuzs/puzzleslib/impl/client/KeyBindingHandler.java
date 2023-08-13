package fuzs.puzzleslib.impl.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class KeyBindingHandler {
    public static final KeyMapping TOGGLE_PLAQUES_KEY_MAPPING = new KeyMapping("key.togglePlaques", InputConstants.KEY_J, "key.categories.misc");
    public static final KeyMapping TOGGLE_PLAQUES_KEY_MAPPING2 = new KeyMapping("key.togglePlaques2", InputConstants.KEY_H, "key.categories.misc");
    private static final Component ON_COMPONENT = Component.empty().append(CommonComponents.OPTION_ON).withStyle(ChatFormatting.GREEN);
    private static final Component OFF_COMPONENT = Component.empty().append(CommonComponents.OPTION_OFF).withStyle(ChatFormatting.RED);
    private static boolean allowRendering;

    public static void onClientTick$Start(Minecraft minecraft) {
        while (TOGGLE_PLAQUES_KEY_MAPPING.consumeClick()) {
            setClientMessage(minecraft, true, 1);
        }
        while (TOGGLE_PLAQUES_KEY_MAPPING2.consumeClick()) {
            setClientMessage(minecraft, true, 2);
        }
    }

    public static void setClientMessage(Minecraft minecraft, boolean actionBar, int no) {
        allowRendering = (!allowRendering);
        Component component = Component.translatable("key.togglePlaques.message" + no, allowRendering ? ON_COMPONENT : OFF_COMPONENT);
        minecraft.player.displayClientMessage(component, actionBar);
    }
}
