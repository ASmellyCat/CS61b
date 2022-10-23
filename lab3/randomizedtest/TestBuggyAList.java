package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> correct = new AListNoResizing<>();
        BuggyAList<Integer> broken = new BuggyAList<>();

        int num_add = 3;
        for (int i = 4; i < 4 + num_add; i++) {
            correct.addLast(i);
            broken.addLast(i);
        }
        for (int j = 0; j < num_add; j++) {
            assertEquals(correct.removeLast(), broken.removeLast());
        }

    }
    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> correct = new AListNoResizing<>();
        BuggyAList<Integer> broken = new BuggyAList<>();

        int N = 500;
        for (int i = 0; i < N; i++) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                int randVal = StdRandom.uniform(0, 100);
                correct.addLast(randVal);
                broken.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                if (correct.size() > 0 && broken.size() > 0) {
                    int corr_ret = correct.removeLast();
                    int brok_ret = broken.removeLast();
                    System.out.println("corr_removeLast(" + corr_ret + ")" + " brok_removeLast(" + brok_ret + ")");
                    assertEquals(corr_ret, brok_ret);
                }
            } else if (operationNumber == 2) {
                // size
                int corr_size = correct.size();
                int brok_size = broken.size();
                System.out.println("corr_size: " + corr_size + " brok_size: " + brok_size);
                assertEquals(corr_size, brok_size);
            } else {
                if (correct.size() > 0 && broken.size() > 0) {
                    int corr_last = correct.getLast();
                    int brok_last = broken.getLast();
                    System.out.println("corr_last: " + corr_last + " brok_last: " + brok_last);
                    assertEquals(corr_last, brok_last);
                }
            }
        }
    }
}
