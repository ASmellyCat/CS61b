package deque;
import java.util.Iterator;
import java.util.ListIterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    /** Initialize IntNode Class. */
    private class Node {
        public T item;
        public Node next;
        public Node prev;

        public Node(T i, Node n, Node p) {
            item = i;
            next = n;
            prev = p;
        }
    }

    public int size;
    public Node sentinel;

    /** Initialize. */
    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    public LinkedListDeque(T item) {
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
        return new callInterator();
    }

    public class callInterator implements Iterator<T> {
        private int wizPos;
        public callInterator() {
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
        if (o instanceof LinkedListDeque) {
            LinkedListDeque<T> oas = (LinkedListDeque<T>) o;
            if (oas.size != this.size) {
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

    public static void main(String[]Args) {
        LinkedListDeque<Integer> t = new LinkedListDeque<>();
        LinkedListDeque<Integer> r = new LinkedListDeque<>(8);
        t.addFirst(1);
        t.addFirst(2);
        t.addFirst(3);
        t.addFirst(4);
        t.addLast(100);
        t.addLast(99);
        t.addLast(98);
        t.addLast(97);
        t.printDeque();
        System.out.println(t.get(0));
        System.out.println(t.removeLast());
        System.out.println(t.removeFirst());
        System.out.println(t.removeLast());
        System.out.println(t.removeFirst());
        System.out.println(t.removeLast());
        System.out.println(t.removeFirst());
        System.out.println(t.removeLast());
        System.out.println(t.removeFirst());
        System.out.println(t.removeLast());
        System.out.println(t.removeFirst());
        System.out.println(t.size());
        int n = 99;

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        for (int i = 0; i <= n; i++) {
            lld1.addLast(i);
        }
        LinkedListDeque<Integer> lld2 = new LinkedListDeque<>();
        for (int i = n; i >= 0; i--) {
            lld2.addFirst(i);
        }
        lld1.printDeque();
        System.out.println(lld1.equals(lld2));
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        for (int i = 0; i <= n; i++) {
            ad1.addLast(i);
        }
        System.out.println(lld1.equals(ad1));
    }
}