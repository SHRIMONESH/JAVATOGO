class Counter {
    int count;

    void increment() {
        count = count + 1;
    }

    int getCount() {
        return count;
    }

    public static void main() {
        Counter counter;
        counter = new Counter();
        counter.increment();
        counter.increment();
        counter.increment();
        int value;
        value = counter.getCount();
        System.out.println("Count:");
        System.out.println(value);
    }
}
