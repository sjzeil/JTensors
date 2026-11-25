package edu.odu.cs.cs361;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * A multi-dimensional store indexed by a sequence of integers.
 */
public class HashedBackingStore<E> implements BackingStore<E> {

    private final int[] shape;
    private final E initialValue;
    private final Map<List<Integer>, E> data;

    /**
     * Create a backing store with the given shape.
     * @param theShape the shape of the store, i.e., the width of each index
     * @param initialValue the value to assign to all entries in the store
     */
    public HashedBackingStore(int[] theShape, E initialValue) {
        this.shape = theShape.clone();
        this.initialValue = initialValue;
        data = new HashMap<>();
    }

    /**
     * Create a backing store with the given shape.
     * @param initialValue the value to assign to all entries in the store
     * @param theShape the shape of the store, i.e., the width of each index
     */
    public HashedBackingStore(E initialValue, int... theShape) {
        this.shape = theShape.clone();
        this.initialValue = initialValue;
        data = new HashMap<>();
    }


    /**
     * @return the dimension of this store, i.e., the number of indices required to access a value
     */
    public int getArity() {
        return shape.length;
    }

    /**
     * @return the shape of this store, i.e., the width of each index,.
     * The length of the returned array is the arity of the store.
     */
    public int[] getShape() {
        return shape.clone();
    }


    private List<Integer> validateIndex(final int[] indices) throws IncorrectArity {
        if (indices.length != shape.length) {
            throw new IncorrectArity("Expected " + shape.length + " indices, got " + indices.length);
        }
        for (int i = shape.length - 1; i >= 0; i--) {
            if (indices[i] < 0 || indices[i] >= shape[i]) {
                throw new ArrayIndexOutOfBoundsException("Index " + indices[i] + " out of bounds for dimension " + i);
            }
        }
        List<Integer> indexList = java.util.Arrays.stream(indices).boxed().toList();
        return indexList;}

    /**
     * Retrieve a value from the store, given indices as explicit parameters or an an array of int.
     * 
     * @param indices the indices of the desired value
     * @return the value assigned, implicitly or explicitly, to that location
     * @throws IncorrectArity if the number of indices is inappropriate to the tensor
     * @throws ArrayIndexOutOfBoundsException if an index is negative or greater or equal to the width of that index in the store.
     */
    public E get(final int... indices) throws IncorrectArity {
        if (indices.length != shape.length) {
            throw new IncorrectArity("Expected " + shape.length + " indices, got " + indices.length);
        }
        List<Integer> indexList = validateIndex(indices);
        return data.getOrDefault(indexList, initialValue);
    }


    /**
     * Assign a value to an indexed position.
     * @param value value to assign
     * @param indices position, expressed as a list of int parameters or an int array
     * @throws IncorrectArity if the number of indices does not match the declaration of the tensor
     */
    public void assign(E value, final int... indices) throws IncorrectArity {
        List<Integer> indexList = validateIndex(indices);
        if ((initialValue == null && value == null) || (initialValue != null && initialValue.equals(value))) {
            data.remove(indexList);
        } else {
            data.put(indexList, value);
        }
    }

    /**
     * Same as assign(value, indices).
     * @param indices position.
     * @param value value to assign
     * @throws IncorrectArity if the number of indices does not match the declaration of the tensor
     */
    public void set(final int[] indices, E value) throws IncorrectArity {
        assign(value, indices);
    }

    private class StorageIterator implements Iterator<TensorEntry<E>> {
        private Iterator<Map.Entry<List<Integer>, E>> mapIterator;
        private Map.Entry<List<Integer>, E> nextEntry;

        public StorageIterator() {
            mapIterator = data.entrySet().iterator();
            nextEntry = null;
            advanceToNextNonDefault();
        }

        private void advanceToNextNonDefault() {
            nextEntry = null;
            while (mapIterator.hasNext()) {
                Map.Entry<List<Integer>, E> current = mapIterator.next();
                if (current.getValue() != initialValue && (initialValue == null || ! initialValue.equals(current.getValue()))) {
                    nextEntry = current;
                    return;
                }
            }
        }

        @Override
        public boolean hasNext() {
            return nextEntry != null;
        }

        @Override
        public TensorEntry<E> next() {
            if (nextEntry == null) {
                throw new NoSuchElementException("Tried to iterate past the end.");
            }

            int[] indices = new int[shape.length];
            int i = 0;
            for (Integer k: nextEntry.getKey()) {
                indices[i] = k;
                ++i;
            }
            TensorEntry<E> entry = new TensorEntry<E>(nextEntry.getValue(), indices);
            advanceToNextNonDefault();
            return entry;
        }
    }

    /**
     * Returns an iterator over the entries in the backing store that do not
     * contain the initial/default value.
     * @return the iterator
     */
    public Iterator<TensorEntry<E>> iterator() {
        return new StorageIterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HashedBackingStore)) return false;
        HashedBackingStore<?> that = (HashedBackingStore<?>) o;
        if (initialValue == null && that.initialValue != null) return false;
        if (!initialValue.equals(that.initialValue)) return false;
        if (!java.util.Arrays.equals(shape, that.shape)) return false;
        return data.equals(that.data);
    }

    @Override
    public String toString() {
        return "HashedBackingStore{" +
                "shape=" + java.util.Arrays.toString(shape) +
                ", initialValue=" + initialValue +
                ", data=" + data +
                '}';
    }

    @Override
    public int hashCode() {
        int result = java.util.Arrays.hashCode(shape);
        result = 29 * result + 17 * initialValue.hashCode() + data.hashCode();
        return result;
    }

}
