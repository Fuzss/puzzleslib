package fuzs.puzzleslib.fabric.mixin.client;

import fuzs.puzzleslib.api.client.renderer.v1.RenderPropertyKey;
import fuzs.puzzleslib.fabric.impl.client.util.EntityRenderStateExtension;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.IdentityHashMap;
import java.util.Map;

@Mixin(EntityRenderState.class)
abstract class EntityRenderStateFabricMixin implements EntityRenderStateExtension {
    @Unique
    private final Map<RenderPropertyKey<?>, Object> puzzleslib$renderProperties = new IdentityHashMap<>();

    @Override
    public <T> @Nullable T puzzleslib$getRenderProperty(RenderPropertyKey<T> key) {
        return (T) this.puzzleslib$renderProperties.get(key);
    }

    @Override
    public <T> void puzzleslib$setRenderProperty(RenderPropertyKey<T> key, @Nullable T t) {
        if (t == null) {
            this.puzzleslib$renderProperties.remove(key);
        } else {
            this.puzzleslib$renderProperties.put(key, t);
        }
    }

    @Override
    public void puzzleslib$clearRenderProperties() {
        this.puzzleslib$renderProperties.clear();
    }
}
