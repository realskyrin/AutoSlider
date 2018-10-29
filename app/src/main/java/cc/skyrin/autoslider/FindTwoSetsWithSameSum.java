package cc.skyrin.autoslider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class FindTwoSetsWithSameSum {

    /**
     * 当前Stack中所有数据的和
     */
    private int sumInStack = 0;

    private Stack<Integer> stack = new Stack<Integer>();

    /**
     * 数据从大到小排列
     */
    private static void sortByDes(int[] data) {
        Arrays.sort(data);
        int length = data.length;
        for (int i = 0; i < length / 2; i++) {
            int temp = data[i];
            data[i] = data[length - i - 1];
            data[length - i - 1] = temp;
        }
    }

    private List<String> uniqueResult = new ArrayList<String>();

    int count = 0;

    public void populate(int[] data) {
        /**
         * 计算数据集的和，如果不是偶数，那么直接输出“结果不存在”。
         */
        int sum = sum(data);
        if (sum % 2 != 0) {
            System.out.println("结果不存在！");
            return;
        }
        /**
         * 如果数据集的和为偶数，计算出平均数，为了减少递归的次数，
         * 将数据从大到小排列。
         */
        int average = sum / 2;
        sortByDes(data);

        /**
         * 打印出数据集合的信息。
         * 如：
         * 源数据集合-->[15,14,13,11,10,9,8,7,6,5,4,3,2,1] 分成和相等的两堆数据，有如下几种情况!
         */
        printDataArray(data);

        populateTargetSets(average, data, 0, data.length);
    }

    private void populateTargetSets(int sum, int[] sourceData, int begin,
                                    int end) {
        // 判断Stack中的数据和是否等于目标值，如果是则输出当前Stack中的数据
        if (sumInStack == sum) {
            if (!isDuplicatedResult(stack, sourceData)) {
                print(stack, sourceData);
            }
        }

        for (int currentIndex = begin; currentIndex < end; currentIndex++) {
            /*
             * 如果当前Stack中的和加上当前index的数据小于等于目标值， 那么将当前的index的数据添加到Stack中去，
             * 同时，将当前Stack中所有数据的和加上新添加到Stack中的数值
             */
            if (sumInStack + sourceData[currentIndex] <= sum) {
                stack.push(sourceData[currentIndex]);
                sumInStack += sourceData[currentIndex];
                // 当前index加上1，递归调用本身
                populateTargetSets(sum, sourceData, currentIndex + 1, end);
                sumInStack -= (Integer) stack.pop();
            }

        }

    }

    private boolean isDuplicatedResult(Stack<Integer> stack, int[] sourceData) {
        return uniqueResult.contains(stack.toString());
    }

    private void print(Stack<Integer> stack, int[] sourceData) {
        printIndexInfor();
        printStack(stack);
        printRemainingData(stack, sourceData);
    }

    private void printIndexInfor() {
        System.out.print("第");
        System.out.print(++count);
        System.out.print("种结果==> ");
    }

    private void printRemainingData(Stack<Integer> stack, int[] sourceData) {
        List<Integer> list = new ArrayList<Integer>();
        for (int element : sourceData) {
            list.add(element);
        }

        for (int element : stack) {
            list.remove(new Integer(element));
        }
        System.out.print(" 和 ");
        System.out.println(list.toString());

        uniqueResult.add(stack.toString());
        uniqueResult.add(list.toString());
    }

    private void printStack(Stack<Integer> stack) {
        System.out.print("");
        System.out.print(stack.toString());
    }

    private void printDataArray(int[] sourceData) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int element : sourceData) {
            sb.append(element);
            sb.append(',');
        }
        sb.setCharAt(sb.length() - 1, ']');
        System.out.print("源数据集合-->");
        System.out.print(sb.toString());
        System.out.println(" 分成和相等的两堆数据，有如下几种情况!");
        System.out.println();
    }

    /**
     * 数据求和。
     */
    private int sum(int[] data) {
        int sum = 0;
        for (int element : data) {
            sum += element;
        }
        return sum;
    }
}
