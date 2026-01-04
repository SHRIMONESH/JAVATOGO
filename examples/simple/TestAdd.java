class TestAdd {
    int x;

    int add(int a, int b) {
        return a + b;
    }

    public static void main() {
        TestAdd test;
        test = new TestAdd();
        int result;
        result = test.add(5, 3);
    }
}
