package fuzs.puzzleslib.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.google.common.collect.ImmutableSet;
import fuzs.puzzleslib.PuzzlesLib;
import fuzs.puzzleslib.config.option.ConfigOption;
import fuzs.puzzleslib.config.option.OptionsBuilder;
import fuzs.puzzleslib.config.serialization.EntryCollectionBuilder;
import fuzs.puzzleslib.element.AbstractElement;
import fuzs.puzzleslib.element.ElementRegistry;
import fuzs.puzzleslib.element.side.ISidedElement;
import fuzs.puzzleslib.json.JsonConfigFileUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * main config manager for all mods
 */
public class ConfigManager {

    /**
     * fires on both loading and reloading, loading phase is required for initial setup
     * @param modConfig      mod config object
     * @param generalElement separate dummy element for managing all other elements
     * @param allElements all elements for current modId
     * @param isReloading print reloaded elements to console
     */
    private static void onModConfig(ModConfig modConfig, AbstractElement generalElement, Collection<AbstractElement> allElements, boolean isReloading) {

        ModConfig.Type type = modConfig.getType();
        syncOptions(allElements, type, isReloading);
        // separate general element so we can sync after everything else has been reloaded as syncing might rely on config values that have just been updated
        getAllOptions(ImmutableSet.of(generalElement), type).forEach(ConfigOption::sync);
    }

    /**
     * register configs from non-empty builders and add listener from active mod container to {@link #onModConfig}
     * @param generalElement separate dummy element for managing all other elements
     * @param allElements all elements for relevant <code>modId</code>
     * @param activeContainer the mod
     * @param configSubPath optional config directory inside of main config dir
     * @return was any config created
     */
    public static boolean load(AbstractElement generalElement, Collection<AbstractElement> allElements, boolean loadConfigEarly, ModContainer activeContainer, String[] configSubPath) {

        boolean successful = false;
        for (ModConfig.Type type : ModConfig.Type.values()) {

            if (type == ModConfig.Type.CLIENT && FMLEnvironment.dist.isDedicatedServer() || type == ModConfig.Type.SERVER && FMLEnvironment.dist.isClient()) {

                continue;
            }

            OptionsBuilder optionsBuilder = new OptionsBuilder(type);
            create(optionsBuilder, generalElement, builder -> allElements.stream()
                    .filter(ISidedElement.getGeneralFilter(type))
                    .forEach(element -> element.setupGeneralConfig(builder)));

            for (AbstractElement element : allElements) {

                ISidedElement.setupConfig(optionsBuilder, type, element);
            }

            Optional<ForgeConfigSpec> optionalSpec = optionsBuilder.build();
            if (optionalSpec.isPresent()) {

                successful = true;
                PuzzlesLib.LOGGER.info("Loading config type {} for mod {}...", type.extension(), activeContainer.getNamespace());

                ForgeConfigSpec spec = optionalSpec.get();
                ModConfig modConfig = new ModConfig(type, spec, activeContainer, getFileName(activeContainer.getNamespace(), type, configSubPath));
                activeContainer.addConfig(modConfig);

                // server config uses a world specific path which doesn't exist at this point
                if (loadConfigEarly && type != ModConfig.Type.SERVER) {

                    loadConfigEarly(modConfig, allElements);
                }
            }
        }

        FMLJavaModLoadingContext.get().getModEventBus().addListener((ModConfig.ModConfigEvent evt) -> {

            if (!loadConfigEarly || evt instanceof ModConfig.Reloading) {

                onModConfig(evt.getConfig(), generalElement, allElements, evt instanceof ModConfig.Reloading);
            }
        });

        return successful;
    }

    /**
     * load configs during construct so they can be used in registry events
     * mostly copied from {@link net.minecraftforge.fml.config.ConfigFileTypeHandler}
     * @param modConfig mod config object
     * @param allElements all elements for current modId
     */
    private static void loadConfigEarly(ModConfig modConfig, Collection<AbstractElement> allElements) {

        final Path configPath = FMLPaths.CONFIGDIR.get().resolve(modConfig.getFileName());
        final CommentedFileConfig configData = CommentedFileConfig.builder(configPath).sync()
                .preserveInsertionOrder()
                .autosave()
                // forge also looks for default configs, which only works for server config file though which we don't handle here
                .onFileNotFound(FileNotFoundAction.CREATE_EMPTY)
                .writingMode(WritingMode.REPLACE)
                .build();

        try {

            configData.load();
        } catch (ParsingException e) {

            throw new RuntimeException("Failed loading config file " + modConfig.getFileName() + " of type " + modConfig.getType() + " for modid " + modConfig.getModId(), e);
        }

        modConfig.getSpec().setConfig(configData);
        syncOptions(allElements, modConfig.getType(), false);
        configData.save();
    }

    /**
     * wrap creation of a new category
     * @param builder       builder for config type
     * @param element       element for this new category
     * @param setupConfig   builder for category
     * @param comment       comments to add to category
     */
    public static void create(OptionsBuilder builder, AbstractElement element, Consumer<OptionsBuilder> setupConfig, String... comment) {

        if (comment.length != 0) {

            builder.comment(comment);
        }

        builder.push(element);
        setupConfig.accept(builder);
        builder.pop(element);
    }

    /**
     * sync config entries for specific type of config
     * call listeners for type as the config has somehow been loaded
     * @param allElements all allElements for relevant mod
     * @param type config type for this listener
     * @param isReloading print reloaded elements to console
     */
    public static void syncOptions(Collection<AbstractElement> allElements, ModConfig.Type type, boolean isReloading) {

        Collection<ConfigOption<?>> options = getAllOptions(allElements, type);
        if (!options.isEmpty()) {

            if (isReloading) {

                PuzzlesLib.LOGGER.info("Reloading {} config options for {}", type.extension(),  ElementRegistry.joinElementNames(allElements));
            }

            options.forEach(ConfigOption::sync);
        }
    }

    /**
     * @param elements all elements for relevant mod
     * @param type config type for this listener
     * @return collection of enabled entries only for this mod and type
     */
    public static Collection<ConfigOption<?>> getAllOptions(Collection<AbstractElement> elements, ModConfig.Type type) {

        // sync all elements, even disabled ones
        return elements.stream()
                .flatMap(element -> element.getOptions().stream())
                .filter(option -> option.isType(type))
                .collect(Collectors.toSet());
    }

    /**
     * @param type type of config
     * @param modId mod id this config belongs to
     * @return config name as if it were generated by Forge itself
     */
    public static String getFileName(String modId, ModConfig.Type type) {

        return String.format("%s-%s.toml", modId, type.extension());
    }

    /**
     * put config into it's own folder when there are multiples
     * @param modId mod id this config belongs to
     * @param type type of config
     * @param path path inside of main config directory
     * @return name lead by folder
     */
    public static String getFileName(String modId, ModConfig.Type type, String... path) {

        String fileName = getFileName(modId, type);
        if (path.length != 0) {

            String prefix = String.join(File.separator, path);
            JsonConfigFileUtil.mkdirs(prefix);

            return prefix + File.separator + fileName;
        }

        return fileName;
    }

    /**
     * @param entries entries to convert to string
     * @param <T> registry element type
     * @return entries as string list
     */
    @SafeVarargs
    public static <T extends IForgeRegistryEntry<T>> List<String> getKeyList(T... entries) {

        return Stream.of(entries)
                .map(IForgeRegistryEntry::getRegistryName)
                .filter(Objects::nonNull)
                .map(ResourceLocation::toString)
                .collect(Collectors.toList());
    }

    /**
     * deserialize string <code>data</code> into entries of a <code>registry</code>
     * @param data data as string list as provided by Forge config
     * @param registry registry to get entries from
     * @param <T> type of registry
     * @return deserialized data as set
     */
    public static <T extends IForgeRegistryEntry<T>> Set<T> deserializeToSet(List<String> data, IForgeRegistry<T> registry) {

        return new EntryCollectionBuilder<>(registry).buildEntrySet(data);
    }

    /**
     * deserialize string <code>data</code> into entries of a <code>registry</code>
     * @param data data as string list as provided by Forge config
     * @param registry registry to get entries from
     * @param <T> type of registry
     * @return deserialized data as map
     */
    public static <T extends IForgeRegistryEntry<T>> Map<T, double[]> deserializeToMap(List<String> data, IForgeRegistry<T> registry) {

        return new EntryCollectionBuilder<>(registry).buildEntryMap(data);
    }

}
