package edu.odu.cs.cs361;

import java.util.Iterator;

/**
 * A multi-dimensional store indexed by a sequence of integers.
 */
public class ArrayBackingStore<E> implements BackingStore<E> {

    private final int[] shape;
    private final E initialValue;
    private final Object[] data;

    /**
     * Create a backing store with the given shape.
     * @param theShape the shape of the store, i.e., the width of each index
     * @param initialValue the value to assign to all entries in the store
     */
    public ArrayBackingStore(int[] theShape, E initialValue) {
        this.shape = theShape.clone();
        this.initialValue = initialValue;
        int size = 1;
        for (int s : shape) {
            size *= s;
        }
        data = new Object[size];
        for (int i = 0; i < size; i++) {
            data[i] = initialValue;
        }
    }

    /**
     * Create a backing store with the given shape.
     * @param initialValue the value to assign to all entries in the store
     * @param theShape the shape of the store, i.e., the width of each index
     */
    public ArrayBackingStore(E initialValue, int... theShape) {
        this.shape = theShape.clone();
        this.initialValue = initialValue;
        int size = 1;
        for (int s : shape) {
            size *= s;
        }
        data = new Object[size];
        for (int i = 0; i < size; i++) {
            data[i] = initialValue;
        }
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


    private int flatIndex(final int[] indices) throws IncorrectArity {
        if (indices.length != shape.length) {
            throw new IncorrectArity("Expected " + shape.length + " indices, got " + indices.length);
        }
        int index = 0;
        int multiplier = 1;
        for (int i = shape.length - 1; i >= 0; i--) {
            if (indices[i] < 0 || indices[i] >= shape[i]) {
                throw new ArrayIndexOutOfBoundsException("Index " + indices[i] + " out of bounds for dimension " + i);
            }
            index += indices[i] * multiplier;
            multiplier *= shape[i];
        }
        return index;
    }

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
        int index = flatIndex(indices);
        @SuppressWarnings("unchecked")
        E value = (E) data[index];
        return value;
    }


    /**
     * Assign a value to an indexed position.
     * @param value value to assign
     * @param indices position, expressed as a list of int parameters or an int array
     * @throws IncorrectArity if the number of indices does not match the declaration of the tensor
     */
    public void assign(E value, final int... indices) throws IncorrectArity {
        int index = flatIndex(indices);
        data[index] = value;
    }

    /**
     * Same as assign(value, indices).
     * @param indices position.
     * @param value value to assign
     * @throws IncorrectArity if the number of indices does not match the declaration of the tensor
     */
    public void set(final int[] indices, E value) throws IncorrectArity {
        int index = flatIndex(indices);
        data[index] = value;
    }

    private class StorageIterator implements Iterator<TensorEntry<E>> {
        private int currentIndex = 0;

        public StorageIterator() {
            currentIndex = 0;
            advanceToNextNonDefault();
        }

        private void advanceToNextNonDefault() {
            while (currentIndex < data.length && (data[currentIndex] == initialValue || (initialValue != null && initialValue.equals(data[currentIndex])))) {
                currentIndex++;
            }
        }

        @Override
        public boolean hasNext() {
            while (currentIndex < data.length) {
                if (data[currentIndex] != initialValue && (initialValue == null || !initialValue.equals(data[currentIndex]))) {
                    return true;
                }
                currentIndex++;
                advanceToNextNonDefault();
            }
            return false;
        }

        @Override
        public TensorEntry<E> next() {
            int[] indices = new int[shape.length];
            int tempIndex = currentIndex;
            for (int i = shape.length - 1; i >= 0; i--) {
                indices[i] = tempIndex % shape[i];
                tempIndex /= shape[i];
            }
            @SuppressWarnings("unchecked")
            E value = (E) data[currentIndex];
            ++currentIndex;
            advanceToNextNonDefault();
            return new TensorEntry<E>(value, indices);
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
        if (!(o instanceof ArrayBackingStore)) return false;
        ArrayBackingStore<?> that = (ArrayBackingStore<?>) o;
        if (!java.util.Arrays.equals(shape, that.shape)) return false;
        return java.util.Arrays.equals(data, that.data);
    }

    @Override
    public String toString() {
        return "ArrayBackingStore{" +
                "shape=" + java.util.Arrays.toString(shape) +
                ", data=" + java.util.Arrays.toString(data) +
                '}';
    }

    @Override
    public int hashCode() {
        int result = java.util.Arrays.hashCode(shape);
        result = 29 * result + 17 * initialValue.hashCode() + data.hashCode();
        return result;
    }

}
