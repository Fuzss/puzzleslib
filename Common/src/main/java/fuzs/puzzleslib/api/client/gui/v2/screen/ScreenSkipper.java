package fuzs.puzzleslib.api.client.gui.v2.screen;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.puzzleslib.api.client.event.v1.gui.ScreenEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * Allows for skipping a screen that's just been opened by automatically triggering the press action of a button on that
 * screen.
 */
public final class ScreenSkipper {
    public static final Codec<ScreenSkipper> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.optionalFieldOf("screen_title_translation_key").forGetter(config -> getOptionalComponent(config.titleComponent)),
                    Codec.STRING.optionalFieldOf("button_translation_key").forGetter(config -> getOptionalComponent(config.buttonComponent)),
                    Codec.STRING.optionalFieldOf("last_screen_title_translation_key")
                            .forGetter(config -> getOptionalComponent(config.lastTitleComponent)),
                    ExtraCodecs.POSITIVE_INT.optionalFieldOf("skip_buttons")
                            .forGetter(config -> config.skipButtons > 0 ? Optional.of(config.skipButtons) : Optional.empty()),
                    Codec.BOOL.optionalFieldOf("single_trigger")
                            .forGetter(config -> config.singleTrigger ? Optional.of(config.singleTrigger) : Optional.empty())
            )
            .apply(instance,
                    (Optional<String> title, Optional<String> button, Optional<String> lastTitle, Optional<Integer> skipButtons, Optional<Boolean> singleTrigger) -> {
                        ScreenSkipper screenSkipper = create();
                        title.ifPresent(screenSkipper::setTitleComponent);
                        button.ifPresent(screenSkipper::setButtonComponent);
                        lastTitle.ifPresent(screenSkipper::setLastTitleComponent);
                        skipButtons.ifPresent(screenSkipper::setSkipButtons);
                        singleTrigger.ifPresent(o -> screenSkipper.singleTrigger = o);
                        return screenSkipper;
                    }
            ));

    static Optional<String> getOptionalComponent(@Nullable Component component) {
        if (component != null && component.getContents() instanceof TranslatableContents contents) {
            return Optional.of(contents.getKey());
        } else {
            return Optional.empty();
        }
    }

    @Nullable
    private Component titleComponent;
    @Nullable
    private Component buttonComponent;
    @Nullable
    private Component lastTitleComponent;
    private int skipButtons;
    private boolean singleTrigger;
    private EventResult trigger;

    private ScreenSkipper() {
        // NO-OP
    }

    /**
     * @return the builder instance
     */
    public static ScreenSkipper create() {
        return new ScreenSkipper();
    }

    /**
     * @param titleKey the screen title translation key
     * @return the builder instance
     */
    public ScreenSkipper setTitleComponent(String titleKey) {
        return this.setTitleComponent(Component.translatable(titleKey));
    }

    /**
     * @param titleComponent the screen title component
     * @return the builder instance
     */
    public ScreenSkipper setTitleComponent(Component titleComponent) {
        this.titleComponent = titleComponent;
        return this;
    }

    /**
     * @param buttonKey the button translation key
     * @return the builder instance
     */
    public ScreenSkipper setButtonComponent(String buttonKey) {
        return this.setButtonComponent(Component.translatable(buttonKey));
    }

    /**
     * @param buttonComponent the button component
     * @return the builder instance
     */
    public ScreenSkipper setButtonComponent(Component buttonComponent) {
        this.buttonComponent = buttonComponent;
        return this;
    }

    /**
     * @param lastTitleKey the last screen title translation key
     * @return the builder instance
     */
    public ScreenSkipper setLastTitleComponent(String lastTitleKey) {
        return this.setLastTitleComponent(Component.translatable(lastTitleKey));
    }

    /**
     * @param lastTitleComponent the last screen title component
     * @return the builder instance
     */
    public ScreenSkipper setLastTitleComponent(Component lastTitleComponent) {
        this.lastTitleComponent = lastTitleComponent;
        return this;
    }

    /**
     * Define matching buttons to skip, allows for specifying buttons that cannot be targeted via their component.
     *
     * @param skipButtons the buttons to skip
     * @return the builder instance
     */
    public ScreenSkipper setSkipButtons(int skipButtons) {
        this.skipButtons = skipButtons;
        return this;
    }

    /**
     * Allow the implementation to only trigger once. Useful for screens shown during start-up.
     *
     * @return the builder instance
     */
    public ScreenSkipper setSingleTrigger() {
        this.singleTrigger = true;
        return this;
    }

    /**
     * Complete the builder by registering the implementation.
     */
    public void build() {
        Preconditions.checkState(this.titleComponent != null || this.lastTitleComponent != null,
                "screen not specified"
        );
        this.setTriggerProperty(false);
        ScreenEvents.afterInit(Screen.class).register(this::onAfterInit);
        if (this.lastTitleComponent != null) {
            ScreenEvents.remove(Screen.class).register((Screen screen) -> {
                Objects.requireNonNull(this.lastTitleComponent, "last title component is null");
                if (this.trigger == EventResult.PASS && screen.getTitle().equals(this.lastTitleComponent)) {
                    this.trigger = EventResult.ALLOW;
                }
            });
        }
    }

    private void setTriggerProperty(boolean triggered) {
        if (triggered && this.singleTrigger) {
            this.trigger = EventResult.DENY;
        } else {
            this.trigger = this.lastTitleComponent != null ? EventResult.PASS : EventResult.ALLOW;
        }
    }

    private void onAfterInit(Minecraft minecraft, Screen screen, int screenWidth, int screenHeight, List<AbstractWidget> widgets, UnaryOperator<AbstractWidget> addWidget, Consumer<AbstractWidget> removeWidget) {
        if (this.trigger == EventResult.ALLOW && (this.titleComponent == null || screen.getTitle().equals(
                this.titleComponent))) {
            this.iterateAllWidgets(widgets, this.skipButtons);
        }
    }

    private void iterateAllWidgets(List<? extends GuiEventListener> widgets, int skipButtons) {
        for (GuiEventListener guiEventListener : widgets) {
            if (guiEventListener instanceof Button button) {
                if (this.buttonComponent == null || button.getMessage().equals(this.buttonComponent)) {
                    if (skipButtons-- <= 0) {
                        button.onPress();
                        this.setTriggerProperty(true);
                        break;
                    }
                }
            } else if (guiEventListener instanceof ContainerEventHandler containerEventHandler) {
                this.iterateAllWidgets(containerEventHandler.children(), skipButtons);
            }
        }
    }
}
