package fuzs.puzzleslib.impl.client.core.proxy;

import net.minecraft.client.gui.Gui;

public interface GuiHeightProxy {

    int getGuiLeftHeight(Gui gui);

    int getGuiRightHeight(Gui gui);

    void setGuiLeftHeight(Gui gui, int leftHeight);

    void setGuiRightHeight(Gui gui, int rightHeight);
}
