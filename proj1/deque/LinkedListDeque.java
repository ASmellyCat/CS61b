package deque;
import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    /**
     * Initialize IntNode Class.
     */
    private class Node {
        private T item;
        private Node next;
        private Node prev;

        Node(T i, Node n, Node p) {
            item = i;
            next = n;
            prev = p;
        }
    }

    private int size;
    private Node sentinel;

    /**
     * Initialize.
     */
    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    private LinkedListDeque(T item) {
        sentinel = new Node(null, null, null);
        sentinel.next = new Node(item, sentinel, sentinel);
        sentinel.prev = sentinel.next;
        size = 1;
    }

    @Override
    public void addFirst(T item) {
        sentinel.next.prev = new Node(item, sentinel.next, sentinel);
        sentinel.next = sentinel.next.prev;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        sentinel.prev.next = new Node(item, sentinel, sentinel.prev);
        sentinel.prev = sentinel.prev.next;
        size += 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    /** Print link. */
    public void printDeque() {
        Node p = sentinel;
        while (p.next != sentinel) {
            System.out.print(p.next.item + " ");
            p = p.next;
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T j = sentinel.next.item;
        sentinel.next.next.prev = sentinel;
        sentinel.next = sentinel.next.next;
        size -= 1;
        return j;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        T j = sentinel.prev.item;
        sentinel.prev.prev.next = sentinel;
        sentinel.prev = sentinel.prev.prev;
        size -= 1;
        return j;
    }

    @Override
    public T get(int index) {
        if (index > (size - 1)) {
            return null;
        }
        Node p = sentinel;
        for (int i = 0; i <= index; i++) {
            p = p.next;
        }
        return p.item;
    }

    private T getRecursiveHelp(Node a, int i) {
        if (i == 0) {
            return a.next.item;

        } else {
            a = a.next;
            i -= 1;
        }
        return getRecursiveHelp(a, i);
    }

    public T getRecursive(int index) {
        if (index > (size - 1)) {
            return null;
        }
        Node p = sentinel;
        return getRecursiveHelp(p, index);
    }

    @Override
    public Iterator<T> iterator() {
        return new CallInterator();
    }

    private class CallInterator implements Iterator<T> {
        private int wizPos;

        CallInterator() {
            wizPos = 0;
        }

        public boolean hasNext() {
            return wizPos < size;
        }

        public T next() {
            T returnItem = get(wizPos);
            wizPos += 1;
            return returnItem;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Deque) {
            Deque<T> oas = (Deque<T>) o;
            if (oas.size() != this.size) {
                return false;
            }
            for (int i = 0; i < size; i += 1) {
                if (!(oas.get(i).equals(this.get(i)))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
