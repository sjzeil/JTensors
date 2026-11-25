package edu.odu.cs.cs361;

import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TestTensor {

    Tensor<Integer> tensor5;

    int testValue(int i1, int i2, int i3, int i4, int i5) {
        return i1 + 2 * i2 + 3 * i3 + 4 * i4 + 5 * i5;
    }

    @BeforeEach
    public void setup() {
        tensor5 = new Tensor<Integer>(new ArrayBackingStore<Integer>(0, 5, 6, 7, 6, 5));
        for (int i1 = 0; i1 < 5; ++i1) {
            for (int i2 = 0; i2 < 5; ++i2) {
                for (int i3 = 0; i3 < 5; ++i3) {
                    for (int i4 = 0; i4 < 5; ++i4) {
                        for (int i5 = 0; i5 < 5; ++i5) {
                            tensor5.assign(testValue(i1, i2, i3, i4, i5), i1, i2, i3, i4, i5);
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testConstructor() {
        Tensor<Integer> tensor1 = new Tensor<>(new ArrayBackingStore<Integer>(0, 1));
        assertThat(tensor1.getArity(), is(1));
        assertThat(tensor1.get(0), is(0));
        assertThrows(IncorrectArity.class, () -> {
            tensor1.get(1, 2);
        });
        assertThrows(IncorrectArity.class, () -> {
            tensor1.get();
        });

        Tensor<Integer> tensor2 = new Tensor<>(new ArrayBackingStore<Integer>(0, 3, 4));
        assertThat(tensor2.getArity(), is(2));
        assertThat(tensor2.get(1, 2), is(0));
        assertThrows(IncorrectArity.class, () -> {
            tensor2.get(1);
        });

        assertThat(tensor2.iterator().hasNext(), is(false));
    }

    @Test
    public void testAssign() {
        Tensor<Integer> tensor1 = new Tensor<>(new ArrayBackingStore<Integer>(0, 2));

        tensor1.assign(42, 1);
        tensor1.assign(14, 0);

        assertThat(tensor1.get(0), is(14));
        assertThat(tensor1.get(1), is(42));

        assertThat(tensor1.getShape(), is(new int[] { 2 }));
        assertThat(tensor1.width(0), is(2));
        assertThat(tensor1.getArity(), is(1));

        assertThat(tensor1, containsInAnyOrder(
                new TensorEntry<>(14, 0),
                new TensorEntry<>(42, 1)));

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            tensor1.get(2);
        });

        assertThrows(IncorrectArity.class, () -> {
            tensor1.assign(0, 1, 2);
        });

        Tensor<String> tensor2 = new Tensor<>(new ArrayBackingStore<String>("abc", 4, 3));

        tensor2.assign("Hello", 1, 2);
        assertThat(tensor2.get(1, 2), is("Hello"));

        Tensor<Integer> tensor5 = new Tensor<>(new ArrayBackingStore<Integer>(0, 2, 3, 4, 5, 6));

        tensor5.assign(-1, 1, 2, 3, 4, 5);
        assertThat(tensor5.get(1, 2, 3, 4, 5), is(-1));

    }

    @Test
    public void testSelect() {
        Tensor<Integer> tensor1 = new Tensor<>(new ArrayBackingStore<Integer>(0, 2, 3));

        int ctr = 0;
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 3; ++j) {
                tensor1.assign(ctr, i, j);
                ++ctr;
            }
        }

        Tensor<Integer> tensor2 = tensor1.select(1, 2);
        assertThat(tensor2.getArity(), is(1));
        assertThat(tensor2.getShape(), is(new int[] { 2 }));
        assertThat(tensor2.get(0), is(2));
        assertThat(tensor2.get(1), is(5));
        assertThat(tensor2.width(0), is(2));

        assertThat(tensor2, containsInAnyOrder(
                new TensorEntry<>(2, 0),
                new TensorEntry<>(5, 1)));

        assertThat(tensor2, is(not(tensor1)));

        Tensor<Integer> tensor3 = new Tensor<>(new ArrayBackingStore<Integer>(0, 2));
        tensor3.assign(2, 0);
        tensor3.assign(5, 1);

        assertThat(tensor2, is(not(tensor3)));

    }

    @Test
    public void testSelect2() {
        Tensor<Integer> tensor = new Tensor<>(new ArrayBackingStore<Integer>(0, 2, 2, 3));

        int ctr = 0;
        for (int k = 0; k < 2; ++k) {
            for (int i = 0; i < 2; ++i) {
                for (int j = 0; j < 3; ++j) {
                    tensor.assign(ctr, k, i, j);
                    ++ctr;
                }
            }
        }

        Tensor<Integer> tensor1 = tensor.select(0, 0);
        Tensor<Integer> tensor2 = tensor1.select(1, 2);

        assertThat(tensor2.getArity(), is(1));
        assertThat(tensor2.getShape(), is(new int[] { 2 }));
        assertThat(tensor2.get(0), is(2));
        assertThat(tensor2.get(1), is(5));
        assertThat(tensor2.width(0), is(2));

        Iterator<TensorEntry<Integer>> it = tensor2.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
        assertThat(tensor2, containsInAnyOrder(
                new TensorEntry<>(2, 0),
                new TensorEntry<>(5, 1)));

        assertThat(tensor2, is(not(tensor)));

        Tensor<Integer> tensor3 = new Tensor<>(new ArrayBackingStore<Integer>(0, 2));
        tensor3.assign(2, 0);
        tensor3.assign(5, 1);

        assertThat(tensor2, is(not(tensor3)));

    }

    @Test
    public void testSlice() {
        Tensor<Integer> tensor1 = tensor5.slice(0, 1, 2);
        assertThat(tensor1.getArity(), is(5));
        assertThat(tensor1.getShape(), is(new int[] { 2, 6, 7, 6, 5 }));

        for (int i1 = 0; i1 < 5; ++i1) {
            if (i1 > 1) {
                final int i1a = i1;
                assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
                    tensor1.get(i1a, 0, 0, 0, 0);
                });
                continue;
            }
            for (int i2 = 0; i2 < 5; ++i2) {
                for (int i3 = 0; i3 < 5; ++i3) {
                    for (int i4 = 0; i4 < 5; ++i4) {
                        for (int i5 = 0; i5 < 5; ++i5) {
                            assertThat(tensor1.get(i1, i2, i3, i4, i5),
                                    is(testValue(i1 + 1, i2, i3, i4, i5)));
                        }
                    }
                }
            }
        }
        assertThat(tensor1.width(0), is(2));

        Tensor<Integer> tensor2 = tensor1.slice(3, 2, 1);
        assertThat(tensor2.getArity(), is(5));
        assertThat(tensor2.getShape(), is(new int[] { 2, 6, 7, 1, 5 }));

        for (int i1 = 0; i1 < 5; ++i1) {
            if (i1 > 1) {
                final int i = i1;
                assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
                    tensor2.get(i, 0, 0, 0, 0);
                });
                continue;
            }
            for (int i2 = 0; i2 < 5; ++i2) {
                for (int i3 = 0; i3 < 5; ++i3) {
                    for (int i4 = 0; i4 < 1; ++i4) {
                        if (i4 > 1) {
                            final int i1a = i1;
                            final int i4a = i4;
                            assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
                                tensor2.get(i1a, 0, 0, i4a, 0);
                            });
                            continue;
                        }
                        for (int i5 = 0; i5 < 5; ++i5) {
                            assertThat(tensor2.get(i1, i2, i3, i4, i5),
                                    is(testValue(i1 + 1, i2, i3, i4 + 2, i5)));
                        }
                    }
                }
            }
        }

        Tensor<Integer> tensor3 = new Tensor<>(new ArrayBackingStore<Integer>(0, 5));
        for (int i = 0; i < 5; i++) {
            tensor3.assign(i, i);
        }
        Tensor<Integer> tensor4 = tensor3.slice(0, 1, 2);

        Iterator<TensorEntry<Integer>> it = tensor4.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
        assertThat(tensor4, containsInAnyOrder(
                new TensorEntry<>(1, 0),
                new TensorEntry<>(2, 1)));

    }

}
