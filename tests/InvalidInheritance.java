/**
 * Conditions being tested:
 * 1) cannot redeclare an identifier from the parent class if it's a member variable.
 * 2) can redeclare an identifier defined in the parent class if it was defined within a method scope.
 */
class Test {
    public static void main(String[] a){
        System.out.println(new Animal().eat());
    }
}

class Animal {
    int weight;
    int x;
    int y;

    public int eat() { int blah; return 0; }
}

class Dog extends Animal {
    boolean weight;
    int x;

    public int eat() { int y; int blah; return 0; }
}