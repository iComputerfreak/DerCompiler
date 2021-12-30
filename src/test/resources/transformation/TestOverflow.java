class TestOverflow {
  public static void main(String[] args) {
    int i = 2147483645;

    while(i > 0) {
      i = i+1;
      System.out.println(i);
    }

    int j = -2147483647;
    j = j-2;

    int k = 2147483647+1;

    System.out.println(i);
    System.out.println(j);
    System.out.println(k);

  }
}
