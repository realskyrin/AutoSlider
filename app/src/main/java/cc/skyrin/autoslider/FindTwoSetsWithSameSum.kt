package cc.skyrin.autoslider

import java.util.ArrayList
import java.util.Arrays
import java.util.Stack

class FindTwoSetsWithSameSum {

    /**
     * 当前Stack中所有数据的和
     */
    private var sumInStack = 0

    private val stack = Stack<Int>()

    private val uniqueResult = ArrayList<String>()

    internal var count = 0

    /**
     * 数据从大到小排列
     */
    private fun sortByDes(data: IntArray) {
        Arrays.sort(data)
        val length = data.size
        for (i in 0 until length / 2) {
            val temp = data[i]
            data[i] = data[length - i - 1]
            data[length - i - 1] = temp
        }
    }

    fun populate(data: IntArray) {
        /**
         * 计算数据集的和，如果不是偶数，那么直接输出“结果不存在”。
         */
        val sum = sum(data)
        if (sum % 2 != 0) {
            println("结果不存在！")
            return
        }
        /**
         * 如果数据集的和为偶数，计算出平均数，为了减少递归的次数，
         * 将数据从大到小排列。
         */
        val average = sum / 2
        sortByDes(data)

        /**
         * 打印出数据集合的信息。
         * 如：
         * 源数据集合-->[15,14,13,11,10,9,8,7,6,5,4,3,2,1] 分成和相等的两堆数据，有如下几种情况!
         */
        printDataArray(data)

        populateTargetSets(average, data, 0, data.size)
    }

    private fun populateTargetSets(sum: Int, sourceData: IntArray, begin: Int,
                                   end: Int) {
        // 判断Stack中的数据和是否等于目标值，如果是则输出当前Stack中的数据
        if (sumInStack == sum) {
            if (!isDuplicatedResult(stack, sourceData)) {
                print(stack, sourceData)
            }
        }

        for (currentIndex in begin until end) {
            /*
             * 如果当前Stack中的和加上当前index的数据小于等于目标值， 那么将当前的index的数据添加到Stack中去，
             * 同时，将当前Stack中所有数据的和加上新添加到Stack中的数值
             */
            if (sumInStack + sourceData[currentIndex] <= sum) {
                stack.push(sourceData[currentIndex])
                sumInStack += sourceData[currentIndex]
                // 当前index加上1，递归调用本身
                populateTargetSets(sum, sourceData, currentIndex + 1, end)
                sumInStack -= stack.pop() as Int
            }

        }

    }

    private fun isDuplicatedResult(stack: Stack<Int>, sourceData: IntArray): Boolean {
        return uniqueResult.contains(stack.toString())
    }

    private fun print(stack: Stack<Int>, sourceData: IntArray) {
        printIndexInfor()
        printStack(stack)
        printRemainingData(stack, sourceData)
    }

    private fun printIndexInfor() {
        print("第")
        print(++count)
        print("种结果==> ")
    }

    private fun printRemainingData(stack: Stack<Int>, sourceData: IntArray) {
        val list = ArrayList<Int>()
        for (element in sourceData) {
            list.add(element)
        }

        for (element in stack) {
            list.remove(element)
        }
        print(" 和 ")
        println(list.toString())

        uniqueResult.add(stack.toString())
        uniqueResult.add(list.toString())
    }

    private fun printStack(stack: Stack<Int>) {
        print("")
        print(stack.toString())
    }

    private fun printDataArray(sourceData: IntArray) {
        val sb = StringBuilder()
        sb.append('[')
        for (element in sourceData) {
            sb.append(element)
            sb.append(',')
        }
        sb.setCharAt(sb.length - 1, ']')
        print("源数据集合-->")
        print(sb.toString())
        println(" 分成和相等的两堆数据，有如下几种情况!")
        println()
    }

    /**
     * 数据求和。
     */
    private fun sum(data: IntArray): Int {
        var sum = 0
        for (element in data) {
            sum += element
        }
        return sum
    }
}
