class IfElse {
    int max(int a, int b) {
        if (a > b) {
            return a;
        } else {
            return b;
        }
    }

    public static void main() {
        IfElse test;
        test = new IfElse();
        int result;
        result = test.max(10, 20);
        System.out.println("Max:");
        System.out.println(result);
    }
}
