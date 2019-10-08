package al.learn.sort;

/**
 * @author: Pantheon
 * @date: 2019/9/30 11:04
 * @comment: 排序算法
 */
public class Sort {

    /**
     * 冒泡
     *
     * @param num
     * @doc 空间复杂度o(1), 时间复杂度, 最好的情况下是第一次比较没有发现交换的现象此为o(n),最差情况o(n2)
     * 稳定排序,只有一个比另外一个大才交换,如果相等不会交换位置
     */
    public static void bubbleSort(int[] num) {
        if (num == null || num.length <= 1) {
            return;
        }
        for (int j = 0; j < num.length; j++) {
            boolean exchangeFlag = false;
            for (int i = 0; i < num.length - j - 1; i++) {
                if (num[i] > num[i + 1]) {
                    int temp = num[i];
                    num[i] = num[i + 1];
                    num[i + 1] = temp;
                    exchangeFlag = true;
                }
            }
            //无交换退出
            if (!exchangeFlag) {
                break;
            }
        }
    }

    /**
     * 插入排序,数据交换次数较少,效率比冒泡要高
     * 思路:如果一个数组为4,2,3,1,8,6,将数据分为两部分,一部分为已排序一部分为未排序
     * 最初的已排序的数据可以认为为4,从索引为1开始遍历,2这个数字比4小,把索引为1的数字变为4,
     * 把2插到4的位置,此时为2,4,3,1,8,6,然后遍历索引为2的数字,3比4小,比2大,把4位置的数字变为3,
     * 把4放在3的位置,此时为2,3,4,1,8,6,接着遍历索引为3的数字,1比4小,把1的数字变为4,此时为2,3,4,4,8,6
     * 接着判断1比3小,3放在4的位置此时为2,3,3,4,8,6接着比较2,数字变成1,2,3,4,8,6接着继续判断
     * 也就是说如果未排序的索引为j,判断j->0的情况,如果存在某个数据比j所在的数据大,则往后挪一个,并把j的数据插在
     * 比他小且比他大的中间
     *
     * @param a
     * @doc 最好o(n), 最长o(n2),稳定排序
     */
    public static void insertionSort(int[] a) {
        if (a.length <= 1) {
            return;
        }
        for (int i = 1; i < a.length; ++i) {
            int value = a[i];
            int j = i - 1;
            for (; j >= 0; j--) {
                if (a[j] > value) {
                    a[j + 1] = a[j];
                } else {
                    break;
                }
            }
            a[j + 1] = value;
        }
    }

    /**
     * 选择排序,时间复杂度,o(n2),最坏o(n2),非稳定排序
     * 思路:若有数组4,5,6,1,2,3,首先假设索引为0的数据最小,然后找出比索引为0最小的数据,此时为1,两两交换位置
     * 此时数据为1,5,6,4,2,3,接着遍历索引为2的数据,找出比5最小的数据为2,交换位置,1,2,6,4,5,3,此时可以看出
     * 其实选择排序也是将数据分为了两部分,左边是排好序的,右边是未排好序的,记着遍历6,发现3比他最小,此时为1,2,3,4,5,6
     *
     * @param a
     */
    public static void selectionSort(int[] a) {
        if (a.length <= 1) {
            return;
        }
        for (int i = 0; i < a.length; i++) {
            int minIndex = i;
            for (int j = i + 1; j < a.length; j++) {
                if (a[j] < a[minIndex]) {
                    minIndex = j;
                }
            }
            int temp = a[i];
            a[i] = a[minIndex];
            a[minIndex] = temp;
        }
    }

    /**
     * //todo
     *
     * @param arr
     * @doc 希尔排序
     */
    public static void shellSort(int[] arr) {
        int len = arr.length;
        if (len == 1) {
            return;
        }

        int step = len / 2;
        while (step >= 1) {
            for (int i = step; i < len; i++) {
                int value = arr[i];
                int j = i - step;
                for (; j >= 0; j -= step) {
                    if (value < arr[j]) {
                        arr[j + step] = arr[j];
                    } else {
                        break;
                    }
                }
                arr[j + step] = value;
            }

            step = step / 2;
        }
    }

    /**
     * 桶排序
     *
     * @param num
     */
    public static void bucketSort(int[] num) {

    }

    /**
     * 计数排序
     *
     * @param num
     */
    public static void countSort(int[] num) {

    }

    /**
     * 基数排序
     * @param num
     */
    public static void radixSort(int[] num) {

    }

}
