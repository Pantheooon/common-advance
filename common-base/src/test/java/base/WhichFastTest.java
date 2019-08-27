package base;

import org.junit.Test;

public class WhichFastTest {



    private long[][] a= new long[10000][10000];


    @Test
    public void test_one() {
        long start = System.currentTimeMillis();
        for (int i = 0;i<a.length;i++){
            for (int j = 0;j<a[i].length;j++){
                a[i][j]=j;
            }
        }
        System.out.println(System.currentTimeMillis()-start);
    }


    @Test
    public void test_two() {
        long start = System.currentTimeMillis();
        for (int i = 0;i<a.length;i++){
            for (int j = 0;j<a[i].length;j++){
                a[j][i]=j;
            }
        }
        System.out.println(System.currentTimeMillis()-start);
    }
}
