package fuzs.puzzleslib.config.v2;

import com.google.common.collect.Sets;
import fuzs.puzzleslib.PuzzlesLib;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

import java.nio.file.Paths;
import java.util.Set;
import java.util.function.Consumer;

public class ConfigManagerV2 {

    private static final Set<ConfigEntry<? extends ForgeConfigSpec.ConfigValue<?>, ?>> CONFIG_ENTRIES = Sets.newHashSet();

    private ConfigManagerV2() {
    }

    public static void registerMod(String modId, IEventBus modBus) {
        modBus.addListener((final ModConfig.ModConfigEvent evt) -> onModConfig(evt, modId));
        // ModConfigEvent sometimes doesn't fire on start-up, resulting in config values not being synced, so we force it once
        // not sure if this is still an issue though
        modBus.addListener((final FMLLoadCompleteEvent evt) -> sync());
    }

    @SubscribeEvent
    public static void onModConfig(final ModConfig.ModConfigEvent evt, String modId) {
        // this is fired on ModEventBus, so mod id check is not necessary here
        // we keep this as it's required on Fabric though due to a dedicated ModEventBus being absent
        if (evt.getConfig().getModId().equals(modId)) {
            final ModConfig.Type type = evt.getConfig().getType();
            sync(type);
            if (evt instanceof ModConfig.Reloading) {
                PuzzlesLib.LOGGER.info("Reloading {} config for {}", type.extension(), modId);
            }
        }
    }

    private static void sync() {
        for (ConfigEntry<? extends ForgeConfigSpec.ConfigValue<?>, ?> entry : CONFIG_ENTRIES) {
            entry.sync();
        }
    }

    private static void sync(ModConfig.Type type) {
        for (ConfigEntry<? extends ForgeConfigSpec.ConfigValue<?>, ?> configValue : CONFIG_ENTRIES) {
            if (configValue.type() == type) {
                configValue.sync();
            }
        }
    }

    public static <S extends ForgeConfigSpec.ConfigValue<T>, T> void addEntry(ModConfig.Type type, S entry, Consumer<T> save) {
        CONFIG_ENTRIES.add(new ConfigEntry<>(type, entry, save));
    }

    public static String simpleName(String modId) {
        return String.format("%s.toml", modId);
    }

    public static String defaultName(String modId, ModConfig.Type type) {
        return String.format("%s-%s.toml", modId, type.extension());
    }

    public static String moveToDir(String configDir, String fileName) {
        return Paths.get(configDir, fileName).toString();
    }

    private static class ConfigEntry<S extends ForgeConfigSpec.ConfigValue<T>, T> {
        private final ModConfig.Type type;
        private final S entry;
        private final Consumer<T> save;

        ConfigEntry(ModConfig.Type type, S entry, Consumer<T> save) {
            this.type = type;
            this.entry = entry;
            this.save = save;
        }

        ModConfig.Type type() {
            return this.type;
        }

        void sync() {
            this.save.accept(this.entry.get());
        }
    }
}
