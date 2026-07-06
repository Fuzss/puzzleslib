package fuzs.puzzleslib.api.client.input.v1;

import net.minecraft.Util;

/**
 * Copied from Minecraft 26.2.
 */
public class InputQuirks {
    private static final boolean ON_OSX = Util.getPlatform() == Util.OS.OSX;
    public static final boolean REPLACE_CTRL_KEY_WITH_CMD_KEY = ON_OSX;
    public static final int EDIT_SHORTCUT_KEY_MODIFIER = REPLACE_CTRL_KEY_WITH_CMD_KEY ? 8 : 2;
    public static final boolean SIMULATE_RIGHT_CLICK_WITH_LONG_LEFT_CLICK = ON_OSX;
    public static final boolean RESTORE_KEY_STATE_AFTER_MOUSE_GRAB = !ON_OSX;
}
