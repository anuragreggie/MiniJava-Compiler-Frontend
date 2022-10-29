/**
 * Conditions tested:
 * 1) if B is a subtype of A, then we can do variable of type A = variable of type B
 */
class Test {
    public static void main(String[] a) {
        System.out.println(1);
    }
}

class A {

    public int one() {
        int a;
        A obj;
        obj = new B();

        a = this.two(obj);

        return 1;
    }

    public int two(A param) {
        return 1;
    }


}

class B extends A {

}