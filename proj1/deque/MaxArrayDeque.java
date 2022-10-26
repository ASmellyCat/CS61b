package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private Comparator<T> cmp;

    public MaxArrayDeque(Comparator<T> c) {
        cmp = c;
    }

    public T max() {
        if (isEmpty()) {
            return null;
        }
        T returnItem = get(0);
        for (T item: this) {
            if (cmp.compare(item, returnItem) > 0) {
                returnItem = item;
            }
        }
        return returnItem;
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        T returnItem = get(0);
        for (T item : this) {
            if (c.compare(item, returnItem) > 0) {
                returnItem = item;
            }
        }
        return returnItem;
    }
}
