package tester;
import static org.junit.Assert.*;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

public class TestArrayDequeEC {
    private final int TEST_NUMBER = 1000;

    @Test
    public void RemovalFirstTest() {
        StudentArrayDeque<Integer> s = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> a = new ArrayDequeSolution<>();
        for (int i = 0; i < TEST_NUMBER; i++) {
            s.addFirst(i);
            a.addFirst(i);
        }
        for (int i = 0; i < TEST_NUMBER; i++) {
            assertEquals("The value should be equal", s.removeFirst(), a.removeFirst());
        }
    }

    @Test
    public void removeLastTest() {
        StudentArrayDeque<Integer> s = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> a = new ArrayDequeSolution<>();
        for (int i = 0; i < TEST_NUMBER; i++) {
            s.addLast(i);
            a.addLast(i);
        }
        for (int i = 0; i < TEST_NUMBER; i++) {
            assertEquals("addFirst(5) \n"
                    + "addFirst(3)\n"
                    + "removeFirst()", s.removeLast(), a.removeLast());
        }
    }

    @Test
    public void addFirstTest() {
        StudentArrayDeque<Integer> s = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> a = new ArrayDequeSolution<>();
        for (int i = 0; i < TEST_NUMBER; i += 1) {
            s.addFirst(i);
            a.addFirst(i);
            assertEquals("The value should be equal", a.get(0), s.get(0));
        }
    }

    @Test
    public void addLastTest() {
        StudentArrayDeque<Integer> s = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> a = new ArrayDequeSolution<>();
        for (int i = 0; i < TEST_NUMBER; i++) {
            s.addLast(i);
            a.addLast(i);
            assertEquals("The value should be equal", a.get(i), s.get(i));
        }
    }

    @Test
    public void randomTest() {
        StudentArrayDeque<Integer> s = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> a = new ArrayDequeSolution<>();
        String errString = new String();

        for (int i = 0; i < TEST_NUMBER; i++) {
            double randomNumber = StdRandom.uniform();

            if (randomNumber > 0.5) {
                s.addLast(i);
                a.addLast(i);
                errString += ("addLast(" + i + ")\n");
            } else {
                s.addFirst(i);
                a.addFirst(i);
                errString += ("addFirst(" + i + ")\n");
            }
        }
        errString += ("size()\n");
        assertEquals(errString, s.size(), a.size());

        for (int i = 0; i < TEST_NUMBER; i++) {
            double numberBetweenZeroAndOne = StdRandom.uniform();

            if (numberBetweenZeroAndOne > 0.5) {
                Integer itemS = s.removeLast();
                Integer itemA = a.removeLast();
                errString += ("removeLast()\n");
                assertEquals(errString, itemS, itemA);
            } else {
                Integer itemS = s.removeFirst();
                Integer itemA = a.removeFirst();
                errString += ("removeFirst()\n");
                assertEquals(errString, itemS, itemA);
            }
            errString += ("size()\n");
            assertEquals(errString, s.size(), a.size());
        }
    }
}


