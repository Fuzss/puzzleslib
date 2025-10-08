package fuzs.puzzleslib.mixin.client;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
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
    private long lastClickTime;
    @Unique
    private boolean doubleClick;
    @Unique
    private int doubleClickHighlightPos;
    @Unique
    private int doubleClickCursorPos;

    public EditBoxMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Inject(method = "deleteText(IZ)V", at = @At("HEAD"), cancellable = true)
    protected void deleteText(int charCount, boolean hasControlDown, CallbackInfo callback) {
        // delete entire words or everything until the edit box beginning or end, based on the held modifier key
        // the modifier keys match the behaviour on Mac
        if (hasControlDown) {
            if (charCount < 0) {
                this.deleteChars(-this.cursorPos);
            }
        } else if (Minecraft.getInstance().hasAltDown()) {
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
    public void keyPressed(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> callback) {
        if (this.isActive() && this.isFocused()) {
            if (keyEvent.isRight()) {
                // when text is selected and the cursor is moved without selecting new text,
                // make it jump to either the beginning or end of the selection
                boolean allowedToMoveRight = true;
                if (!keyEvent.hasShiftDown() && this.highlightPos != this.cursorPos) {
                    this.setCursorPosition(Math.max(this.getCursorPosition(), this.highlightPos));
                    this.setHighlightPos(this.getCursorPosition());
                    allowedToMoveRight = false;
                }
                // select entire words or everything until the edit box beginning or end, based on the held modifier key
                if (keyEvent.hasControlDown()) {
                    this.moveCursorToEnd(keyEvent.hasShiftDown());
                } else if (keyEvent.hasAltDown()) {
                    this.moveCursorTo(this.getWordPosition(1), keyEvent.hasShiftDown());
                } else if (allowedToMoveRight) {
                    this.moveCursor(1, keyEvent.hasShiftDown());
                }

                callback.setReturnValue(true);
            } else if (keyEvent.isLeft()) {
                // when text is selected and the cursor is moved without selecting new text,
                // make it jump to either the beginning or end of the selection
                boolean allowedToMoveLeft = true;
                if (!keyEvent.hasShiftDown() && this.highlightPos != this.cursorPos) {
                    this.setCursorPosition(Math.min(this.getCursorPosition(), this.highlightPos));
                    this.setHighlightPos(this.getCursorPosition());
                    allowedToMoveLeft = false;
                }
                // select entire words or everything until edit box beginning / end based on held modifier key
                if (keyEvent.hasControlDown()) {
                    this.moveCursorToStart(keyEvent.hasShiftDown());
                } else if (keyEvent.hasAltDown()) {
                    this.moveCursorTo(this.getWordPosition(-1), keyEvent.hasShiftDown());
                } else if (allowedToMoveLeft) {
                    this.moveCursor(-1, keyEvent.hasShiftDown());
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
    public abstract void setHighlightPos(int position);

    @Inject(method = "onClick", at = @At("TAIL"))
    public void onClick(MouseButtonEvent mouseButtonEvent, boolean doubleClick, CallbackInfo callback) {
        long millis = Util.getMillis();
        boolean tripleClick = this.doubleClick;
        this.doubleClick = millis - this.lastClickTime < 250L;
        if (this.doubleClick) {
            if (tripleClick) {
                // triple click to select all text in the edit box
                this.moveCursorToEnd(false);
                this.setHighlightPos(0);
            } else {
                // store double click positions for dragging to select the clicked word
                // the highlight positions is the right selection boundary
                // the cursor position is the left selection boundary
                this.doubleClickHighlightPos = this.getWordPosition(1, this.getCursorPosition(), false);
                this.doubleClickCursorPos = this.getWordPosition(-1, this.getCursorPosition(), false);
            }
        }

        this.lastClickTime = millis;
    }

    @Shadow
    private int findClickedPositionInText(MouseButtonEvent mouseButtonEvent) {
        throw new RuntimeException();
    }

    @Inject(method = "onDrag", at = @At("HEAD"), cancellable = true)
    protected void onDrag(MouseButtonEvent mouseButtonEvent, double dragX, double dragY, CallbackInfo callback) {
        if (this.doubleClick) {
            // double-click drag across text to select individual words
            // dragging outside the edit box will select everything until the beginning or end
            int clickedPosition = this.findClickedPositionInText(mouseButtonEvent);
            if (this.isMouseOver(mouseButtonEvent.x(), mouseButtonEvent.y())) {
                int rightBoundary = this.getWordPosition(1, clickedPosition, false);
                this.moveCursorTo(Math.max(this.doubleClickHighlightPos, rightBoundary), false);
                int leftBoundary = this.getWordPosition(-1, clickedPosition, false);
                this.moveCursorTo(Math.min(this.doubleClickCursorPos, leftBoundary), true);
            } else {
                if (clickedPosition > this.doubleClickHighlightPos) {
                    this.moveCursorToEnd(false);
                } else {
                    this.moveCursorTo(this.doubleClickHighlightPos, false);
                }
                if (clickedPosition < this.doubleClickCursorPos) {
                    this.moveCursorToStart(true);
                } else {
                    this.moveCursorTo(this.doubleClickCursorPos, true);
                }
            }

            callback.cancel();
        } else {
            // vanilla already allows for dragging across text to select individual letters,
            // we additionally support dragging outside the edit box to select everything until the beginning or end
            if (!this.isMouseOver(mouseButtonEvent.x(), mouseButtonEvent.y())) {
                if (this.highlightPos < this.findClickedPositionInText(mouseButtonEvent)) {
                    this.moveCursorToEnd(true);
                } else {
                    this.moveCursorToStart(true);
                }

                callback.cancel();
            }
        }
    }
}