package fuzs.puzzleslib.fabric.impl.capability.data;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import net.minecraft.nbt.CompoundTag;

public class ComponentAdapter<T, C extends CapabilityComponent<T>> implements ComponentV3 {
    private static final ComponentAdapter<?, ?> EMPTY = new ComponentAdapter<>(null) {

        @Override
        public CapabilityComponent<Object> getComponent() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void readFromNbt(CompoundTag tag) {
            // NO-OP
        }

        @Override
        public void writeToNbt(CompoundTag tag) {
            // NO-OP
        }
    };

    private final C component;

    public ComponentAdapter(C component) {
        this.component = component;
    }

    public static <T, C extends CapabilityComponent<T>> ComponentAdapter<T, C> empty() {
        return (ComponentAdapter<T, C>) EMPTY;
    }

    public C getComponent() {
        return this.component;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        this.component.read(tag);
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        this.component.write(tag);
    }
}
