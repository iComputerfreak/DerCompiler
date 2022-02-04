class ArrayInc {


    public int[] arr;

    public static void main(String[] args) {
        ArrayInc ai = new ArrayInc();
        ai.arr = new int[5];
        int i = 3;
        ai.arr[i] = ai.arr[i]+1;
        System.out.println(ai.arr[i]);

    }

}





