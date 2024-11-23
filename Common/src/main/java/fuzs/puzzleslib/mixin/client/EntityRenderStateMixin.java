package fuzs.puzzleslib.mixin.client;

import fuzs.puzzleslib.api.client.util.v1.RenderPropertyKey;
import fuzs.puzzleslib.impl.client.util.EntityRenderStateExtension;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.IdentityHashMap;
import java.util.Map;

@Mixin(EntityRenderState.class)
abstract class EntityRenderStateMixin implements EntityRenderStateExtension {
    @Unique
    private final Map<RenderPropertyKey<?>, Object> mobplaques$renderProperties = new IdentityHashMap<>();

    @Override
    public <T> @Nullable T mobplaques$getRenderProperty(RenderPropertyKey<T> key) {
        return (T) this.mobplaques$renderProperties.get(key);
    }

    @Override
    public <T> void mobplaques$setRenderProperty(RenderPropertyKey<T> key, @Nullable T t) {
        if (t == null) {
            this.mobplaques$renderProperties.remove(key);
        } else {
            this.mobplaques$renderProperties.put(key, t);
        }
    }

    @Override
    public void mobplaques$clearRenderProperties() {
        this.mobplaques$renderProperties.clear();
    }
}
