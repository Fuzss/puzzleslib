package fuzs.puzzleslib.json.adapter;

import com.google.gson.*;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * a json adapter for generic regsitry entries
 * @param <T> registry data type
 */
public class RegistryEntryAdapter<T extends IForgeRegistryEntry<T>> implements JsonDeserializer<T>, JsonSerializer<T> {

    /**
     * forge registry
     */
    private final IForgeRegistry<T> registry;

    /**
     * initialize by providing registry
     * @param registry forge registry
     */
    public RegistryEntryAdapter(IForgeRegistry<T> registry) {

        this.registry = registry;
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        return this.getRegistryEntry(json, "identifier");
    }

    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {

        ResourceLocation resourcelocation = this.registry.getKey(src);
        if (resourcelocation != null) {

            return new JsonPrimitive(resourcelocation.toString());
        }

        throw new IllegalArgumentException("Can't serialize unknown entry " + src);
    }

    /**
     * @return type (name) of registry
     */
    private String getType() {

        return this.registry.getRegistryName().getPath();
    }

    /**
     * @param json json element source
     * @param memberName member to get
     * @return member converted to registry entry
     */
    private T getRegistryEntry(JsonObject json, String memberName) {

        if (json.has(memberName)) {

            return getRegistryEntry(json.get(memberName), memberName);
        } else {

            throw new JsonSyntaxException("Missing " + memberName + ", expected to find entry of type " + this.getType());
        }
    }

    /**
     * @param json json element source
     * @param memberName member to get
     * @return member converted to registry entry
     */
    private T getRegistryEntry(JsonElement json, String memberName) {

        if (json.isJsonPrimitive()) {

            String s = json.getAsString();
            ResourceLocation registryKey = new ResourceLocation(s);

            return this.getOptional(registryKey).orElseThrow(() ->
                    new JsonSyntaxException("Expected " + memberName + " to be of type " + this.getType() + ", was unknown string '" + s + "'"));
        } else {

            throw new JsonSyntaxException("Expected " + memberName + " to be of type " + this.getType() + ", was " + JSONUtils.getType(json));
        }
    }

    /**
     * @param registryKey key to get
     * @return optional key from registry
     */
    private Optional<T> getOptional(ResourceLocation registryKey) {

        if (this.registry.containsKey(registryKey)) {

            return Optional.ofNullable(this.registry.getValue(registryKey));
        }

        return Optional.empty();
    }

}
