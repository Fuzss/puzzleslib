package fuzs.puzzleslib.api.client.data.v2;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * TODO move to {@link fuzs.puzzleslib.api.client.data.v2.models}
 */
public record ItemModelProperties(ResourceLocation modelLocation, Map<ResourceLocation, Float> modelPredicates) {

    public ItemModelProperties(ResourceLocation modelLocation) {
        this(modelLocation, Maps.newLinkedHashMap());
    }

    public ItemModelProperties put(ResourceLocation modelPropertyLocation, float modelPropertyValue) {
        this.modelPredicates.put(modelPropertyLocation, modelPropertyValue);
        return this;
    }

    public static ItemModelProperties singleOverride(ResourceLocation modelLocation, ResourceLocation modelPropertyLocation, float modelPropertyValue) {
        return new ItemModelProperties(modelLocation).put(modelPropertyLocation, modelPropertyValue);
    }

    public static ItemModelProperties twoOverrides(ResourceLocation modelLocation, ResourceLocation modelPropertyLocation1, float modelPropertyValue1, ResourceLocation modelPropertyLocation2, float modelPropertyValue2) {
        return new ItemModelProperties(modelLocation).put(modelPropertyLocation1, modelPropertyValue1)
                .put(modelPropertyLocation2, modelPropertyValue2);
    }

    public static ItemModelProperties threeOverrides(ResourceLocation modelLocation, ResourceLocation modelPropertyLocation1, float modelPropertyValue1, ResourceLocation modelPropertyLocation2, float modelPropertyValue2, ResourceLocation modelPropertyLocation3, float modelPropertyValue3) {
        return new ItemModelProperties(modelLocation).put(modelPropertyLocation1, modelPropertyValue1)
                .put(modelPropertyLocation2, modelPropertyValue2)
                .put(modelPropertyLocation3, modelPropertyValue3);
    }

    /**
     * Use in conjunction with
     * {@link ModelTemplate#create(ResourceLocation, TextureMapping, BiConsumer, ModelTemplate.JsonFactory)}.
     */
    public static ModelTemplate.JsonFactory overridesFactory(ModelTemplate modelTemplate, ItemModelProperties... allItemModelOverrides) {
        return (ResourceLocation resourceLocation, Map<TextureSlot, ResourceLocation> map) -> {
            JsonObject jsonObject = modelTemplate.createBaseTemplate(resourceLocation, map);
            JsonArray jsonArray = new JsonArray();
            for (ItemModelProperties itemModelProperties : allItemModelOverrides) {
                jsonArray.add(itemModelProperties.toJson());
            }
            jsonObject.add("overrides", jsonArray);
            return jsonObject;
        };
    }

    JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        JsonObject predicates = new JsonObject();
        for (Map.Entry<ResourceLocation, Float> entry : this.modelPredicates.entrySet()) {
            predicates.addProperty(entry.getKey().toString(), entry.getValue());
        }
        jsonObject.add("predicate", predicates);
        jsonObject.addProperty("model", this.modelLocation.toString());
        return jsonObject;
    }
}
