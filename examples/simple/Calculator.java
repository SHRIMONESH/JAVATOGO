class Calculator {
    int add(int a, int b) {
        return a + b;
    }

    int multiply(int a, int b) {
        return a * b;
    }

    public static void main() {
        Calculator calc;
        calc = new Calculator();
        int sum;
        sum = calc.add(5, 3);
        int product;
        product = calc.multiply(4, 6);
        System.out.println("Sum:");
        System.out.println(sum);
        System.out.println("Product:");
        System.out.println(product);
    }
}
