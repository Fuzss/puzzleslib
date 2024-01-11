package fuzs.puzzleslib.impl.event;

import java.util.AbstractList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.function.Predicate;

public final class PotentialSpawnsList<E> extends AbstractList<E> {
    private final List<E> list;
    private final Predicate<E> add;
    private final Predicate<E> remove;

    public PotentialSpawnsList(List<E> list, Predicate<E> add, Predicate<E> remove) {
        this.list = list;
        this.add = add;
        this.remove = remove;
    }

    @Override
    public E get(int index) {
        return this.list.get(index);
    }

    @Override
    public E set(int index, E element) {
        Objects.checkIndex(index, this.size());
        E e = this.remove(index);
        this.add(index, element);
        return e;
    }

    @Override
    public void add(int index, E element) {
        Objects.checkIndex(index, this.size() + 1);
        index = this.size() - index;
        Stack<E> stack = new Stack<>();
        while (index-- > 0) {
            stack.push(this.remove(this.size() - 1));
        }
        this.add(element);
        while (!stack.isEmpty()) {
            this.add(stack.pop());
        }
    }

    @Override
    public E remove(int index) {
        Objects.checkIndex(index, this.size());
        E e = this.get(index);
        if (this.remove(e)) {
            return e;
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public boolean add(E e) {
        return this.add.test(e);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object o) {
        return this.remove.test((E) o);
    }
}
