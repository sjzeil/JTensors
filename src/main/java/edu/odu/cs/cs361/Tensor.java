package edu.odu.cs.cs361;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A multi-dimensional store indexed by a sequence of integers.
 */
public class Tensor<E> implements Cloneable, Iterable<TensorEntry<E>> {

    private BackingStore<E> backingStore;
    private int[] backingShape;
    private int[] shape;
    private int[] startIndex;
    private boolean[] selected;

    public Tensor(BackingStore<E> backingStore) {
        this.backingStore = backingStore;
        this.backingShape = backingStore.getShape();
        this.startIndex = new int[backingShape.length];
        Arrays.fill(this.startIndex, 0);
        this.shape = Arrays.copyOf(backingShape, backingShape.length);
        this.selected = new boolean[backingShape.length];
        Arrays.fill(this.selected, false);
    }

    /**
     * @return the dimension of this tensor
     */
    public int getArity() {
        return shape.length;
    }

    /**
     * 
     * @param indexNum position of an index
     * @return the maximum permitted value for that index
     */
    public int width(int indexNum) {
        if (indexNum < 0 || indexNum >= shape.length) {
            throw new ArrayIndexOutOfBoundsException("Index number out of bounds");
        }
        return shape[indexNum];
    }

    /**
     * @return an array containing the shape of this tensor
     */
    public int[] getShape() {
        return Arrays.copyOf(shape, shape.length);
    }

    /**
     * Retrieve a value from a tensor, given indices as explicit parameters or an an
     * array of int.
     * 
     * @param index the index of the desired value.
     *              Can be passed as a sequence of int parameters or as an array.
     * @return the value, or None if no value has been assigned, implicitly or
     *         explicitly, to that location
     * @throws IncorrectArity                 if the number of indices is
     *                                        inappropriate to the tensor
     * @throws ArrayIndexOutOfBoundsException if an index is negative or, for some
     *                                        tensors, too large
     */
    public E get(final int... index) throws IncorrectArity {
        return backingStore.get(backingIndex(index));
    }

    private int[] backingIndex(int... index) {
        if (index.length != shape.length) 
            throw new IncorrectArity("Index of length " + index.length + " supplied to tensor expecting an index of length " + shape.length);
        int[] result = new int[backingShape.length];
        int j = 0;
        for (int i = 0; i < result.length; ++i) {
            if (selected[i]) {
                result[i] = startIndex[i];
            } else {
                if (index[j] < 0 || index[j] >= shape[j]) {
                    throw new ArrayIndexOutOfBoundsException();
                }
                result[i] = index[j] + startIndex[i];
                ++j;
            }
        }
        return result;
    }

    private int[] apparentIndex(int... index) {
        int[] result = new int[shape.length];
        int j = 0;
        for (int i = 0; i < backingShape.length; ++i) {
            if (selected[i] && index[i] != startIndex[i]) return null;
            if (!selected[i]) {
                if (index[i] < 0 || index[i] - startIndex[i] >= shape[j]) return null;
                result[j] = index[i] - startIndex[i];
                ++j;
            }
        }
        return result;
    }

    /**
     * Assign a value to an indexed position.
     * 
     * @param value value to assign
     * @param index position, expressed as a list of int parameters or an int array
     * @throws IncorrectArity if the number of indices does not match the
     *                        declaration of the tensor
     */
    public void assign(E value, int... index) throws IncorrectArity {
        backingStore.assign(value, backingIndex(index));
    }

    /**
     * Provide an iterator over entries in the tensor that are not equal to
     * the initial/default value.
     * What constitutes an "explicitly assigned" depends on the backing store.
     * 
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<TensorEntry<E>> iterator() {
        return new TensorIterator();
    }

    private class TensorIterator implements Iterator<TensorEntry<E>> {
        private Iterator<TensorEntry<E>> backingIterator;
        private TensorEntry<E> nextEntry;

        public TensorIterator() {
            backingIterator = backingStore.iterator();
            nextEntry = null;
            advanceToNextValidIndex();
        }

        private void advanceToNextValidIndex() {
            nextEntry = null;
            while (backingIterator.hasNext()) {
                TensorEntry<E> current = backingIterator.next();
                int[] backingIndex = current.getKey();
                int[] apparentIndex = apparentIndex(backingIndex);
                if (apparentIndex != null) {
                    nextEntry = current;
                    break;
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

            TensorEntry<E> savedEntry = nextEntry;
            advanceToNextValidIndex();
            return new TensorEntry<E>(savedEntry.getValue(), apparentIndex(savedEntry.getKey()));
        }

    }

    /**
     * Provide a tensor, based upon this one, in which one or more of the
     * indices have been remapped to a potentially smaller range.
     * 
     * @param index the index of the dimension to fix
     * @param start the first index value in that dimension be selected
     * @param width the number of index values in that dimension to select
     * @return a reduced-size tensor
     */
    public Tensor<E> slice(int index, int start, int width) {
        int bd = backingDimension(index);
        
        if (start + width >= shape[bd]) {
            throw new ArrayIndexOutOfBoundsException();
        }
        Tensor<E> result = new Tensor<>(backingStore);
        result.selected = Arrays.copyOf(selected, selected.length);
        result.startIndex = Arrays.copyOf(startIndex, startIndex.length);
        result.shape = Arrays.copyOf(shape, shape.length);

        result.startIndex[bd] = start;
        result.shape[bd] = width;

        return result;
    }

    private int backingDimension(int dimension) {
        if (dimension < 0 || dimension >= shape.length) {
            throw new ArrayIndexOutOfBoundsException("Dimension number out of bounds");
        }
        int d = 0;
        int bd = 0;
        while (d < dimension) {
            if (!selected[bd]) {
                ++d;
                ++bd;
            } else {
                ++bd;
            }
        }
        return bd;
    }

    /**
     * Provide a tensor, based upon this one, in which one of the dimensions
     * has been fixed to a single index value, resulting in a lower arity
     * tensor with a subset of the entries in this one.
     * 
     * 
     * @param dimension the dimension to fix
     * @param value     the value to fix the index of that dimension to
     * @return a reduced-arity tensor
     */
    public Tensor<E> select(int dimension, int value) {
        int bd = backingDimension(dimension);
        if (dimension < 0 || dimension >= shape.length) {
            throw new ArrayIndexOutOfBoundsException("Dimension number out of bounds");
        }
        Tensor<E> result = new Tensor<>(backingStore);
        result.selected = Arrays.copyOf(selected, selected.length);
        result.startIndex = Arrays.copyOf(startIndex, startIndex.length);
        result.shape = new int[shape.length - 1];
        int j = 0;
        for (int i = 0; i < shape.length; ++i) {
            if (i != dimension) {
                result.shape[j] = shape[i];
                ++j;
            }
        }
        result.selected[bd] = true;
        result.startIndex[bd] = value;
        result.backingShape[dimension] = 1;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Tensor))
            return false;
        Tensor<?> tensor = (Tensor<?>) o;
        return Arrays.equals(shape, tensor.shape)
            && Arrays.equals(startIndex, tensor.startIndex)
            &&  backingStore.equals(tensor.backingStore);
    }

    @Override
    public String toString() {
        return backingStore.toString();
    }

    @Override
    public int hashCode() {
        return backingStore.hashCode();
    }


}
