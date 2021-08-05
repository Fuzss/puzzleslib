package fuzs.puzzleslib.config.option;

import fuzs.puzzleslib.element.AbstractElement;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.Optional;

public class OptionsBuilder {

    private final ForgeConfigSpec.Builder builder;
    private final ModConfig.Type type;
    private AbstractElement activeElement;
    private ConfigOption.ConfigOptionBuilder<?, ?> activeOptionBuilder;
    private int useCounter;

    public OptionsBuilder(ModConfig.Type type) {

        this.builder = new ForgeConfigSpec.Builder();
        this.type = type;
    }

    public OptionsBuilder comment(String... comment) {

        assert comment.length != 0 : "Unable to set comment on builder: " + "Empty comments not allowed";

        this.builder.comment(comment);
        return this;
    }

    public OptionsBuilder push(AbstractElement element) {

        assert this.activeElement == null : "Unable to push element on builder: " + "Element already set";

        this.activeElement = element;
        this.builder.push(element.getRegistryName().getPath());
        return this;
    }

    public OptionsBuilder push(String path) {

        this.tryCreateLast();
        this.builder.push(path);
        return this;
    }

    public OptionsBuilder pop(AbstractElement element) {

        assert element == this.activeElement : "Unable to pop element from builder: " + "No element set";

        this.pop();
        this.activeElement = null;
        return this;
    }

    public OptionsBuilder pop() {

        this.tryCreateLast();
        this.builder.pop();
        return this;
    }

    public Optional<ForgeConfigSpec> build() {

        // never create an empty spec
        return this.useCounter > 0 ? Optional.of(this.builder.build()) : Optional.empty();
    }

    public <T> GenericOption.GenericOptionBuilder<T> define(String optionName, T defaultValue) {

        return this.setBuilder(new GenericOption.GenericOptionBuilder<>(optionName, defaultValue));
    }

    public <T> ListOption.ListOptionBuilder<T> define(String optionName, List<T> defaultValue) {

        return this.setBuilder(new ListOption.ListOptionBuilder<>(optionName, defaultValue));
    }

    public BooleanOption.BooleanOptionBuilder define(String optionName, boolean defaultValue) {

        return this.setBuilder(new BooleanOption.BooleanOptionBuilder(optionName, defaultValue));
    }

    public IntegerOption.IntegerOptionBuilder define(String optionName, int defaultValue) {

        return this.setBuilder(new IntegerOption.IntegerOptionBuilder(optionName, defaultValue));
    }

    public LongOption.LongOptionBuilder define(String optionName, long defaultValue) {

        return this.setBuilder(new LongOption.LongOptionBuilder(optionName, defaultValue));
    }

    public FloatOption.FloatOptionBuilder define(String optionName, float defaultValue) {

        return this.setBuilder(new FloatOption.FloatOptionBuilder(optionName, defaultValue));
    }

    public DoubleOption.DoubleOptionBuilder define(String optionName, double defaultValue) {

        return this.setBuilder(new DoubleOption.DoubleOptionBuilder(optionName, defaultValue));
    }

    public <T extends Enum<T>> EnumOption.EnumOptionBuilder<T> define(String optionName, T defaultValue) {

        return this.setBuilder(new EnumOption.EnumOptionBuilder<>(optionName, defaultValue));
    }

    private <T extends ConfigOption.ConfigOptionBuilder<?, ?>> T setBuilder(T optionBuilder) {

        this.tryCreateLast();
        this.useCounter++;
        this.activeOptionBuilder = optionBuilder;

        return optionBuilder;
    }

    private void tryCreateLast() {

        if (this.activeOptionBuilder != null) {

            this.createOption(this.activeOptionBuilder);
            this.activeOptionBuilder = null;
        }
    }

    private <T, S> void createOption(ConfigOption.ConfigOptionBuilder<T, S> builder) {

        if (builder.restart) {

            builder.comment = ArrayUtils.addAll(builder.comment, "This option will only change after the game has been restarted.");
        }

        if (builder.comment.length != 0) {

            this.builder.comment(builder.comment);
        }

        this.activeElement.addOption(builder.createOption(builder.getConfigValue(this.builder), this.type));
    }

}
