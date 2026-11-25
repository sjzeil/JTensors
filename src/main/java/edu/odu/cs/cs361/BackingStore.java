package edu.odu.cs.cs361;

import java.util.Iterator;


/**
 * A multi-dimensional store indexed by a sequence of integers.
 */
public interface BackingStore<E> extends Cloneable, Iterable<TensorEntry<E>> {


    /**
     * @return the dimension of this store, i.e., the number of indices required to access a value
     */
    public int getArity();

    /**
     * @return the shape of this store, i.e., the width of each index,.
     * The length of the returned array is the arity of the store.
     */
    public int[] getShape();


    /**
     * Retrieve a value from the store, given indices as explicit parameters or an an array of int.
     * 
     * @param indices the indices of the desired value. Can be passed as separate integer values or as an int[] array.
     * @return the value assigned, implicitly or explicitly, to that location
     * @throws IncorrectArity if the number of indices is inappropriate to the tensor
     * @throws ArrayIndexOutOfBoundsException if an index is negative or greater or equal to the width of that index in the store.
     */
    public E get(final int... indices) throws IncorrectArity;


    /**
     * Assign a value to an indexed position.
     * @param value value to assign
     * @param indices position, expressed as a list of int parameters or an int array.
     * @throws IncorrectArity if the number of indices does not match the declaration of the tensor
     */
    public void assign(E value, final int... indices) throws IncorrectArity;

    /**
     * Same as assign(value, indices).
     * @param indices position.
     * @param value value to assign
     * @throws IncorrectArity if the number of indices does not match the declaration of the tensor
     */
    public void set(final int[] indices, E value) throws IncorrectArity;

    /**
     * Returns an iterator over the entries in the backing store that do not
     * contain the initial/default value.
     * @return the iterator
     */
    public Iterator<TensorEntry<E>> iterator();

    @Override
    public boolean equals(Object o);

    @Override
    public String toString();

    @Override
    public int hashCode();

}
