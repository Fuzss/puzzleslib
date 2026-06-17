package fuzs.puzzleslib.common.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
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
    private String value;
    @Shadow
    private int cursorPos;
    @Shadow
    private int highlightPos;
    @Unique
    private long puzzleslib$lastClickTime;
    @Unique
    private boolean puzzleslib$doubleClick;
    @Unique
    private int puzzleslib$doubleClickHighlightPos;
    @Unique
    private int puzzleslib$doubleClickCursorPos;

    public EditBoxMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Inject(method = "deleteText(IZ)V", at = @At("HEAD"), cancellable = true)
    protected void deleteText(int dir, boolean wholeWord, CallbackInfo callback) {
        // delete entire words or everything until the edit box beginning or end, based on the held modifier key
        // the modifier keys match the behaviour on Mac
        if (wholeWord) {
            if (dir < 0) {
                this.deleteChars(-this.cursorPos);
            }
        } else if (Minecraft.getInstance().hasAltDown()) {
            this.deleteWords(dir);
        } else {
            this.deleteChars(dir);
        }

        callback.cancel();
    }

    @Shadow
    public abstract void deleteWords(int dir);

    @Shadow
    public abstract void deleteChars(int dir);

    @Shadow
    public abstract int getWordPosition(int dir);

    @Shadow
    protected abstract int getWordPosition(int dir, int from, boolean stripSpaces);

    @Inject(method = "getWordPosition(IIZ)I", at = @At("HEAD"), cancellable = true)
    protected void getWordPosition(int dir, int from, boolean stripSpaces, CallbackInfoReturnable<Integer> callback) {
        int i = from;
        boolean backwards = dir < 0;
        int skippedWords = Math.abs(dir);

        for (int k = 0; k < skippedWords; ++k) {
            if (!backwards) {
                int l = this.value.length();
                while (stripSpaces && i == from && i < l && !puzzleslib$isWordChar(this.value.charAt(i))) {
                    ++i;
                    from++;
                }

                while (i < l && puzzleslib$isWordChar(this.value.charAt(i))) {
                    ++i;
                }
            } else {
                while (stripSpaces && i == from && i > 0 && !puzzleslib$isWordChar(this.value.charAt(i - 1))) {
                    --i;
                    from--;
                }

                while (i > 0 && puzzleslib$isWordChar(this.value.charAt(i - 1))) {
                    --i;
                }
            }
        }

        callback.setReturnValue(i);
    }

    @Unique
    private static boolean puzzleslib$isWordChar(char charAt) {
        // break skipping on more than just spaces, from Owo Lib, thanks!
        return charAt == '_' || Character.isAlphabetic(charAt) || Character.isDigit(charAt);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> callback) {
        if (this.isActive() && this.isFocused()) {
            if (event.isRight()) {
                // when text is selected and the cursor is moved without selecting new text,
                // make it jump to either the beginning or end of the selection
                boolean allowedToMoveRight = true;
                if (!event.hasShiftDown() && this.highlightPos != this.cursorPos) {
                    this.setCursorPosition(Math.max(this.getCursorPosition(), this.highlightPos));
                    this.setHighlightPos(this.getCursorPosition());
                    allowedToMoveRight = false;
                }

                // select entire words or everything until the edit box beginning or end, based on the held modifier key
                if (event.hasControlDownWithQuirk()) {
                    this.moveCursorToEnd(event.hasShiftDown());
                } else if (event.hasAltDown()) {
                    this.moveCursorTo(this.getWordPosition(1), event.hasShiftDown());
                } else if (allowedToMoveRight) {
                    this.moveCursor(1, event.hasShiftDown());
                }

                callback.setReturnValue(true);
            } else if (event.isLeft()) {
                // when text is selected and the cursor is moved without selecting new text,
                // make it jump to either the beginning or end of the selection
                boolean allowedToMoveLeft = true;
                if (!event.hasShiftDown() && this.highlightPos != this.cursorPos) {
                    this.setCursorPosition(Math.min(this.getCursorPosition(), this.highlightPos));
                    this.setHighlightPos(this.getCursorPosition());
                    allowedToMoveLeft = false;
                }

                // select entire words or everything until edit box beginning / end based on held modifier key
                if (event.hasControlDownWithQuirk()) {
                    this.moveCursorToStart(event.hasShiftDown());
                } else if (event.hasAltDown()) {
                    this.moveCursorTo(this.getWordPosition(-1), event.hasShiftDown());
                } else if (allowedToMoveLeft) {
                    this.moveCursor(-1, event.hasShiftDown());
                }

                callback.setReturnValue(true);
            }
        }
    }

    @Shadow
    public abstract void moveCursor(int dir, boolean hasShiftDown);

    @Shadow
    public abstract void moveCursorTo(int dir, boolean extendSelection);

    @Shadow
    public abstract void setCursorPosition(int pos);

    @Shadow
    public abstract void moveCursorToStart(boolean hasShiftDown);

    @Shadow
    public abstract void moveCursorToEnd(boolean hasShiftDown);

    @Shadow
    public abstract int getCursorPosition();

    @Shadow
    public abstract void setHighlightPos(int pos);

    @Inject(method = "onClick", at = @At("TAIL"))
    public void onClick(MouseButtonEvent event, boolean doubleClick, CallbackInfo callback) {
        long millis = Util.getMillis();
        boolean tripleClick = this.puzzleslib$doubleClick;
        this.puzzleslib$doubleClick = millis - this.puzzleslib$lastClickTime < 250L;
        if (this.puzzleslib$doubleClick) {
            if (tripleClick) {
                // triple click to select all text in the edit box
                this.moveCursorToEnd(false);
                this.setHighlightPos(0);
            } else {
                // store double click positions for dragging to select the clicked word
                // the highlight positions is the right selection boundary
                // the cursor position is the left selection boundary
                this.puzzleslib$doubleClickHighlightPos = this.getWordPosition(1, this.getCursorPosition(), false);
                this.puzzleslib$doubleClickCursorPos = this.getWordPosition(-1, this.getCursorPosition(), false);
            }
        }

        this.puzzleslib$lastClickTime = millis;
    }

    @Shadow
    private int findClickedPositionInText(MouseButtonEvent event) {
        throw new RuntimeException();
    }

    @Inject(method = "onDrag", at = @At("HEAD"), cancellable = true)
    protected void onDrag(MouseButtonEvent event, double dx, double dy, CallbackInfo callback) {
        if (this.puzzleslib$doubleClick) {
            // double-click drag across text to select individual words
            // dragging outside the edit box will select everything until the beginning or end
            int clickedPosition = this.findClickedPositionInText(event);
            if (this.isMouseOver(event.x(), event.y())) {
                int rightBoundary = this.getWordPosition(1, clickedPosition, false);
                this.moveCursorTo(Math.max(this.puzzleslib$doubleClickHighlightPos, rightBoundary), false);
                int leftBoundary = this.getWordPosition(-1, clickedPosition, false);
                this.moveCursorTo(Math.min(this.puzzleslib$doubleClickCursorPos, leftBoundary), true);
            } else {
                if (clickedPosition > this.puzzleslib$doubleClickHighlightPos) {
                    this.moveCursorToEnd(false);
                } else {
                    this.moveCursorTo(this.puzzleslib$doubleClickHighlightPos, false);
                }
                if (clickedPosition < this.puzzleslib$doubleClickCursorPos) {
                    this.moveCursorToStart(true);
                } else {
                    this.moveCursorTo(this.puzzleslib$doubleClickCursorPos, true);
                }
            }

            callback.cancel();
        } else {
            // vanilla already allows for dragging across text to select individual letters,
            // we additionally support dragging outside the edit box to select everything until the beginning or end
            if (!this.isMouseOver(event.x(), event.y())) {
                if (this.highlightPos < this.findClickedPositionInText(event)) {
                    this.moveCursorToEnd(true);
                } else {
                    this.moveCursorToStart(true);
                }

                callback.cancel();
            }
        }
    }
}
