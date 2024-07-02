package fuzs.puzzleslib.impl.client.init;

import fuzs.puzzleslib.api.client.init.v1.ModelLayerFactory;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public record ModelLayerFactoryImpl(String namespace) implements ModelLayerFactory {

    @Override
    public ModelLayerLocation register(String path) {
        return this.register(path, "main");
    }

    @Override
    public ModelLayerLocation register(String path, String layer) {
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(this.namespace, path), layer);
    }

    @Override
    public ModelLayerLocation registerInnerArmor(String path) {
        return this.register(path, "inner_armor");
    }

    @Override
    public ModelLayerLocation registerOuterArmor(String path) {
        return this.register(path, "outer_armor");
    }
}
