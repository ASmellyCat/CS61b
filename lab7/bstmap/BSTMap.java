package bstmap;

import edu.princeton.cs.algs4.BST;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private class BSTNode {
        private K key;
        private V value;
        private BSTNode left, right;

        BSTNode(K k, V v) {
            key = k;
            value = v;
        }
    }

    private BSTNode root;
    private int size;

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Argument to contain() is null.");
        }
        return containsKeyHelp(root, key);
    }

    private boolean containsKeyHelp(BSTNode node, K key) {
        if (node == null) {
            return false;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return containsKeyHelp(node.left, key);
        } else if (cmp > 0) {
            return containsKeyHelp(node.right, key);
        } else {
            return true;
        }
    }

    @Override
    public V get(K key) {
        return getHelp(root, key);
    }

    private V getHelp(BSTNode node, K key) {
        if (key == null) {
            throw new IllegalArgumentException("Argument to contain() is null.");
        }
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp > 0) {
            return getHelp(node.right, key);
        } else if (cmp < 0) {
            return getHelp(node.left, key);
        } else {
            return node.value;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("calls put() with a null key");
        }
        root = putHelp(root, key, value);
        size += 1;
    }

    private BSTNode putHelp(BSTNode node, K key, V value) {
        if (node == null) {
            return new BSTNode(key, value);
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = putHelp(node.left, key, value);
        } else if (cmp > 0) {
            node.right = putHelp(node.right, key, value);
        } else {
            node.value = value;
        }
        return node;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keysets = new HashSet<>();
        keySetHelp(root, keysets);
        return keysets;
    }

    private void keySetHelp(BSTNode node, Set<K> set) {
        if (node == null) {
            return;
        }
        set.add(node.key);
        keySetHelp(node.left, set);
        keySetHelp(node.right, set);
    }

    @Override
    public V remove(K key) {
        if (containsKey(key)) {
            V returnValue = get(key);
            root = removeHelp(root, key);
            size -= 1;
            return returnValue;
        }
        return null;
    }

    private BSTNode removeHelp(BSTNode node, K key) {
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = removeHelp(node.left, key);
        }
        else if (cmp > 0) {
            node.right = removeHelp(node.right, key);
        } else {
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }
            BSTNode t = node;
            node = min(t.right);
            node.right = removeMin(t.right);
            node.left = t.left;

        }
        return node;
    }

    private BSTNode removeMin(BSTNode node) {
        if (node.left == null) {
            return node.right;
        }
        node.left = removeMin(node.left);
        return node;
    }

    private BSTNode min(BSTNode node) {
        if (node.left == null) {
            return node;
        }
        return min(node.left);
    }

    public V remove(K key, V value) {
        if (containsKey(key)) {
            if (value == get(key)) {
                remove(key);
                return value;
            }
            return null;
        }
    }

    public Iterator<K> iterator () {
        return keySet().iterator();
    }

    public void printInOrder () {
        printInOrderHelp(root);
    }

    private void printInOrderHelp (BSTNode node){
        if (node == null) {
            return;
        }
        printInOrderHelp(node.left);
        System.out.print(node.key.toString() + " ");
        printInOrderHelp(node.right);
    }
}


