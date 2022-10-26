package deque;

import org.junit.Test;
import java.util.Comparator;
import static org.junit.Assert.*;

public class MaxArrayDequeTest{
    private int TEST_NUMER = 100;

    @Test
    public void intCompareTest() {
        MaxArrayDeque<Integer> ad1 = new MaxArrayDeque<>(new IntegerComparator());
        for (int i = 0; i < TEST_NUMER; i++) {
            ad1.addLast(i);
        }
        assertEquals((double) TEST_NUMER - 1, (double) ad1.max(), 0.0);
    }

    @Test
    public void strCompareTest() {
        MaxArrayDeque<String> ad1 = new MaxArrayDeque<>(new StringComparator());
        ad1.addFirst("Tomorrow is another day.");
        ad1.addLast("you are not alone.");
        assertEquals("you are not alone.", ad1.max());
        assertEquals("Tomorrow is another day.", ad1.max(new StringlengthComparator()));
    }

    private static class IntegerComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer i1, Integer i2) {
            return i1 - i2;
            }
    }

    private static class StringComparator implements Comparator<String> {
        @Override
        public int compare(String s1, String s2) {
            return s1.compareTo(s2);

        }
    }

    private static class StringlengthComparator implements Comparator<String> {
        @Override
        public int compare(String s1, String s2) {
            return s1.length() - s2.length();
        }
    }


}

