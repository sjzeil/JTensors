package edu.odu.cs.cs361;

import java.util.Arrays;

public class Index {

    int[] shape;
    int[] idx;

    /**
     * Create an index [0, 0, ...] of length matching that of the given shape
     * @param shape An array of the limits that each index is allowed to take on.
     */
    public Index(int[] shape) {
        this.shape = Arrays.copyOf(shape, shape.length);
        idx = new int[shape.length];
        Arrays.fill(idx, 0);
    }

    /**
     * Advance the index to a new value. Over a series of calls to this
     * function, the index will take on all legal values as defined by the
     * shape supplied to the constructor.  For example, and index might
     * start at [0,0,0], after the first advance go to [0,0,1], then [0,0,2],
     * eventually moving to [0,1,0], [0,1,1], and so on.
     * @return true if the new value exists, false if all legal values
     *   have been returned.
     */
    public boolean advance() {
        if (idx[0] >= shape[0])
            throw new java.util.NoSuchElementException();
        int i = shape.length - 1;
        idx[i] += 1;
        while (i > 0 && idx[i] >= shape[i]) {
            idx[i] = 0;
            --i;
            idx[i] += 1;
        }
        return (idx[0] < shape[0]);
    }

    /**
     * Return the entire index as an int[]. Warning: Treat this as read-only.
     * Changing this index may invalidate subsequent operations.
     * @return
     */
    public int[] getIndex() {
        return idx;
    }

    /**
     * Return the value of a particular element in the index.
     * @param i position within the index
     * @return current value of that index element
     */
    public int get(int i) {
        return idx[i];
    }

    /**
     * 
     * @return the shape
     */
    public int[] getShape() {
        return Arrays.copyOf(shape, shape.length);
    }

    /**
     * Render the index as a string.
     * @see java.lang.Object#toString()
     */
    @Override 
    public String toString() {
        StringBuffer out = new StringBuffer();
        out.append('[');
        for (int i = 0; i < idx.length; ++i) {
            if (i > 0)
                out.append(',');
            out.append(Integer.toString(idx[i]));
        }
        out.append(']');
        return out.toString();
    }

    @Override 
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Index)) return false;
        Index index = (Index)o;
        if (shape.length != index.shape.length) return false;
        if (!Arrays.equals(shape, index.shape)) return false;
        return Arrays.equals(idx, index.idx);
    }

    @Override 
    public int hashCode() {
        return Arrays.hashCode(shape) + 29 *Arrays.hashCode(idx);
    }


}
