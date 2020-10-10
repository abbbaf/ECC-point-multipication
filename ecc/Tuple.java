package ecc;

public class Tuple<V1, V2> {

    private V1 first;
    private V2 second;


    public Tuple(V1 first, V2 second) {
        this.first = first;
        this.second = second;
    }

 
    public V1 getFirst() {
        return first;
    }

 
    public V2 getSecond() {
        return second;
    }
}