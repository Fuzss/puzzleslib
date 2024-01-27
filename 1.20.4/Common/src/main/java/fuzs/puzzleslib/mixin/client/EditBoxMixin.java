package fuzs.puzzleslib.mixin.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EditBox.class)
abstract class EditBoxMixin extends AbstractWidget {
    @Shadow
    @Final
    public Font font;
    @Shadow
    public String value;
    @Shadow
    public boolean bordered;
    @Shadow
    public int displayPos;
    @Shadow
    public int cursorPos;
    @Shadow
    public int highlightPos;
    protected long lastClickTime;
    protected boolean doubleClick;
    protected int doubleClickHighlightPos;
    protected int doubleClickCursorPos;

    public EditBoxMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Inject(method = "deleteText(I)V", at = @At("HEAD"), cancellable = true)
    protected void deleteText(int charCount, CallbackInfo callback) {
        // delete entire words or everything until edit box beginning / end based on held modifier key
        if (Screen.hasControlDown()) {
            if (charCount < 0) this.deleteChars(-this.cursorPos);
        } else if (Screen.hasAltDown()) {
            this.deleteWords(charCount);
        } else {
            this.deleteChars(charCount);
        }

        callback.cancel();
    }

    @Shadow
    public abstract void deleteWords(int num);

    @Shadow
    public abstract void deleteChars(int num);

    @Shadow
    public abstract int getWordPosition(int numWords);

    @Shadow
    protected abstract int getWordPosition(int numWords, int pos, boolean skipConsecutiveSpaces);

    @Inject(method = "getWordPosition(IIZ)I", at = @At("HEAD"), cancellable = true)
    protected void getWordPosition(int numWords, int pos, boolean skipConsecutiveSpaces, CallbackInfoReturnable<Integer> callback) {
        int i = pos;
        boolean backwards = numWords < 0;
        int skippedWords = Math.abs(numWords);

        for (int k = 0; k < skippedWords; ++k) {
            if (!backwards) {
                int l = this.value.length();
                while (skipConsecutiveSpaces && i == pos && i < l && !isWordChar(this.value.charAt(i))) {
                    ++i;
                    pos++;
                }

                while (i < l && isWordChar(this.value.charAt(i))) {
                    ++i;
                }
            } else {
                while (skipConsecutiveSpaces && i == pos && i > 0 && !isWordChar(this.value.charAt(i - 1))) {
                    --i;
                    pos--;
                }

                while (i > 0 && isWordChar(this.value.charAt(i - 1))) {
                    --i;
                }
            }
        }

        callback.setReturnValue(i);
    }

    @Unique
    private static boolean isWordChar(char charAt) {
        // break skipping on more than just spaces, from Owo Lib, thanks!
        return charAt == '_' || Character.isAlphabetic(charAt) || Character.isDigit(charAt);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> callback) {
        if (this.isActive() && this.isFocused()) {
            if (keyCode == InputConstants.KEY_RIGHT) {
                // when text is selected and the cursor is moved without selecting new text,
                // make it jump to either the beginning or end of the selection
                boolean allowedToMoveRight = true;
                if (!Screen.hasShiftDown() && this.highlightPos != this.cursorPos) {
                    this.setCursorPosition(Math.max(this.getCursorPosition(), this.highlightPos));
                    this.setHighlightPos(this.getCursorPosition());
                    allowedToMoveRight = false;
                }
                // select entire words or everything until edit box beginning / end based on held modifier key
                if (Screen.hasControlDown()) {
                    this.moveCursorToEnd(Screen.hasShiftDown());
                } else if (Screen.hasAltDown()) {
                    this.moveCursorTo(this.getWordPosition(1), Screen.hasShiftDown());
                } else if (allowedToMoveRight) {
                    this.moveCursor(1, Screen.hasShiftDown());
                }

                callback.setReturnValue(true);
            } else if (keyCode == InputConstants.KEY_LEFT) {
                // when text is selected and the cursor is moved without selecting new text,
                // make it jump to either the beginning or end of the selection
                boolean allowedToMoveLeft = true;
                if (!Screen.hasShiftDown() && this.highlightPos != this.cursorPos) {
                    this.setCursorPosition(Math.min(this.getCursorPosition(), this.highlightPos));
                    this.setHighlightPos(this.getCursorPosition());
                    allowedToMoveLeft = false;
                }
                // select entire words or everything until edit box beginning / end based on held modifier key
                if (Screen.hasControlDown()) {
                    this.moveCursorToStart(Screen.hasShiftDown());
                } else if (Screen.hasAltDown()) {
                    this.moveCursorTo(this.getWordPosition(-1), Screen.hasShiftDown());
                } else if (allowedToMoveLeft) {
                    this.moveCursor(-1, Screen.hasShiftDown());
                }

                callback.setReturnValue(true);
            }
        }
    }

    @Shadow
    public abstract void moveCursor(int delta, boolean select);

    @Shadow
    public abstract void moveCursorTo(int delta, boolean select);

    @Shadow
    public abstract void setCursorPosition(int pos);

    @Shadow
    public abstract void moveCursorToStart(boolean select);

    @Shadow
    public abstract void moveCursorToEnd(boolean select);

    @Shadow
    public abstract int getCursorPosition();

    @Shadow
    public abstract int getInnerWidth();

    @Shadow
    public abstract void setHighlightPos(int position);

    @Inject(method = "onClick", at = @At("TAIL"))
    public void onClick(double mouseX, double mouseY, CallbackInfo callback) {
        long millis = Util.getMillis();
        boolean tripleClick = this.doubleClick;
        this.doubleClick = millis - this.lastClickTime < 250L;
        if (this.doubleClick) {
            if (tripleClick) {
                // triple click to select all text in the edit box
                this.moveCursorToEnd(false);
                this.setHighlightPos(0);
            } else {
                // double click to select the clicked word
                // highlight positions is right selection boundary, cursor position is left selection boundary
                this.doubleClickHighlightPos = this.getWordPosition(1, this.getCursorPosition(), false);
                this.moveCursorTo(this.doubleClickHighlightPos, false);
                this.doubleClickCursorPos = this.getWordPosition(-1, this.getCursorPosition(), false);
                this.moveCursorTo(this.doubleClickCursorPos, true);
            }
        }

        this.lastClickTime = millis;
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        int i = Mth.floor(mouseX) - this.getX();
        if (this.bordered) {
            i -= 4;
        }

        String string = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
        int mousePosition = this.font.plainSubstrByWidth(string, i).length() + this.displayPos;

        if (this.doubleClick) {
            // double click drag across text to select individual words
            // dragging outside the edit box will select everything until beginning / end
            if (this.clicked(mouseX, mouseY)) {
                int rightBoundary = this.getWordPosition(1, mousePosition, false);
                this.moveCursorTo(Math.max(this.doubleClickHighlightPos, rightBoundary), false);
                int leftBoundary = this.getWordPosition(-1, mousePosition, false);
                this.moveCursorTo(Math.min(this.doubleClickCursorPos, leftBoundary), true);
            } else {
                if (mousePosition > this.doubleClickHighlightPos) {
                    this.moveCursorToEnd(false);
                } else {
                    this.moveCursorTo(this.doubleClickHighlightPos, false);
                }
                if (mousePosition < this.doubleClickCursorPos) {
                    this.moveCursorToStart(true);
                } else {
                    this.moveCursorTo(this.doubleClickCursorPos, true);
                }
            }
        } else {
            // drag across text to select individual letters
            // dragging outside the edit box will select everything until beginning / end
            if (this.clicked(mouseX, mouseY)) {
                this.moveCursorTo(mousePosition, true);
            } else if (this.highlightPos < mousePosition) {
                this.moveCursorToEnd(true);
            } else {
                this.moveCursorToStart(true);
            }
        }
    }
}