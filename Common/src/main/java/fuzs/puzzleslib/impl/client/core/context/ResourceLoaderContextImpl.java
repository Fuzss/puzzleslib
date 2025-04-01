package fuzs.puzzleslib.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.BlockStateResolverContext;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Map;
import java.util.concurrent.Executor;

public record ResourceLoaderContextImpl(ResourceManager resourceManager,
                                        Executor executor,
                                        Map<ResourceLocation, ResolvedModel> resolvedModels) implements BlockStateResolverContext.ResourceLoaderContext {
    @Override
    public void addModel(ResourceLocation resourceLocation, ResolvedModel resolvedModel) {
        this.resolvedModels.put(resourceLocation, resolvedModel);
    }
}
