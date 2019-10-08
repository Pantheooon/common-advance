package al.learn.sort;

/**
 * @author: Pantheon
 * @date: 2019/10/2 13:55
 * @doc 归并排序:时间复杂度o(nlogn),空间复杂度o(n),非原地算法,稳定性算法
 * 思路:对于一个这样的数组9,8,7,6,2,8,6,15,假设将数组分为两部分,并排好序,分别为6,7,8,9和2,6,8,15,
 * 只要将这两部分数据进行合并即可,合并的思路比较简单,就是申请一个tmp数组,两个指针,一个指向[i]左边开始节点,
 * 一个指向[j]右边开始节点,如果left[i]>right[j],则将tmp[k]=left[i],反之则tmp[k]=right[j],这个是归并算法中
 * 并的思路,然后就再来说下分的思路,对于这样一个无序的数组,如果进行分,然后合并排序?如果我们把数组进行左右拆分,进行合并排序
 * 在对左边和右边进行拆分进行归并,这种思想不就是递归吗?所以,左右递归然后合并,递归最开始执行的就是两两比较大小,然后合到大数组中
 * 两两比较完,开始更大量的比较,直至最左边到最右边两个大数组的合并,这个算法结束
 */
public class MergeSort {


    public static void mergeSort(int[] num) {
        mergeInternally(num, 0, num.length - 1);
    }

    private static void mergeInternally(int[] num, int p, int r) {
        if (p >= r) {
            return;
        }
        int q = (r + p) / 2;
        mergeInternally(num, p, q);
        mergeInternally(num, q + 1, r);
        merge(num, p, q, r);
    }

    public static void merge(int[] num, int p, int q, int r) {
        int i = p;
        int j = q + 1;
        int k = 0;
        int[] tmp = new int[r - p + 1];
        //将num的数据进行拆分,比较大小,填到tmp中
        for (; i <= q && j <= r; ) {
            if (num[i] < num[j]) {
                tmp[k++] = num[i++];
            } else {
                tmp[k++] = num[j++];
            }
        }
        //将剩余的数据回填
        int start = i;
        int end = q;
        if (j <= r) {
            start = j;
            end = r;
        }
        while (start <= end) {
            tmp[k] = num[start++];
        }
        //将数据填入到num中
        for (k = 0; k < tmp.length; k++) {
            num[p + k] = tmp[k];
        }
    }

}
