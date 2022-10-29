/**
 * Conditions being tested:
 * 1) Nested method calls can be made.
 * 2) Method invocations can made on objects declared with the new keyword.
 * 3) Method invocations can be chained together.
 */
class Factorial{
    public static void main(String[] a){
        System.out.println(new Calculator().ComputeFac(new N().n(1)));
    }
}

class N {
    public int n(int s) {
        return new Factory().getCalculator().ComputeFac(1);
    }
}

class Calculator {
    public int ComputeFac(int num){
        return 1;
    }

}

class Factory {
    public Calculator getCalculator() {
        return new Calculator();
    }
}