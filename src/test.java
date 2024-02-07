import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringJoiner;


public class test{
    public static void main(String[] args){

        ArrayList<int[]> x = new ArrayList<int[]>();
        x.add(new int[]{1,2});
        x.add(new int[]{2,2});
        x.add(new int[]{1,0});
        x.add(new int[]{1,2});
        

        System.out.println(x.size());

        HashSet<int[]> y = new HashSet<int[]>(x);
        System.out.println(y.size());
    }
}