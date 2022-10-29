/**
 * Conditions being tested:
 * 1) variables must be initialised before use.
 * 2) subclasses can access variables from a superclass.
 * 2) subclasses must intitialise variables from a superclass before they are used.
 */
class Factorial{
    public static void main(String[] a){
        System.out.println(1);
    }
}

class Animal {
    int a;
    public int sound(int az) {
        a = 1;
        return a;
    }
}

class Cow extends Animal {
    public int sound(int az) {
        return a;
    }

    public boolean moo(boolean doesMoo) {
        return doesMoo;
    }
}

class BabyCow extends Cow {
    public int sound(int b) {
        int c;
        c = a + 3;
        return c;
    }

    public boolean moo(boolean doesMoo) {
        return false;
    }
}