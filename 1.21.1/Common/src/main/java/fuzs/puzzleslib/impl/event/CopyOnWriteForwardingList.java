package fuzs.puzzleslib.impl.event;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public final class CopyOnWriteForwardingList<T> extends AbstractList<T> {
    private List<T> delegate;
    private boolean copyOnWrite = true;

    public CopyOnWriteForwardingList(List<T> delegate) {
        this.delegate = delegate;
    }

    public List<T> delegate() {
        return this.delegate;
    }

    @Override
    public T get(int index) {
        return this.delegate().get(index);
    }

    @Override
    public T set(int index, T element) {
        this.tryCopyOnWrite();
        return this.delegate().set(index, element);
    }

    @Override
    public void add(int index, T element) {
        this.tryCopyOnWrite();
        this.delegate().add(index, element);
    }

    @Override
    public T remove(int index) {
        this.tryCopyOnWrite();
        return this.delegate().remove(index);
    }

    private void tryCopyOnWrite() {
        if (this.copyOnWrite) {
            this.delegate = new ArrayList<>(this.delegate);
            this.copyOnWrite = false;
        }
    }

    @Override
    public int size() {
        return this.delegate().size();
    }
}
