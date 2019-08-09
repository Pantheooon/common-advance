package aha;

import org.junit.Test;

public class SortTest {


    @Test
    public void simpleBucket() {
        String num = "7,3,1,19,23,3,4,33";
        String[] split = num.split(",");
        int[] bucket = new int[34];
        for (String index : split) {
            Integer of = Integer.valueOf(index);
            bucket[of]++;
        }

        String after = "";

        for (int i = 0; i < bucket.length; i++) {
            if (bucket[i] != 0) {
                for (int j = 0; j < bucket[i]; j++) {
                    after += ",";
                    after += i;
                }

            }
        }
        System.out.println(after);
    }

    @Test
    public void maopao(){
        int[] num = new int[]{99,35,18,76,12};
        for (int i=0;i<num.length;i++){
            for (int j = 0;j<num.length-1;j++){
                if (num[j]>num[j+1]){
                    int swap = num[j];
                    num[j] = num[j+1];
                    num[j+1] =swap;
                }
            }
        }
        for (int i : num) {
            System.out.println(i);
        }
    }


    @Test
    public void quickSort(){
        a=new int[]{99,35,18,76,12};
        quickSort(0,a.length-1);
        for (int i : a) {
            System.out.println(i);
        }
    }
    int[] a;

    void quickSort(int left,int right){
        int i,j,t,temp;
        if (left>right){
            return;
        }
        //temp为基准数据
        temp=a[left];
        i=left;
        j = right;
        while (i!=j){
            while(a[j]>=temp&&i<j)
                j--;
            while (a[i]<=temp&&i<j)
                i++;
            if (i<j){
                t=a[i];
                a[i]=a[j];
                a[j]=t;
            }
        }
        a[left]=a[i];
        a[i]=temp;
        quickSort(left,i-1);
        quickSort(i+1,right);

    }

}
