class ArrayIndexEvaluationOrder {

    public int val;
    public int[] arr;

    public ArrayIndexEvaluationOrder init() {
        this.arr = new int[20];
        this.val = 0;

        return this;
    }

    public int[] fetchArrWithSideEffect() {
        val = 42;
        return arr;
    }

    public int fetchIndexWithSideEffect() {
        val = 21;
        return 1;
    }

    public void check() {
        fetchArrWithSideEffect()[fetchIndexWithSideEffect()] = 10;

        System.out.println(val);
        System.out.println(arr[1]);
    }

    public static void main(String[] args) {
        new ArrayIndexEvaluationOrder().init().check();
    }
}
