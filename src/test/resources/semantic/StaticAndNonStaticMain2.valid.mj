/* OK */
/* According to the language spec, `args` from the main may not
  be used in the body of the main method, but strictly speaking
  that does not exclude (valid) usage elsewhere. */
class foo {
    public void main(String[] args) {

    }
}

class main {
    public void foo(String[] args) {
        foo foo = new foo();
        foo.main(args);
    }

    public static void main(String[] args) {
        main main = new main();
        main.foo(null);
    }
}
