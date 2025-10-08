package fuzs.puzzleslib.api.core.v1.utility;

import com.google.common.base.Function;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

/**
 * A list implementation similar to {@link com.google.common.collect.Lists#transform(List, Function)}.
 * <p>
 * Offers read-access while also providing writing capabilities.
 * <p>
 * Can have a different size from the original list for elements that are converted to {@code null}.
 * <p>
 * Does not support {@code null} values.
 *
 * @param <T> the list type
 * @param <E> the delegate list type
 */
public abstract class TransformingForwardingList<T, E> extends AbstractList<T> {
    private final List<E> delegate;

    /**
     * @param delegate the original list
     */
    public TransformingForwardingList(List<E> delegate) {
        for (E element : delegate) {
            Objects.requireNonNull(element, "element is null");
        }
        this.delegate = delegate;
    }

    @Override
    public T get(int index) {
        int delegateIndex = this.delegateIndex(index);
        E delegateElement = this.delegate.get(delegateIndex);
        return this.getAsElement(delegateElement);
    }

    @Override
    public int size() {
        return this.index(this.delegate.size() - 1) + 1;
    }

    @Override
    public T set(int index, T element) {
        Objects.requireNonNull(element, "element is null");
        int delegateIndex = this.delegateIndex(index);
        E delegateElement = this.delegate.set(delegateIndex, this.getAsListElement(element));
        return this.getAsElement(delegateElement);
    }

    @Override
    public void add(int index, T element) {
        Objects.requireNonNull(element, "element is null");
        int delegateIndex = this.delegateIndex(index);
        this.delegate.add(delegateIndex != -1 ? delegateIndex : this.delegate.size(), this.getAsListElement(element));
    }

    @Override
    public T remove(int index) {
        int delegateIndex = this.delegateIndex(index);
        E delegateElement = this.delegate.remove(delegateIndex);
        return this.getAsElement(delegateElement);
    }

    /**
     * @param element the element for the original list
     * @return the element for this list, or {@code null} if inconvertible
     */
    protected abstract @Nullable T getAsElement(@Nullable E element);

    /**
     * @param t the element for this list
     * @return the element for the original list, or {@code null} if inconvertible
     */
    protected abstract @Nullable E getAsListElement(@Nullable T t);

    /**
     * @param index the index in this list
     * @return the corresponding index in the original list
     */
    private int delegateIndex(int index) {
        for (int i = 0; i < this.delegate.size(); i++) {
            E delegateElement = this.delegate.get(i);
            T t = this.getAsElement(delegateElement);
            if (t != null) {
                if (index == 0) {
                    return i;
                } else {
                    index--;
                }
            }
        }

        return -1;
    }

    /**
     * @param index the index in the original list
     * @return the corresponding index in this list
     */
    private int index(int delegateIndex) {
        for (int i = 0; i < this.delegate.size(); i++) {
            E delegateElement = this.delegate.get(i);
            T t = this.getAsElement(delegateElement);
            if (t != null) {
                if (i == delegateIndex) {
                    return i;
                }
            } else {
                delegateIndex--;
            }
        }

        return -1;
    }
}
