package hashmap;

import afu.org.checkerframework.checker.oigj.qual.O;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private static int DEFAULT_INITIAL_SIZE = 16;
    private static double DEFAULT_MAX_LOAD_FACTOR = 0.75;
    private double maxLoadFactor;
    private int size;
    private int length;
    private HashSet<K> keys;

    /**
     * Constructors
     */
    public MyHashMap() {
        this(DEFAULT_INITIAL_SIZE, DEFAULT_MAX_LOAD_FACTOR);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, DEFAULT_MAX_LOAD_FACTOR);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad     maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        maxLoadFactor = maxLoad;
        size = 0;
        length = initialSize;
        keys = new HashSet<>();
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     * <p>
     * The only requirements of a hash table bucket are that we can:
     * 1. Insert items (`add` method)
     * 2. Remove items (`remove` method)
     * 3. Iterate through items (`iterator` method)
     * <p>
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     * <p>
     * Override this method to use different data structures as
     * the underlying bucket type
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }


    // Your code won't compile until you do so!
    @Override
    public void clear() {
        buckets = createTable(DEFAULT_INITIAL_SIZE);
        size = 0;

    }

    @Override
    public boolean containsKey(K key) {
        if (key == null) throw new IllegalArgumentException("argument to contains() is null");
        return get(key) != null;
    }

    @Override
    public V get(K key) {
        if (key == null) throw new IllegalArgumentException("argument to contains() is null");
        int bucketIndex = getBucketIndex(key, buckets);
        Node node = getNode(bucketIndex, key);
        if (node == null) return null;
        return node.value;
    }

    private int getBucketIndex(K key, Collection<Node>[] buckets) {
        return Math.floorMod(key.hashCode(), buckets.length);
    }

    private Node getNode(int bucketIndex, K key) {
        if (buckets[bucketIndex] != null) {
            for (Node node : buckets[bucketIndex]) {
                if (node.key.equals(key)) return node;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if (key == null) return;
        int bucketIndex = getBucketIndex(key, buckets);
        Node node = getNode(bucketIndex, key);
        if (node != null) {
            node.value = value;
            return;
        }
        if (buckets[bucketIndex] == null) buckets[bucketIndex] = createBucket();
        buckets[bucketIndex].add(createNode(key, value));
        size += 1;
        keys.add(key);
        if (isMaxLoad()) resize(buckets.length * 2);

    }

    private void resize(int capacity) {
        Collection<Node>[] a = new Collection[capacity];
        for (K key : keys) {
            int busketIndex = getBucketIndex(key, a);
            if (a[busketIndex] == null) a[busketIndex] = createBucket();
            a[busketIndex].add(createNode(key, get(key)));
        }
        buckets = a;
        length = buckets.length;
    }

    private boolean isMaxLoad() {
        return ((double) size / buckets.length) >= maxLoadFactor;
    }

    @Override
    public Set<K> keySet() {
        return keys;
    }

    @Override
    public V remove(K key) {
        if (key == null) return null;
        int bucketIndex = getBucketIndex(key, buckets);
        Node node = getNode(bucketIndex, key);
        if (node != null) {
            buckets[bucketIndex].remove(node);
            size -= 1;
            keys.remove(node.key);
            return node.value;
        }
        return null;
    }

    @Override
    public V remove(K key, V value) {
        if (key == null) return null;
        int bucketIndex = getBucketIndex(key, buckets);
        Node node = getNode(bucketIndex, key);
        if (node != null) {
            if (node.value.equals(value)) {
                buckets[bucketIndex].remove(node);
                size -= 1;
                keys.remove(node.key);
                return node.value;
            }
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return new HashIterator();
    }

    private class HashIterator implements Iterator<K> {
        private Collection<Node>[] b = buckets;
        private int bucketIndex = 0;
        private Collection<Node> bucket = buckets[bucketIndex];
        private Iterator<Node> nodeIterator = bucket.iterator();

        @Override
        public boolean hasNext() {
            return nodeIterator.hasNext() || bucketIndex < length;
        }

        @Override
        public K next() {
            if (nodeIterator.hasNext()) {
                Node node = nodeIterator.next();
                return node.key;
            }
            bucketIndex += 1;
            bucket = buckets[bucketIndex];
            nodeIterator = bucket.iterator();
            return next();
        }



    }
}

