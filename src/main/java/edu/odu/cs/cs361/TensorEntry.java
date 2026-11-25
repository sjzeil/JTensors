package edu.odu.cs.cs361;

import java.util.Arrays;
import java.util.Objects;

/**
 * A combination of a list of index values and the associated value stored in
 * the tensor.
 */
public class TensorEntry<E> implements Cloneable  {
    private int[] key;
    private E value;

    /**
     * Create an entry.
     * @param value the value stored in the tensor
     * @param index the index of the value in the tensor. Can be passed as
     *    separate integer values or as an in[] array.
     */
    public TensorEntry(E value, int... index) {
        key = Arrays.copyOf(index, index.length);
        this.value = value;
    }

    /**
     * Get the key part of an entry (a sequence of integer index values).
     * 
     * @return the index of this key,value pair
     */
    public int[] getKey() {
        return key;
    }

    /**
     * Get the value part of an entry.
     * 
     * @return the value of this key,value pair
     */
    public E getValue() {
        return value;
    }

    /**
     * Check to see if two entries have equal keys and values.
     * 
     * @param o and object, usually another Entry
     * @return true if entries are equal
     */
    public boolean equals(Object o) {
        if (o != null && o instanceof TensorEntry) {
            @SuppressWarnings("unchecked")
            TensorEntry<E> e = (TensorEntry<E>) o;
            return key.length == e.key.length && Arrays.equals(key, e.key) && value.equals(e.value);
        } else {
            return false;
        }
    }

    /**
     * calculate a hash code for this entry.
     * 
     * @return a hash code
     */
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public String toString() {
        return "Entry [key=" + Arrays.toString(key) + ", value=" + value + "]";
    }

}
