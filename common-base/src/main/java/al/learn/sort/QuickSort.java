package al.learn.sort;

/**
 * @author: Pantheon
 * @date: 2019/10/2 15:23
 * @doc 快速排序是一个原地算法, 时间复杂度为o(nlogn), 最坏为o(n2), 非稳定性排序
 * 思路:假设有这样的一个数组a,6, 5, 9, 7, 11, 8,首先选择一个基准数据,方便起见,选择最右边的那个数据作为基准数据
 * 有两个指针,i,j分别从0和a.length-2的地方开始遍历数组,左边最先开始,如果a[i]>基准数字,则不动,等右边开始找到一个数字满足
 * a[j]小于基准数字,则交换二者的位置,然后左边先走,接着重复,直到i=j,此时把a[i]的数字和基准数字进行交换,第一次遍历后的数据为
 * 6,5,7,8,9,11,这个时候可以看到基准数字的作用就是,比基准数字小的都在左边,比基准数字大的都在右边,把整个数组一分为3,[0-2][2][3-5]
 * 接着递归遍历左边和右边
 * 思考问题:如果给定一个数组a,通过时间复杂度为o(n)的算法获取第k大的数据?
 * 整个问题可以通过快排来解决,第k大的数据其实就是找数组排好序后a[k]的数字,ok,首先选出基准数据,进行确定基准数据的位置,加入基准数据的
 * 索引恰好为k,遍历结束,如果基准数据索引大于k,则遍历基准数据左边的数据,如果小于k,则遍历基准数据右边的数据,所以时间复杂度为
 * o(n+n/2+n/4+....1) = o(2n-1) = o(n)
 */
public class QuickSort {
    public static void quickSort(int[] a) {
        quickSortInternally(a, 0, a.length - 1);
    }

    private static void quickSortInternally(int[] a, int p, int r) {
        if (p >= r) {
            return;
        }
        int partition = partition(a, p, r);
        quickSortInternally(a, p, partition - 1);
        quickSortInternally(a, partition + 1, r);

    }

    private static int partition(int[] a, int p, int r) {
        int pivotIndex = r;
        int pivot = a[pivotIndex];
        int i = p;
        int j = r - 1;
        for (; i <= j; ) {
            if (a[i] > pivot) {
                if (a[j] < pivot) {
                    swap(a, i, j);
                    i++;
                    j--;
                } else {
                    j--;
                }
            } else {
                i++;
            }
        }

        swap(a, i, r);
        return i;
    }

    private static void swap(int[] a, int i, int j) {
        int tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }


}
