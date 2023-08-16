/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package fuzs.puzzleslib.api.data.v1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ExistingFileHelper.ResourceType;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * <p>Dataprovider for using a Codec to generate jsons.
 * Path names for jsons are derived from the given registry folder and each entry's namespaced id, in the format:</p>
 * <pre>
 * {@code <assets/data>/entryid/registryfolder/entrypath.json }
 * </pre>
 *
 * @param <T> the type of thing being generated.
 */
public class JsonCodecProvider<T> implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    protected final DataGenerator output;
    protected final ExistingFileHelper existingFileHelper;
    protected final String modid;
    protected final DynamicOps<JsonElement> dynamicOps;
    protected final PackType packType;
    protected final String directory;
    protected final Codec<T> codec;
    protected final Map<ResourceLocation, T> entries;

    /**
     * @param output     {@link DataGenerator}.
     * @param dynamicOps DynamicOps to encode values to jsons with using the provided Codec, e.g. {@link JsonOps#INSTANCE}.
     * @param packType   PackType specifying whether to generate entries in assets or data.
     * @param directory  String representing the directory to generate jsons in, e.g. "dimension" or "cheesemod/cheese".
     * @param codec      Codec to encode values to jsons with using the provided DynamicOps.
     * @param entries    Map of named entries to serialize to jsons. Paths for values are derived from the ResourceLocation's entryid:entrypath as specified above.
     */
    public JsonCodecProvider(DataGenerator output, ExistingFileHelper existingFileHelper, String modid, DynamicOps<JsonElement> dynamicOps, PackType packType, String directory, Codec<T> codec, Map<ResourceLocation, T> entries) {
        // Track generated data so other dataproviders can validate if needed.
        final ResourceType resourceType = new ResourceType(packType, ".json", directory);
        for (ResourceLocation id : entries.keySet()) {
            existingFileHelper.trackGenerated(id, resourceType);
        }
        this.output = output;
        this.existingFileHelper = existingFileHelper;
        this.modid = modid;
        this.dynamicOps = dynamicOps;
        this.packType = packType;
        this.directory = directory;
        this.codec = codec;
        this.entries = entries;
    }

    @Override
    public void run(final HashCache cache) throws IOException {
        final Path outputFolder = this.output.getOutputFolder().resolve(this.packType.getDirectory());
        this.gather(LamdbaExceptionUtils.rethrowBiConsumer((id, value) -> {
            final Path path = outputFolder.resolve(id.getNamespace()).resolve(this.directory).resolve(id.getPath() + ".json");
            JsonElement encoded = this.codec.encodeStart(this.dynamicOps, value).getOrThrow(false, msg -> LOGGER.error("Failed to encode {}: {}", path, msg));
            DataProvider.save(GSON, cache, encoded, path);
        }));
    }

    protected void gather(BiConsumer<ResourceLocation, T> consumer) {
        this.entries.forEach(consumer);
    }

    @Override
    public String getName() {
        return String.format("%s generator for %s", this.directory, this.modid);
    }
}
