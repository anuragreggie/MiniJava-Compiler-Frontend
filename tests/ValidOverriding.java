/**
 *  Conditions being tested:
 *  1) Overriden methods must match n overriding method matches the ancestor's method signature with the same name
 *  (same number of arguments, same static type of arguments, the same return type).
 *  2) Overriding should work with mult level inheritance.
 */
class Factorial{
    public static void main(String[] a){
        System.out.println(1);
    }
}

class Animal {
    int a;
    public int sound() {
        a = 1;
        return a;
    }
}

class Cow extends Animal {
    public int sound() {
        return 1;
    }

    public boolean moo(boolean doesMoo) {
        return doesMoo;
    }
}

class BabyCow extends Cow {

}

class Fac {
    int[] mine;

    public int ComputeFac(int num, Z z) {
        int a;
        boolean moo;
        a = new BabyCow().sound();
        moo = new BabyCow().moo(false);
        return 1;
    }

}