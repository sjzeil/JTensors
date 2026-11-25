package edu.odu.cs.cs361;

import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TestTensors {

    Tensor<Double> tensor1;
    Tensor<Double> tensor2;
    Tensor<Double> tensor3;


    @BeforeEach
    public void setup() {
        tensor1 = new Tensor<Double>(new ArrayBackingStore<Double>(1.0, 3, 2));
        tensor2 = new Tensor<Double>(new ArrayBackingStore<Double>(0.5, 3, 2));
        tensor3 = new Tensor<Double>(new ArrayBackingStore<Double>(2.0, 3, 3));
    }

    @Test
    public void testSum() {
        assertThat(Tensors.sum(tensor1), closeTo(6.0, 0.001));
        assertThat(Tensors.sum(tensor2), closeTo(3.0, 0.001));
        assertThat(Tensors.sum(tensor3), closeTo(18.0, 0.001));
    }

    @Test
    public void testProduct() {
        assertThat(Tensors.product(tensor1), closeTo(1.0, 0.001));
        assertThat(Tensors.product(tensor2), closeTo(1.0/Math.pow(2.0, 6), 0.001));
        assertThat(Tensors.product(tensor3), closeTo(Math.pow(2.0, 9), 0.001));
    }

    @Test
    public void testAdd() {
        Tensors.add(tensor1, tensor2);
        Tensor<Double> expected = new Tensor<>(new ArrayBackingStore<Double>(1.5, 3, 2));
        assertThat(tensor1, is(expected));
    }

    @Test
    public void testMultiply() {
        Tensors.multiply(tensor1, tensor2);
        Tensor<Double> expected = tensor2;
        assertThat(tensor1, is(expected));
    }

    @Test
    public void testScale() {
        Tensors.scale(tensor2, 2.0);
        Tensor<Double> expected = tensor1;
        assertThat(tensor2, is(expected));
    }

    @Test
    public void testDotProduct() {
        double d = Tensors.dotProduct(tensor1, tensor2);
        assertThat(d, closeTo(3.0, 0.0001));
    }


}
