package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 3;
        nextLast = 4;
    }

    private void resize(int capacity) {
        T[] a = (T[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            int ind = getArrayInd(i);
            a[capacity / 4 + i] = items[ind];
        }
        nextFirst = capacity / 4 - 1;
        nextLast = nextFirst + size + 1;
        items = a;

    }

    private int getArrayInd(int ind) {
        if ((ind + nextFirst + 1) >= items.length) {
            return ind + nextFirst + 1 - items.length;
        }
        return ind + nextFirst + 1;
    }

    @Override
    public void addFirst(T item) {
        expandSize();
        items[nextFirst] = item;
        size += 1;
        if (nextFirst == 0) {
            nextFirst = items.length - 1;
        } else {
            nextFirst -= 1;
        }
    }

    @Override
    public void addLast(T item) {
        expandSize();
        items[nextLast] = item;
        size += 1;
        if (nextLast == items.length - 1) {
            nextLast = 0;
        } else {
            nextLast += 1;
        }
    }

    private boolean isFull() {
        return size == items.length;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(items[getArrayInd(i)] + " ");
        }
        System.out.println();
    }

    private void expandSize() {
        if (size > items.length - 2) {
            resize(items.length * 2);
        }
    }

    private void shrinkSize() {
        if (size < items.length / 4) {
            resize(items.length / 2);
        }
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        shrinkSize();
        int ind = getArrayInd(0);
        T returnItem = items[ind];
        items[ind] = null;
        nextFirst = ind;
        size -= 1;
        return returnItem;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        shrinkSize();
        int ind = getArrayInd(size - 1);
        T returnItem = items[ind];
        items[ind] = null;
        nextLast = ind;
        size -= 1;
        return returnItem;
    }

    @Override
    public T get(int index) {
        return items[getArrayInd(index)];
    }

    @Override
    public Iterator<T> iterator() {
        return new MyIterator();
    }

    private class MyIterator implements Iterator<T> {
        private int wizPos;

        MyIterator() {
            wizPos = 0;
        }

        @Override
        public boolean hasNext() {
            if (wizPos < size) {
                return true;
            }
            return false;
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
