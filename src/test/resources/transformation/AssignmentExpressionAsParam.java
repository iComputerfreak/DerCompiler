class Main
{
    public static void main(String[] args) {
        int[] var = new int[5];
        int i = -1;
        while ( i < 4) {
            System.out.println(new Main().arrayAccess(var, i = i + 1));
        }
    }

    public int arrayAccess(int[] arr, int i) {
        return arr[i];
    }
}
