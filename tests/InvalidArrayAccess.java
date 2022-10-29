/**
 * Conditions being tested:
 * 1) arrays are initialised with the size specified to an int.
 * 2) arrays are assigned before use.
 * 3) arrays must be indexed into using ints.
 * 4) the type returned when accessing an array element is an int.
 */
class Test {
    public static void main(String[] a){
        System.out.println(1);
    }
}

class ArrayTest {
    public boolean access1() {
        int[] a;
        int[] b;
        a = new int[false];
        a = new int[2 + 3];
        a[0] = b[1];
        b = new int[1];
        a[0] = b[2 < 3];
        return a[0];
    }
}