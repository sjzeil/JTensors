package edu.odu.cs.cs361;

//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TestIndex {


    @Test
    public void testConstructor() {
        int[] shape = {3, 2, 2};
        Index index = new Index(shape);
        for (int i = 0; i < 3; ++i) {
            assertThat(index.get(i), is(0));
        }
        int[] zeroes = {0, 0, 0};
        assertThat(index.getIndex(), is(zeroes));
        assertThat(index.getShape(), is(shape));
        assertThat(index.toString(), is("[0,0,0]"));
        assertThat(index, is(new Index(shape)));
    }


    @Test
    public void testAdvance() {
        int[] shape = {3, 2, 2};
        Index index = new Index(shape);
        Index index0 = new Index(shape);
        int[][] expected = {
            {0, 0, 0},
            {0, 0, 1},
            {0, 1, 0},
            {0, 1, 1},
            {1, 0, 0},
            {1, 0, 1},
            {1, 1, 0},
            {1, 1, 1},
            {2, 0, 0},
            {2, 0, 1},
        };

        for (int[] exp: expected) {
            assertThat(index.getIndex(), is(exp));
            boolean OK = index.advance();
            assertTrue(OK);
            assertThat(index, is(not(index0)));
        }

        int[] idx = index.getIndex();
        for (int i = 0; i < shape.length; ++i) {
            idx[i] = shape[i] - 1;
        }
        boolean OK = index.advance();
        assertFalse(OK);

        assertThrows(java.util.NoSuchElementException.class, () -> {
            index.advance();
        }

        );
    }


}
