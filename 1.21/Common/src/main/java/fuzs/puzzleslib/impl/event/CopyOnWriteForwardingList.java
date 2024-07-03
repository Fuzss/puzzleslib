package fuzs.puzzleslib.impl.event;

import com.google.common.collect.ForwardingList;

import java.util.ArrayList;
import java.util.List;

public final class CopyOnWriteForwardingList<T> extends ForwardingList<T> {
    private List<T> delegate;
    private boolean copyOnWrite = true;

    public CopyOnWriteForwardingList(List<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<T> delegate() {
        return this.delegate;
    }

    @Override
    public void add(int index, T element) {
        this.tryCopyOnWrite();
        super.add(index, element);
    }

    @Override
    public T remove(int index) {
        this.tryCopyOnWrite();
        return super.remove(index);
    }

    @Override
    public T set(int index, T element) {
        this.tryCopyOnWrite();
        return super.set(index, element);
    }

    private void tryCopyOnWrite() {
        if (this.copyOnWrite) {
            this.delegate = new ArrayList<>(this.delegate);
            this.copyOnWrite = false;
        }
    }
}
