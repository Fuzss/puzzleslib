package fuzs.puzzleslib.impl.chat;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class FormattedContentSink implements FormattedText.StyledContentConsumer<Unit>, FormattedCharSink {
    private final List<Map.Entry<StringBuilder, Style>> builders = new ArrayList<>(Collections.singletonList(Map.entry(new StringBuilder(),
            Style.EMPTY
    )));
    @Nullable
    private Component component;
    @Nullable
    private String string;

    public FormattedContentSink(FormattedText formattedText) {
        // populate builders even when this is a component as we may want to convert to string later
        formattedText.visit(this, Style.EMPTY);
        if (formattedText instanceof Component) {
            this.component = ((Component) formattedText).copy();
        }
    }

    public FormattedContentSink(FormattedCharSequence formattedCharSequence) {
        formattedCharSequence.accept(this);
    }

    @Override
    public Optional<Unit> accept(Style style, String string) {
        Map.Entry<StringBuilder, Style> entry = this.builders.get(this.builders.size() - 1);
        if (entry.getValue() == style || entry.getValue().isEmpty() && style.isEmpty()) {
            entry.getKey().append(string);
        } else {
            this.builders.add(Map.entry(new StringBuilder(string), style));
        }

        return Optional.empty();
    }

    @Override
    public boolean accept(int width, Style style, int codePoint) {
        this.accept(style, String.valueOf(Character.toChars(codePoint)));
        return true;
    }

    public Component getComponent() {

        if (this.component == null) {

            MutableComponent component = null;
            for (Map.Entry<StringBuilder, Style> entry : this.builders) {

                MutableComponent componentFromEntry = Component.literal(entry.getKey().toString())
                        .setStyle(entry.getValue());

                if (component == null) {
                    component = componentFromEntry;
                } else {
                    component.append(componentFromEntry);
                }
            }

            Objects.requireNonNull(component, "component is null");
            this.component = component;
        }

        return this.component;
    }

    public String getString() {

        if (this.string == null) {

            StringBuilder builder = new StringBuilder();
            for (Map.Entry<StringBuilder, Style> entry : this.builders) {

                builder.append(this.getStringFormatFromStyle(entry.getValue()));
                builder.append(entry.getKey());
                // it's ok to add reset if when no formatting codes have been applied before
                builder.append(ChatFormatting.RESET);
            }

            this.string = builder.toString();
        }

        return this.string;
    }

    public String getStringFormatFromStyle(Style style) {
        if (style.isEmpty()) {
            return "";
        } else {
            return this.getFormatFromStyle(style).stream().map(ChatFormatting::toString).collect(Collectors.joining());
        }
    }

    public List<ChatFormatting> getFormatFromStyle(Style style) {

        List<ChatFormatting> list = new ArrayList<>();

        // color must be added before formatting
        if (style.getColor() != null) {
            ChatFormatting color = ChatFormatting.getByName(style.getColor().serialize());
            if (color != null) {
                list.add(color);
            }
        }

        // multiple formatting codes may exist at the same time
        if (style.isObfuscated()) {
            list.add(ChatFormatting.OBFUSCATED);
        }
        if (style.isBold()) {
            list.add(ChatFormatting.BOLD);
        }
        if (style.isStrikethrough()) {
            list.add(ChatFormatting.STRIKETHROUGH);
        }
        if (style.isUnderlined()) {
            list.add(ChatFormatting.UNDERLINE);
        }
        if (style.isItalic()) {
            list.add(ChatFormatting.ITALIC);
        }

        return list;
    }
}
