/**
 * Conditions being tested:
 * 1) the type of the expression being returned by a fucntion must match the expected type.
 */
class Factorial {
    public static void main(String[] a){
        System.out.println(1);
    }
}

class Fac {

    public boolean ComputeFac(int num){
        return this.ComputeFac(num - 1) + 3;
    }
}