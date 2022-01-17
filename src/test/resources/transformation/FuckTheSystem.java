class FuckTheSystem {
    public static void main(String[] args) {
        System System  = new System();
        System.init();
        System.out.println(2);
    }
}

class System {
    public System out;
    public void init() { out = new System(); }
    public void println(int i) {}
}

