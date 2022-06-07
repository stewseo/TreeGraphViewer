package PriorityMinMaxHeap;

import java.util.Collections;

public class PriorityQueue {
    public static void main(String args) {
        addNum(55);
        addNum(77);
        addNum(88);
        addNum(44);
        addNum(9);

    }

    private static java.util.PriorityQueue<Integer> small = new java.util.PriorityQueue<Integer>(Collections.reverseOrder());
    private static java.util.PriorityQueue<Integer> large = new java.util.PriorityQueue<Integer>();
    private static boolean even = true;

    public static double findMedian() {
        if (even)
            return (small.peek() + large.peek()) / 2.0;
        else
            return small.peek();
    }

    public static void addNum(int num) {
        if (even) {
            large.offer(num);
            small.offer(large.poll());
        } else {
            small.offer(num);
            large.offer(small.poll());
        }
        even = !even;
    }

}
