package edu.odu.cs.cs361;

//import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class TestHashedBackingStore {


    @Test
    public void testConstructor()  {
        int[] dimensions = {2, 3};
        HashedBackingStore<Integer> storage = new HashedBackingStore<>(dimensions, 0);
        assertThat(storage.getArity(), is(2));
        assertThat(storage.getShape(), is(dimensions));

        int[] index = {1,1};
        assertThat(storage.get(index), is(0));

        int[] index2 = {1};
        assertThrows(IncorrectArity.class, () -> {
            storage.get(index2);
        });

        int[] index3 = dimensions;
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            storage.get(index3);
        });

        assertThat(storage.iterator().hasNext(), is(false));

        assertThat(storage, is(new HashedBackingStore<Integer>(dimensions, 0)));
        assertThat(storage, is(not(new HashedBackingStore<Integer>(dimensions, 1))));
        assertThat(storage, is(not(new HashedBackingStore<Integer>(index, 0))));
    }

        @Test
    public void testConstructor2()  {
        int[] dimensions = {2, 3};
        HashedBackingStore<Integer> storage = new HashedBackingStore<>(0, 2, 3);
        assertThat(storage.getArity(), is(2));
        assertThat(storage.getShape(), is(dimensions));

        int[] index = {1,1};
        assertThat(storage.get(index), is(0));

        int[] index2 = {1};
        assertThrows(IncorrectArity.class, () -> {
            storage.get(index2);
        });

        int[] index3 = dimensions;
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            storage.get(index3);
        });

        for (TensorEntry<Integer> entry : storage) {
            System.out.println(entry);
        }
        assertThat(storage.iterator().hasNext(), is(false));

        assertThat(storage, is(new HashedBackingStore<Integer>(dimensions, 0)));
        assertThat(storage, is(not(new HashedBackingStore<Integer>(dimensions, 1))));
        assertThat(storage, is(not(new HashedBackingStore<Integer>(index, 0))));
    }

    @Test
    public void testAssign() {
        int[] dimensions = {2, 3};
        HashedBackingStore<Integer> storage = new HashedBackingStore<>(dimensions, 0);

        int[] index = {1,1};
        storage.assign(42, index);

        assertThat(storage.get(index), is(42));

        int[] index2 = {1};
        assertThrows(IncorrectArity.class, () -> {
            storage.get(index2);
        });

        int[] index3 = dimensions;
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            storage.get(index3);
        });

        int[] index4 = {0,1};
        storage.assign(0, index4);

        assertThat(storage, contains(
                new TensorEntry<Integer>(42, 1, 1)
            ));

        assertThat(storage, is(not(new HashedBackingStore<Integer>(dimensions, 0))));
        assertThat(storage, is(not(new HashedBackingStore<Integer>(dimensions, 1))));
        assertThat(storage, is(not(new HashedBackingStore<Integer>(index, 0))));

        storage.assign(0, index);
        assertThat(storage, is(new HashedBackingStore<Integer>(dimensions, 0)));
        assertThat(storage.iterator().hasNext(), is(false));

        storage.assign(1, index);
        storage.assign(2, index4);

        assertThat(storage, hasItem(new TensorEntry<Integer>(2, 0, 1)));
        assertThat(storage, hasItem(new TensorEntry<Integer>(1, 1, 1)));
        

        assertThat(storage, containsInAnyOrder(
                new TensorEntry<Integer>(2, 0, 1),
                new TensorEntry<Integer>(1, 1, 1)
            ));
        

    }


        @Test
    public void testAssign3() {
        int[] dimensions = {5, 7, 6};
        HashedBackingStore<String> storage = new HashedBackingStore<String>(dimensions, null);

        int[] index = {2, 3, 4};
        storage.assign("test1", index);

        assertThat(storage.get(index), is("test1"));

        int[] index2 = {4, 3, 2};
        storage.assign("test2", index2);
        assertThat(storage.get(index2), is("test2"));

        assertThat(storage, containsInAnyOrder(
                new TensorEntry<String>("test1", 2, 3, 4),
                new TensorEntry<String>("test2", 4, 3, 2)
            ));
    }

}
