package fuzs.puzzleslib.config;

import com.google.common.collect.Lists;
import fuzs.puzzleslib.PuzzlesLib;
import fuzs.puzzleslib.config.option.ConfigOption;
import fuzs.puzzleslib.config.option.OptionsBuilder;
import fuzs.puzzleslib.config.serialization.EntryCollectionBuilder;
import fuzs.puzzleslib.element.AbstractElement;
import fuzs.puzzleslib.element.ElementRegistry;
import fuzs.puzzleslib.element.side.ISidedElement;
import fuzs.puzzleslib.json.JsonConfigFileUtil;
import com.google.common.collect.ImmutableSet;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * main config manager for all mods
 */
public class ConfigManager {

    /**
     * fires on both loading and reloading, loading phase is required for initial setup
     * @param evt event provided by Forge
     */
    private static void onModConfig(final ModConfig.ModConfigEvent evt, Collection<AbstractElement> generalElement, Collection<AbstractElement> allElements) {

        ModConfig.Type type = evt.getConfig().getType();
        syncOptions(allElements, type, evt instanceof ModConfig.Reloading);
        // separate general element so we can sync after everything else has been reloaded as syncing might rely on config values that have just been updated
        getAllOptions(generalElement, type).forEach(ConfigOption::sync);
    }

    /**
     * register configs from non-empty builders and add listener from active mod container to {@link #onModConfig}
     * @param generalElement separate dummy element for managing all other elements
     * @param allElements all elements for relevant <code>modId</code>
     * @param fileName file name possibly inside of directory without type
     * @return {@link ModConfig.Type} constants for config types created
     */
    public static List<ModConfig.Type> load(AbstractElement generalElement, Collection<AbstractElement> allElements, Function<ModConfig.Type, String> fileName) {

        List<ModConfig.Type> createdConfigTypes = Lists.newArrayListWithCapacity(ModConfig.Type.values().length);
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

            optionsBuilder.build().ifPresent(spec -> {

                createdConfigTypes.add(type);
                ModLoadingContext.get().registerConfig(type, spec, fileName.apply(type));
            });
        }

        FMLJavaModLoadingContext.get().getModEventBus().addListener((ModConfig.ModConfigEvent evt) -> onModConfig(evt, ImmutableSet.of(generalElement), allElements));
        return createdConfigTypes;
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
     * sync all config entries and notify all listeners
     * @param elements all elements for relevant mod
     * @param type config type for this listener
     */
    public static void syncOptions(Collection<AbstractElement> elements, ModConfig.Type type) {

        syncOptions(elements, type, false);
    }

    /**
     * sync config entries for specific type of config
     * call listeners for type as the config has somehow been loaded
     * @param allElements all allElements for relevant mod
     * @param type config type for this listener
     * @param printLog print reloaded elements to console
     */
    private static void syncOptions(Collection<AbstractElement> allElements, ModConfig.Type type, boolean printLog) {

        Collection<ConfigOption<?>> options = getAllOptions(allElements, type);
        if (!options.isEmpty()) {

            options.forEach(ConfigOption::sync);
        }

        if (printLog) {

            PuzzlesLib.LOGGER.info("Reloaded " + type.extension() + " config options for " + (options.isEmpty() ? "no elements" : ElementRegistry.joinElementNames(allElements)));
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
