package fuzs.puzzleslib.impl.event;

import java.util.AbstractList;
import java.util.List;
import java.util.function.Predicate;

public final class PotentialSpawnsList<E> extends AbstractList<E> {
    private final List<E> list;
    private final Predicate<E> add;
    private final Predicate<Object> remove;

    public PotentialSpawnsList(List<E> list, Predicate<E> add, Predicate<Object> remove) {
        this.list = list;
        this.add = add;
        this.remove = remove;
    }

    @Override
    public E get(int index) {
        return this.list.get(index);
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public boolean add(E e) {
        return this.add.test(e);
    }

    @Override
    public boolean remove(Object o) {
        return this.remove.test(o);
    }
}
