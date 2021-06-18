package com.fuzs.puzzleslib.config.option;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public abstract class ConfigOption<T> {

    private final ForgeConfigSpec.ConfigValue<T> value;
    private final ModConfig.Type type;
    private final List<String> path;
    private final String name;
    private final Supplier<T> defaultValue;
    private final boolean restart;
    private final String[] comment;
    private final List<Consumer<T>> syncConsumers;
    private final List<Runnable> reloadListeners;

    ConfigOption(ForgeConfigSpec.ConfigValue<T> value, ModConfig.Type type, ConfigOptionBuilder<T> builder) {

        this.value = value;
        this.type = type;
        this.path = value.getPath();
        this.name = builder.name;
        this.defaultValue = () -> builder.defaultValue;
        this.restart = builder.restart;
        this.comment = builder.comment;
        this.syncConsumers = builder.syncConsumers;
        this.reloadListeners = builder.reloadListeners;
    }

    public T get() {

        return this.value.get();
    }

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

        return this.defaultValue.get();
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

    public static abstract class ConfigOptionBuilder<T> {

        final String name;
        final T defaultValue;
        String[] comment = new String[0];
        private boolean restart;
        private final List<Consumer<T>> syncConsumers = Lists.newArrayList();
        private final List<Runnable> reloadListeners = Lists.newArrayList();

        ConfigOptionBuilder(String name, T defaultValue) {

            this.name = name;
            this.defaultValue = defaultValue;
        }

        public ConfigOptionBuilder<T> comment(String... comment) {

            this.comment = comment;
            return this;
        }

        public ConfigOptionBuilder<T> restart() {

            this.restart = true;
            return this;
        }

        public ConfigOptionBuilder<T> sync(Consumer<T> syncToField) {

            this.syncConsumers.add(syncToField);
            return this;
        }

        public ConfigOptionBuilder<T> listen(Runnable runOnReload) {

            this.reloadListeners.add(runOnReload);
            return this;
        }

        abstract BiFunction<ForgeConfigSpec.ConfigValue<T>, ModConfig.Type, ConfigOption<T>> getFactory();

        abstract ForgeConfigSpec.ConfigValue<T> getConfigValue(ForgeConfigSpec.Builder builder);

    }

}
