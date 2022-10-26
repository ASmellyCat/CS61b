package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class Node {
        private T item;
        private Node prev;
        private Node next;

        Node(T i, Node p, Node n) {
            item = i;
            prev = p;
            next = n;
        }
    }

    private int size;
    private Node sentinel;

    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        sentinel.next.prev = new Node(item, sentinel, sentinel.next);
        sentinel.next = sentinel.next.prev;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        sentinel.prev.next = new Node(item, sentinel.prev, sentinel);
        sentinel.prev = sentinel.prev.next;
        size += 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node p = sentinel.next;
        for (int i = 0; i < size; i++) {
            System.out.print(p.item + " ");
            p = p.next;
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T returnItem = sentinel.next.item;
        sentinel.next.prev = sentinel;
        sentinel.next = sentinel.next.next;
        size -= 1;
        return returnItem;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        T returnItem = sentinel.prev.item;
        sentinel.prev.next = sentinel;
        sentinel.prev = sentinel.prev.prev;
        size -= 1;
        return returnItem;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        Node p = sentinel.next;
        for (int i = 0; i < index; i++) {
            p = p.next;
        }
        return p.item;
    }

    public T getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        Node p = sentinel;
        return getRecursiveHelp(p, index);
    }

    private T getRecursiveHelp(Node node, int index) {
        if (index == 0) {
            return node.next.item;
        }
        node = node.next;
        index -= 1;
        return getRecursiveHelp(node, index);
    }

    @Override
    public Iterator<T> iterator() {
        return new myIterator();
    }

    private class myIterator implements Iterator<T> {
        private int wizPos;
        myIterator() {
            wizPos = 0;
        }

        @Override
        public boolean hasNext() {
            return (wizPos < size);
        }

        @Override
        public T next() {
            T returnItem = get(wizPos);
            wizPos += 1;
            return returnItem;
        }
    }

    public boolean equals(Object o) {
        if (o instanceof Deque) {
            Deque<T> oas = (Deque) o;
            if (oas.size() != this.size) {
                return false;
            }
            for (int i = 0; i < size; i++) {
                if (!(oas.get(i).equals(this.get(i)))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
