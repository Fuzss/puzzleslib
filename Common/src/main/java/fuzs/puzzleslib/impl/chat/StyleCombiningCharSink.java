package fuzs.puzzleslib.impl.chat;

import fuzs.puzzleslib.api.util.v1.ComponentHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSink;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class StyleCombiningCharSink implements FormattedCharSink {
    private final List<Map.Entry<StringBuilder, Style>> builders;

    public StyleCombiningCharSink() {
        this.builders = new ArrayList<>(List.of(Map.entry(new StringBuilder(), Style.EMPTY)));
    }

    @Override
    public boolean accept(int width, Style style, int codePoint) {
        Map.Entry<StringBuilder, Style> entry = this.builders.getLast();
        StringBuilder stringBuilder;

        // do not sanitise the style here already, so we can check for equality
        if (entry.getValue() == style || entry.getValue().isEmpty() && style.isEmpty()) {
            stringBuilder = entry.getKey();
        } else {
            stringBuilder = new StringBuilder();
            this.builders.add(Map.entry(stringBuilder, style));
        }

        stringBuilder.appendCodePoint(codePoint);
        return true;
    }

    public Component getAsComponent() {
        Stream.Builder<MutableComponent> builder = Stream.builder();
        this.iterate((String string, Style style) -> {
            builder.accept(Component.literal(string).withStyle(style));
        });

        return builder.build().reduce(MutableComponent::append).orElseGet(Component::empty);
    }

    public String getAsString() {
        StringBuilder builder = new StringBuilder();
        this.iterate((String string, Style style) -> {
            if (!style.isEmpty()) {
                builder.append(ComponentHelper.getLegacyFormatString(style));
            }

            builder.append(string);

            if (!style.isEmpty()) {
                builder.append(ChatFormatting.RESET);
            }
        });

        return builder.toString();
    }

    private void iterate(BiConsumer<String, Style> componentConsumer) {
        for (Map.Entry<StringBuilder, Style> entry : this.builders) {
            if (!entry.getKey().isEmpty() || !entry.getValue().isEmpty()) {
                Style style = ComponentHelper.sanitizeLegacyFormat(entry.getValue());
                componentConsumer.accept(entry.getKey().toString(), style);
            }
        }
    }

    @FunctionalInterface
    public interface FormattedContentComposer<T> extends Function<StyleCombiningCharSink, T> {

    }
}
