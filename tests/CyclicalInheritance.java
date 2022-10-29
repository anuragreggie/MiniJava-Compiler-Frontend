/**
 * Conditions tested:
 * 1) cyclical inheritance is not allowed.
 */

//   A -> B -> C -> D
//   |              |
//   |<-<-<-<-<-<-<-|

class Test{
    public static void main(String[] a){
        System.out.println(1);
    }
}

class A extends B  {

}

class B extends C {

}

class C extends D {

}

class D extends A {

}