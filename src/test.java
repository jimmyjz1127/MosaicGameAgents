import java.util.ArrayList;
import java.util.StringJoiner;


public class test{
    public static void main(String[] args){
        String[] x = {"A", "B", "C"};

        StringJoiner joiner = new StringJoiner("&", "(" ,")");

        for (String elem : x){
            joiner.add(elem);
        }
        System.out.println(joiner.toString());
    }
}