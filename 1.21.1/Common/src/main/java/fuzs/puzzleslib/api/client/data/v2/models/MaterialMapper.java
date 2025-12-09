package fuzs.puzzleslib.api.client.data.v2.models;

import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

public record MaterialMapper(ResourceLocation sheet, String prefix) {
    public Material apply(ResourceLocation name) {
        return new Material(this.sheet, name.withPrefix(this.prefix + "/"));
    }

    public Material defaultNamespaceApply(String name) {
        return this.apply(ResourceLocation.withDefaultNamespace(name));
    }
}
