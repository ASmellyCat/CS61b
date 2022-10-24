package deque;

public class ArrayDeque<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;
    private static int RFACTOR = 2;

    /** Creates an empty list. */
    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 4;
        nextLast = 5;
    }
    private void resize(int capacity) {
        T[] a = (T[]) new Object[capacity];
        int ind = 0;
        for (int i = 0; i < size; i+= 1) {
            ind = arrayInd(i);
            a[capacity / 4 + i] = items[ind];
        }
        items = a;
        nextFirst = capacity / 4 - 1;
        nextLast = nextFirst + size + 1;
    }

    private int arrayInd(int ind) {
        if (ind + nextFirst + 1 >= items.length) {
            return ind + nextFirst + 1  - items.length;
        } else {
            return ind + nextFirst + 1;
        }
    }

    public void addFirst(T item) {
        if (size == items.length - 2) {
            resize(RFACTOR * items.length);
        }
        items[nextFirst] = item;
        if (nextFirst == 0) {
            nextFirst = items.length - 1;
        } else {
            nextFirst -= 1;
        }
        size += 1;
    }

    public void addLast(T item) {
        if (size == items.length - 2) {
            resize(RFACTOR * items.length);
        }
        items[nextLast] = item;
        if (nextLast == items.length - 1) {
            nextLast = 0;
        } else {
            nextLast += 1;
        }
        size += 1;
    }

    /** Whether the list is empty. */
    public boolean isEmpty() {
        return (size == 0) ? (true) : (false);
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        for (int i = 0; i < size; i+= 1) {
            int ind = arrayInd(i);
            System.out.print(items[ind] + " ");
        }
        System.out.println();
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        if (size < items.length/4 && size > 8) {
            resize(items.length / RFACTOR);
        }
        T item = getFirst();
        int ind = arrayInd(0);
        items[ind] = null;
        size = size - 1;
        nextFirst = ind;
        return item;
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        if (size < items.length/4 && size > 8) {
            resize(items.length / RFACTOR);
        }
        T item = getLast();
        int ind = arrayInd(size - 1);
        items[ind] = null;
        size = size - 1;
        nextLast = ind;
        return item;
    }

    public T getFirst() {
        int ind = arrayInd(0);
        return items[ind];
    }

    public T getLast() {
        int ind = arrayInd(size - 1);
        return items[ind];
    }

    public T get(int i) {
        int ind = arrayInd(i);
        return items[ind];
    }
    public static void main(String[]Args) {
        ArrayDeque<Integer> t = new ArrayDeque<>();
        t.addFirst(1);
        t.addFirst(2);
        t.addFirst(3);
        t.addFirst(4);
        t.addFirst(5);
        t.addFirst(6);
        t.addLast(100);
        t.addLast(99);
        t.addLast(98);
        t.addLast(97);
        t.addLast(96);
        t.addLast(95);
        t.printDeque();
        System.out.println(t.get(0));
        t.removeLast();
        t.removeFirst();
        t.printDeque();
        System.out.println(t.get(0));
        t.removeLast();
        t.removeFirst();
        t.removeLast();
        t.removeFirst();
        t.removeLast();
        t.removeFirst();
        t.removeLast();
        t.removeFirst();
        t.removeLast();
        t.removeFirst();
        t.printDeque();
        System.out.println(t.get(0));
        System.out.println(t.isEmpty());
    }

}
