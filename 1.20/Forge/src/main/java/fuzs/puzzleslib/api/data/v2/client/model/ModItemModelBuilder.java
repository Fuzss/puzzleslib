package fuzs.puzzleslib.api.data.v2.client.model;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Map;

public class ModItemModelBuilder extends UncheckedModelBuilder<ModItemModelBuilder> {
    private final ItemModelBuilder itemModelBuilder;

    public ModItemModelBuilder(ResourceLocation outputLocation, ExistingFileHelper fileHelper) {
        super(outputLocation, fileHelper);
        this.itemModelBuilder = new ItemModelBuilder(outputLocation, fileHelper);
    }

    public ItemModelBuilder.OverrideBuilder override() {
        return this.itemModelBuilder.override();
    }

    public ItemModelBuilder.OverrideBuilder override(int index) {
        return this.itemModelBuilder.override(index);
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = super.toJson();
        JsonObject json = this.itemModelBuilder.toJson();
        if (json.has("overrides")) {
            root.add("overrides", json.get("overrides"));
        }
        return root;
    }

    @Override
    public Map<String, String> textures() {
        return this.textures;
    }
}
