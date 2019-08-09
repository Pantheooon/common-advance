package aha;

import org.junit.Test;

public class QueueTest {

    @Test
    public void test(){
        int[] q = new int[100];
        q[0]=6;
        q[1]=3;
        q[2]=1;
        q[3]=7;
        q[4]=5;
        q[5]=8;
        q[6]=9;
        q[7]=2;
        q[8]=4;
        int index = 9;
      for (int i = 0;i<index;i++){
          if (i%2==0){
              System.out.println(q[i]);
          }else {
              q[index]=q[i];
              index++;
          }
      }
    }
}
