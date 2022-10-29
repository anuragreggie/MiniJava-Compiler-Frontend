/**
 * Conditions being tested:
 *  1) the expression inside an if statement must evaluate to a boolean.
 *  2) the expression inside a while statement must evaluate to a boolean.
 */
class Factorial{
    public static void main(String[] a){
        System.out.println(new Fac().ComputeFac(10));
    }
}

class Fac {
    public int ComputeFac(int num){
        int num_aux ;
        if (3 * 1)
            num_aux = 1 ;
        else
            num_aux = num * (this.ComputeFac(num-1)) ;
        while(2 + 5)
            num_aux = 3;
        return 1;
    }

}