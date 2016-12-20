package ru.mail.polis;

import java.util.Comparator;
import java.util.List;
import java.util.LinkedList;
import java.util.Random;

//TODO: write code here
public class AVLTree<E extends Comparable<E>> implements ISortedSet<E> {

    class Node {

        Node(E value, Node parent) {
            this.value = value;
            this.parent = parent;
            size = 1;
        }

        E value;
        Node left;
        Node right;
        Node parent;

        int size;

        int height;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("N{");
            sb.append("d=").append(value);
            if (left != null) {
                sb.append(", l=").append(left);
            }
            if (right != null) {
                sb.append(", r=").append(right);
            }
            sb.append('}');
            return sb.toString();
        }

    }

    private Node root;
    private final Comparator<E> comparator;

    public AVLTree() {
        this.comparator = null;
    }

    public AVLTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public E first() {

        if (isEmpty()) {
            throw new IllegalArgumentException("set is empty, no first element");
        }
        Node curr = root;
        while (curr.left != null) {
            curr = curr.left;
        }
        return curr.value;
    }

    @Override
    public E last() {
        if (isEmpty()) {
            throw new IllegalArgumentException("set is empty, no last element");
        }
        Node curr = root;
        while (curr.right != null) {
            curr = curr.right;
        }
        return curr.value;
    }

    @Override
    public List<E> inorderTraverse() {
        List<E> list = new LinkedList<E>();
        inorder(root, list);
        return list;
    }

    private void inorder(Node r, List<E> list) {
        if (r != null) {
            inorder(r.left, list);
            list.add(r.value);
            inorder(r.right, list);
        }
    }

    @Override
    public int size() {
        return root.size;
    }

    @Override
    public boolean isEmpty() {
        if(root == null)
            return false;
        return true;
    }

    @Override
    public boolean contains(E value) {
        return false;
    }

    @Override
    public boolean add(E value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }

        if (root == null) {
            root = new Node(value, null);
            return true;
        }

        Node curr = root;
        while (true) {
            int cmp = compare(curr.value, value);
            if (cmp == 0) {
                return false;
            } else if (cmp < 0) {
                if (curr.right != null) {
                    curr = curr.right;
                } else {
                    curr.right = new Node(value, curr);
                    break;
                }
            } else if (cmp > 0) {
                if (curr.left != null) {
                    curr = curr.left;
                } else {
                    curr.left = new Node(value, curr);
                    break;
                }
            }
        }

        balance(curr);

        return true;
    }

    private Node min(Node t) {
        if(t == null)
            return t;
        while(t.left != null)
            t = t.left;
        return t;
    }

    @Override
    public boolean remove(E value) {
        if (value == null) throw new IllegalArgumentException("argument to delete() is null");
        if (!contains(value)) return false;
        root = remove(root, value);
        return true;
    }

    private Node remove(Node x, E value) {
        int cmp = value.compareTo(x.value);
        if (cmp < 0) {
            x.left = remove(x.left, value);
        }
        else if (cmp > 0) {
            x.right = remove(x.right, value);
        }
        else {
            if (x.left == null) {
                return x.right;
            }
            else if (x.right == null) {
                return x.left;
            }
            else {
                Node y = x;
                x = min(y.right);
                x.right = deleteMin(y.right);
                x.left = y.left;
            }
        }
        x.size = 1 + x.left.size + x.right.size;
        x.height = 1 + Math.max(height(x.left), height(x.right));
        return balance(x);
    }

    private Node deleteMin(Node x) {
        if (x.left == null) return x.right;
        x.left = deleteMin(x.left);
        x.size = 1 + x.left.size + x.right.size;
        x.height = 1 + Math.max(height(x.left), height(x.right));
        return balance(x);
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }


    private Node balance(Node x) {

        Node y = x;
        while (x != root) {
            if (balanceFactor(x) < -1) {
                if (balanceFactor(x.right) > 0) {
                    x.right = rotateRight(x.right);
                }
                x = rotateLeft(x);
            } else if (balanceFactor(x) > 1) {
                if (balanceFactor(x.left) < 0) {
                    x.left = rotateLeft(x.left);
                }
                x = rotateRight(x);
            }

            x = x.parent;
        }

        return y;
    }

    private int balanceFactor(Node x) {
        return height(x.left) - height(x.right);
    }

    private Node doubleRodateLeft( Node k3 )
    {
        k3.left = rotateRight( k3.left );
        return rotateLeft( k3 );
    }

    private Node doubleRotateRight( Node k1 )
    {
        k1.right = rotateLeft( k1.right );
        return rotateRight( k1 );
    }

    private Node rotateLeft( Node k2 )
    {
        Node k1 = k2.left;
        k2.left = k1.right;
        k1.right = k2;
        k2.height = max( height( k2.left ), height( k2.right ) ) + 1;
        k1.height = max( height( k1.left ), k2.height ) + 1;
        return k1;
    }

    private Node rotateRight( Node k1 )
    {
        Node k2 = k1.right;
        k1.right = k2.left;
        k2.left = k1;
        k1.height = max( height( k1.left ), height( k1.right ) ) + 1;
        k2.height = max( height( k2.right ), k1.height ) + 1;
        return k2;
    }

    private int height(Node node)
    {
        return node != null ? node.height : 0;
    }

    private int max(int a, int b)
    {
        return a > b ? a : b;
    }


    public static void main(String[] args) {
        AVLTree<Integer> tree = new AVLTree<Integer>();

        Random rnd = new Random();
        for (int i = 0; i < 20; i++) {
            tree.add(rnd.nextInt());
        }

        System.out.println(tree.size());
        for (Integer i : tree.inorderTraverse())
            System.out.println(i);
    }
}
