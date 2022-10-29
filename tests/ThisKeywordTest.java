/**
 * Conditions being tested:
 * 1) The keyword "this" returns the current class.
 * 2) Method invocations can be made on "this" only if the method exists within the current class and the
 *    arguments passed in are valid.
 */

class Factorial{
    public static void main(String[] a){
        System.out.println(new Fac().ComputeFac(1));
    }
}

class Fac {
    int[] mine;

    public int ComputeFac(int num) {
        int selfRes;
        int c;
        c = f[false];
        c = this;
        selfRes = this.methodThatDoesNotExist(num);
        self = this.ComputeFac(false, false, false);
        self = this.ComputeFac(2, false);
        return this.ComputeFac(num - 1);
    }

}