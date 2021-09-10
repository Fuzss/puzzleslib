package fuzs.puzzleslib.config.option;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.List;
import java.util.function.Consumer;

public abstract class ConfigOption<T, S> {

    /**
     * splitter for {@link #split}
     */
    private static final Splitter DOT_SPLITTER = Splitter.on(".");

    private final ForgeConfigSpec.ConfigValue<S> value;
    private final ModConfig.Type type;
    private final List<String> path;
    private final String name;
    private final T defaultValue;
    private final boolean restart;
    private final String[] comment;
    private final List<Consumer<T>> syncConsumers;
    private final List<Runnable> reloadListeners;

    ConfigOption(ForgeConfigSpec.ConfigValue<S> value, ModConfig.Type type, ConfigOptionBuilder<T, S> builder) {

        this.value = value;
        this.type = type;
        this.path = value.getPath();
        this.name = builder.name;
        this.defaultValue = builder.defaultValue;
        this.restart = builder.restart;
        this.comment = builder.comment;
        this.syncConsumers = builder.syncConsumers;
        this.reloadListeners = builder.reloadListeners;
    }

    public T get() {

        return this.convertValue(this.value.get());
    }

    abstract T convertValue(S value);

    public boolean isType(ModConfig.Type type) {

        return this.type == type;
    }

    public List<String> getPath() {

        return this.path;
    }

    public String getName() {

        return this.name;
    }

    public T getDefault() {

        return this.defaultValue;
    }

    public boolean isRestartRequired() {

        return this.restart;
    }

    public String[] getComment() {

        return this.comment;
    }

    public void sync() {

        for (Consumer<T> syncToField : this.syncConsumers) {

            syncToField.accept(this.get());
        }

        for (Runnable reloadListener : this.reloadListeners) {

            reloadListener.run();
        }
    }

    public void addSyncConsumer(Consumer<T> syncToField) {

        this.syncConsumers.add(syncToField);
    }

    public void addReloadListener(Runnable runOnReload) {

        this.reloadListeners.add(runOnReload);
    }

    /**
     * split helper copied from {@link ForgeConfigSpec}
     * @param path path to split
     * @return split path as list
     */
    static List<String> split(String path) {

        return Lists.newArrayList(DOT_SPLITTER.split(path));
    }

    public static abstract class ConfigOptionBuilder<T, S> extends OptionBuilder {

        final String name;
        final T defaultValue;
        private String[] comment = new String[0];
        private boolean restart;
        private final List<Consumer<T>> syncConsumers = Lists.newArrayList();
        private final List<Runnable> reloadListeners = Lists.newArrayList();

        ConfigOptionBuilder(OptionBuilder previous, String name, T defaultValue) {

            super(previous);
            this.name = name;
            this.defaultValue = defaultValue;
        }

        List<String> buildComment() {

            List<String> comment = Lists.newArrayList(this.comment);
            if (this.restart) {

                comment.add("This option requires a game restart to update.");
            }

            return comment;
        }

        public ConfigOptionBuilder<T, S> comment(String... comment) {

            this.comment = comment;
            return this;
        }

        public ConfigOptionBuilder<T, S> restart() {

            this.restart = true;
            return this;
        }

        public ConfigOptionBuilder<T, S> sync(Consumer<T> syncToField) {

            this.syncConsumers.add(syncToField);
            return this;
        }

        public ConfigOptionBuilder<T, S> listen(Runnable runOnReload) {

            this.reloadListeners.add(runOnReload);
            return this;
        }

        abstract ForgeConfigSpec.ConfigValue<S> getConfigValue(ForgeConfigSpec.Builder builder);

        abstract ConfigOption<T, S> createOption(ForgeConfigSpec.ConfigValue<S> value, ModConfig.Type type);

    }

}
