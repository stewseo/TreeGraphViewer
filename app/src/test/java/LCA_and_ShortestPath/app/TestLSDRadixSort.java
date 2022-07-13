package LCA_and_ShortestPath.app;

import LSDRadixSort.LSDRadixSort;
import org.gradle.internal.impldep.org.junit.runner.manipulation.Sorter;
import org.jgrapht.util.RadixSort;
import org.junit.jupiter.api.Test;

import static LSDRadixSort.LSDRadixSort.tryAllocate;

public class TestLSDRadixSort {

    Sorter sorter;
    int[] a;
    int low;
    int high;
    int[] b;
    int size;
    //Count the number of Digits (least significant group of bits) in each radix into an auxiliary
    //array
    @Test
    void correctOrderTest() {
        int offset = low;
        size = high - low;

            tryAllocate(a, size);
            
            int start = low - offset;
            int last = high - offset;

            //Set up buckets of exactly the right size.
            int[] count1 = new int[1024];
            int[] count2 = new int[2048];
            int[] count3 = new int[2048];

            for (int i = low; i < high; ++i) {
            // Counting least significant group of bits
                // Digit 1 in range (0, 2^10 − 1)
                count1[ a[i] & 0x3FF]--;
                // 2^10, 2^22 − 1
                count2[(a[i] >>> 10) & 0x7FF]--;
                // and 2^22, 2^32 − 1
                count3[(a[i] >>> 21) ^ 0x400]--; // Reverse the sign bit

            }
            /*
            //create spacing between each bucket for accurate mapping

             * Detect digits to be processed.
             */
            boolean processDigit1 = processDigit(count1, 1023, -size, high);
            boolean processDigit2 = processDigit(count2, 2047, -size, high);
            boolean processDigit3 = processDigit(count3, 2047, -size, high);
            /*
             * Process the 1-st digit.
             */
            if (processDigit1) {
                for (int i = low; i < high; ++i) {
                    b[count1[a[i] & 0x3FF]++ - offset] = a[i];
                }
            }

            //Sort the remaining range.
            // Unsigned right shift XOR 1024 will allow negative numbers to be sorted

            /*
             * Process the 2-nd digit.
             */
            if (processDigit2) {
                if (processDigit1) {
                    for (int i = start; i < last; ++i) {
                        a[count2[(b[i] >>> 10) & 0x7FF]++] = b[i];
                    }
                } else {
                    for (int i = low; i < high; ++i) {
                        b[count2[(a[i] >>> 10) & 0x7FF]++ - offset] = a[i];
                    }
                }
            }

            /*
             * Process the 3-rd digit.
             */
            if (processDigit3) {
                if (processDigit1 ^ processDigit2) {

                    for (int i = start; i < last; ++i) {
                        a[count3[(b[i] >>> 21) ^ 0x400]++] = b[i];
                    }
                } else {
                    for (int i = low; i < high; ++i) {

                        b[count3[(a[i] >>> 21) ^ 0x400]++ - offset] = a[i];
                    }
                }
            }

            /*
             * Copy the buffer to original array, if we process ood number of digits.
             */
            if (processDigit1 ^ processDigit2 ^ processDigit3) {
                System.arraycopy(b, low - offset, a, low, size);
            }
        }

        /**
         * Checks the count array and then creates histogram.
         *
         * @param count the count array
         * @param last the last index of count array
         * @param total 0
         * @param high the index of the last element, exclusive
         * @return {@code true} if the digit must be processed, otherwise {@code false}
         */

        private static int coun= 0;
        private static boolean processDigit(int[] count, int last, int total, int high) {
            /*
             * Check if we can skip given digit.
             */
            for (int c : count) {
                if (c == total) {
                    return false;
                }
                if (c < 0) {
                    break;
                }
            }

            /*
             * Compute the histogram.
             */
            count[last] += high;
            for (int i = last; i > 0; --i) {
                count[i - 1] += count[i];
            }
            return true;
        }
    }

