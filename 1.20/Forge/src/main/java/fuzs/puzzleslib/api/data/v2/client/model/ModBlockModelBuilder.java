package fuzs.puzzleslib.api.data.v2.client.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Map;

public class ModBlockModelBuilder extends UncheckedModelBuilder<ModBlockModelBuilder> {

    public ModBlockModelBuilder(ResourceLocation outputLocation, ExistingFileHelper fileHelper) {
        super(outputLocation, fileHelper);
    }

    @Override
    public Map<String, String> textures() {
        return this.textures;
    }
}
