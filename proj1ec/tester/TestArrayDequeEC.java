package tester;
import static org.junit.Assert.*;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

public class TestArrayDequeEC {
    private final int TEST = 500000;
    @Test
    public void testRemoveFirst() {
        StudentArrayDeque<Integer> s = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> a = new ArrayDequeSolution<>();
        for (int i = 0; i < TEST; i += 1) {
            s.addFirst(i);
            a.addFirst(i);
        }
        for (int i = 0; i < TEST; i += 1) {
            assertEquals(s.removeFirst(), a.removeFirst());
        }
    }
    @Test
    public void testRemoveLast() {
        StudentArrayDeque<Integer> s = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> a = new ArrayDequeSolution<>();
        for (int i = 0; i < TEST; i += 1) {
            s.addLast(i);
            a.addLast(i);
        }
        for (int i = 0; i < TEST; i += 1) {
            assertEquals("addFirst(5) \n"
                    + "addFirst(3)\n"
                    + "removeFirst()", s.removeLast(), a.removeLast());
        }
    }
    @Test
    public void testAddFirst() {
        StudentArrayDeque<Integer> s = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> a = new ArrayDequeSolution<>();
        for (int i = 0; i < TEST; i += 1) {
            s.addFirst(i);
            a.addFirst(i);
            assertEquals(a.get(0), s.get(0));
        }
    }
    @Test
    public void testAddLast() {
        StudentArrayDeque<Integer> s = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> a = new ArrayDequeSolution<>();
        for (int i = 0; i < TEST; i += 1) {
            s.addLast(i);
            a.addLast(i);
            assertEquals(a.get(i), s.get(i));
        }
    }
    @Test
    public void test() {
        StudentArrayDeque<Integer> sad1 = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> sad2 = new ArrayDequeSolution<>();
        String errString = new String();

        for (int i = 0; i < 10; i += 1) {
            double numberBetweenZeroAndOne = StdRandom.uniform();

            if (numberBetweenZeroAndOne < 0.5) {
                sad1.addLast(i);
                sad2.addLast(i);
                errString += ("addLast(" + i + ")\n");
            } else {
                sad1.addFirst(i);
                sad2.addFirst(i);
                errString += ("addFirst(" + i + ")\n");
            }
        }

        // sad1.printDeque();
        // System.out.println(sad2.toString());
        errString += ("size()\n");
        assertEquals(errString, sad2.size(), sad1.size());

        for (int i = 0; i < 10; i += 1) {
            double numberBetweenZeroAndOne = StdRandom.uniform();

            if (numberBetweenZeroAndOne < 0.5) {
                Integer item1 = sad1.removeLast();
                Integer item2 = sad2.removeLast();
                errString += ("removeLast()\n");
                assertEquals(errString, item2, item1);
            } else {
                Integer item1 = sad1.removeFirst();
                Integer item2 = sad2.removeFirst();
                errString += ("removeFirst()\n");
                assertEquals(errString, item2, item1);
            }
            errString += ("size()\n");
            assertEquals(errString, sad2.size(), sad1.size());
        }
    }
}

