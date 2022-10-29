/**
 * Conditions being tested:
 * 1) If the object being returned from a method is a subtype of the expected class then the method declaration is valid.
 * 2) If the object being passed in as argument for a method is a subtype of the expected type then the method declaration is valid.
 */
class Factorial{
    public static void main(String[] a){
        // checks subtyping for method argument as BabyCow is passed into blah rather than Animal.
        System.out.println(new World().blah(new BabyCow()));
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
        a = 1;
        return a;
    }

    public boolean moo(boolean doesMoo) {
        return doesMoo;
    }
}

class BabyCow extends Cow {
    public int sound(int b) {
        int c;
        c = 1;
        a = 1;
        c = a + 3;
        return c;
    }

    public boolean moo(boolean doesMoo) {
        return false;
    }
}

class World {
    // check subtyping for return types
    public Animal makeAnimal() {
        return new BabyCow();
    }

    // checks subtyping for method arguments
    public int blah(Animal animal) {
        return 0;
    }
}