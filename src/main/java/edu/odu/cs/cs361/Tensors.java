package edu.odu.cs.cs361;

/**
 * A collection of sample functions for manipulating numeric tensors.
 * Tensors
 */
public class Tensors {

    /**
     * Compute the sum of all elements in a tensor.
     * @param tensor a numeric tensor
     * @return the sum
     */
    public static double sum(Tensor<Double> tensor) {
        Index idx = new Index(tensor.getShape());
        boolean OK = true;
        double sum = 0.0;
        while (OK) {
            int[] index = idx.getIndex();
            sum += tensor.get(index);
            OK = idx.advance();
        }
        return sum;
    }

    /**
     * Add one tensor into another, element by element
     * @param intoTensor the tensor that will contain the sum of the elements
     * @param tensor2 the tensor being added in
     * @throws ArrayIndexOutOfBoundsException if the two tensors do not have the same shape
     */
    public static void add(Tensor<Double> intoTensor, Tensor<Double> tensor2) {
        Index idx = new Index(intoTensor.getShape());
        boolean OK = true;
        while (OK) {
            int[] index = idx.getIndex();
            intoTensor.assign((Double)intoTensor.get(index) + tensor2.get(index), index);
            OK = idx.advance();
        }
    }

    /**
     * Multiply one tensor into another, element by element
     * @param intoTensor the tensor that will contain the products of the elements
     * @param tensor2 the tensor being multiplied in
     * @throws ArrayIndexOutOfBoundsException if the two tensors do not have the same shape
     */
    public static void multiply(Tensor<Double> intoTensor, Tensor<Double> tensor2) {
        Index idx = new Index(intoTensor.getShape());
        boolean OK = true;
        while (OK) {
            int[] index = idx.getIndex();
            intoTensor.assign((Double)intoTensor.get(index) * tensor2.get(index), index);
            OK = idx.advance();
        }
    }


    /**
     * Compute the product of all elements in a tensor.
     * @param tensor a numeric tensor
     * @return the product
     */
    public static double product(Tensor<Double> tensor) {
        Index idx = new Index(tensor.getShape());
        boolean OK = true;
        double prod = 1.0;
        while (OK) {
            int[] index = idx.getIndex();
            prod *= tensor.get(index);
            OK = idx.advance();
        }
        return prod;
    }

    /**
     * Multiple each element in the tensor by a scaling factor.
     * @param tensor the tensor to be modified
     * @param d the amount to multiple each element by
     */
    public static void scale(Tensor<Double> tensor, double d) {
        Index idx = new Index(tensor.getShape());
        boolean OK = true;
        while (OK) {
            int[] index = idx.getIndex();
            tensor.assign(tensor.get(index) * d, index);
            OK = idx.advance();
        }
    }

    /**
     * Compute the dot product of two tensors. I.e., Get the sum of the
     * products of each pair of corresponding elements in the two tensors.
     * @param tensor1
     * @param tensor2
     * @return the dot-product
     * @throws ArrayIndexOutOfBoundsException if the two tensors do not have the same shape
     */
    public static double dotProduct(Tensor<Double> tensor1, Tensor<Double> tensor2) {
        Index idx = new Index(tensor1.getShape());
        boolean OK = true;
        double sum = 0.0;
        while (OK) {
            int[] index = idx.getIndex();
            sum += tensor1.get(index) * tensor2.get(index);
            OK = idx.advance();
        }
        return sum;
    }

}
