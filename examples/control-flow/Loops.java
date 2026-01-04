class Loops {
    int factorial(int n) {
        int result;
        result = 1;
        int i;
        i = 1;
        while (i <= n) {
            result = result * i;
            i = i + 1;
        }
        return result;
    }

    public static void main() {
        Loops test;
        test = new Loops();
        int result;
        result = test.factorial(5);
        System.out.println("Factorial:");
        System.out.println(result);
    }
}
