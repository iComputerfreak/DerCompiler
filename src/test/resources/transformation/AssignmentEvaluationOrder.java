class AssignmentEvaluationOrder {

    public int x;
    public int[] arr;

    public AssignmentEvaluationOrder init() {
        arr = new int[20];
        x = 10;

        return this;
    }

    public void check() {
        arr[x = 5] = x;

        System.out.println(arr[5]);
    }

    public static void main(String[] args) {
        new AssignmentEvaluationOrder().init().check();
    }
}

