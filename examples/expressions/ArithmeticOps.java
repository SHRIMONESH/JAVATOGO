class ArithmeticOps {
    int calculate(int x, int y) {
        int sum;
        sum = x + y;
        int product;
        product = x * y;
        int result;
        result = sum + product;
        return result;
    }

    public static void main() {
        ArithmeticOps ops;
        ops = new ArithmeticOps();
        int result;
        result = ops.calculate(3, 4);
        System.out.println(result);
    }
}
