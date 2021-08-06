package fuzs.puzzleslib.config.option;

import com.google.common.collect.Sets;
import fuzs.puzzleslib.element.AbstractElement;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class OptionBuilder {

    /**
     * forge builder used in background as this is more of a wrapper
     */
    private final ForgeConfigSpec.Builder builder;
    /**
     * config type
     */
    private final ModConfig.Type type;
    /**
     * last option builder that hasn't been built yet ({@link #createOption} hasn't been called for)
     */
    private final MutableObject<ConfigOption.ConfigOptionBuilder<?, ?>> activeOptionBuilder;
    /**
     * element this builder belongs to
     */
    private AbstractElement activeElement;
    /**
     * has this been created yet, meaning has {@link #createOption} been called
     */
    private boolean created;

    /**
     * base constructor for starting a new builder chain
     * @param type config type of this builder
     */
    public OptionBuilder(ModConfig.Type type) {

        this(new ForgeConfigSpec.Builder(), type, new MutableObject<>(), null);
    }

    /**
     * copy constructor for initializing from a {@link fuzs.puzzleslib.config.option.ConfigOption.ConfigOptionBuilder}
     * @param other previous {@link OptionBuilder}
     */
    @SuppressWarnings("CopyConstructorMissesField")
    protected OptionBuilder(OptionBuilder other) {

        this(other.builder, other.type, other.activeOptionBuilder, other.activeElement);
    }

    /**
     * @param builder forge builder
     * @param type                builder config type
     * @param activeOptionBuilder mutable last option builder
     * @param activeElement element this belongs to
     */
    private OptionBuilder(ForgeConfigSpec.Builder builder, ModConfig.Type type, MutableObject<ConfigOption.ConfigOptionBuilder<?, ?>> activeOptionBuilder, AbstractElement activeElement) {

        this.builder = builder;
        this.type = type;
        this.activeOptionBuilder = activeOptionBuilder;
        this.activeElement = activeElement;
    }

    public OptionBuilder description(String... description) {

        assert description.length != 0 : "Unable to set description on builder: " + "Empty comments not allowed";

        this.builder.comment(description);
        return this;
    }

    public OptionBuilder push(AbstractElement element) {

        assert this.activeElement == null : "Unable to push element on builder: " + "Element already set";

        this.activeElement = element;
        return this.push(element.getRegistryName().getPath());
    }

    public OptionBuilder push(String path) {

        this.tryCreate();
        this.builder.push(path);
        return this;
    }

    public OptionBuilder pop(AbstractElement element) {

        assert element == this.activeElement : "Unable to pop element from builder: " + "Wrong element set";

        OptionBuilder pop = this.pop();
        this.activeElement = null;
        return pop;
    }

    public OptionBuilder pop() {

        this.tryCreate();
        this.builder.pop();
        return this;
    }

    public Optional<ForgeConfigSpec> build() {

        // never create an empty spec
        return this.created ? Optional.of(this.builder.build()) : Optional.empty();
    }

    public <T> GenericOption.GenericOptionBuilder<T> define(String optionName, T defaultValue) {

        return this.getNext(new GenericOption.GenericOptionBuilder<>(this, optionName, defaultValue));
    }

    public <T> SetOption.SetOptionBuilder<T> define(String optionName, Collection<T> defaultValue) {

        return this.getNext(new SetOption.SetOptionBuilder<>(this, optionName, Sets.newHashSet(defaultValue)));
    }

    public BooleanOption.BooleanOptionBuilder define(String optionName, boolean defaultValue) {

        return this.getNext(new BooleanOption.BooleanOptionBuilder(this, optionName, defaultValue));
    }

    public IntegerOption.IntegerOptionBuilder define(String optionName, int defaultValue) {

        return this.getNext(new IntegerOption.IntegerOptionBuilder(this, optionName, defaultValue));
    }

    public LongOption.LongOptionBuilder define(String optionName, long defaultValue) {

        return this.getNext(new LongOption.LongOptionBuilder(this, optionName, defaultValue));
    }

    public FloatOption.FloatOptionBuilder define(String optionName, float defaultValue) {

        return this.getNext(new FloatOption.FloatOptionBuilder(this, optionName, defaultValue));
    }

    public DoubleOption.DoubleOptionBuilder define(String optionName, double defaultValue) {

        return this.getNext(new DoubleOption.DoubleOptionBuilder(this, optionName, defaultValue));
    }

    public <T extends Enum<T>> EnumOption.EnumOptionBuilder<T> define(String optionName, T defaultValue) {

        return this.getNext(new EnumOption.EnumOptionBuilder<>(this, optionName, defaultValue));
    }

    public <T extends Enum<T>> EnumsOption.EnumsOptionBuilder<T> define(String optionName, Collection<T> defaultValue, Class<T> declaringClazz) {

        return this.getNext(new EnumsOption.EnumsOptionBuilder<>(this, optionName, Sets.newHashSet(defaultValue), declaringClazz));
    }

    private <T extends ConfigOption.ConfigOptionBuilder<?, ?>> T getNext(T optionsBuilder) {

        this.tryCreate();
        this.activeOptionBuilder.setValue(optionsBuilder);
        return optionsBuilder;
    }

    private void tryCreate() {

        if (this.activeOptionBuilder.getValue() != null) {

            this.createOption(this.activeOptionBuilder.getValue());
            this.activeOptionBuilder.setValue(null);
            this.created = true;
        }
    }

    private <T, S> void createOption(ConfigOption.ConfigOptionBuilder<T, S> builder) {

        List<String> comment = builder.getComment();
        if (!comment.isEmpty()) {

            this.builder.comment(comment.toArray(new String[0]));
        }

        this.activeElement.addOption(builder.createOption(builder.getConfigValue(this.builder), this.type));
    }

}
