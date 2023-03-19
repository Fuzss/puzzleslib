package fuzs.puzzleslib.impl.event.data;

import fuzs.puzzleslib.api.event.v1.data.MutableValue;

public class ValueMutableValue<T> implements MutableValue<T> {
    private T value;

    public ValueMutableValue(T value) {
        this.value = value;
    }

    @Override
    public void accept(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return this.value;
    }
}
