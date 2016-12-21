package ru.mail.polis;

import java.util.Comparator;
import java.util.List;
import java.util.LinkedList;
import java.util.Random;

//TODO: write code here
public class AVLTree<E extends Comparable<E>> implements ISortedSet<E> {

    class Node {

        Node(E value) {
            this.value = value;
        }

        E value;
        Node left;
        Node right;

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
    private int size;

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
        return size;
    }

    @Override
    public boolean isEmpty() {
        if(root == null)
            return false;
        return true;
    }

    @Override
    public boolean contains(E value) {
        Node node = root;

        while (node != null) {
            int cmp = compare(value, node.value);
            if (cmp == 0) return true;
            if (cmp < 0) node = node.left;
            if (cmp > 0) node = node.right;
        }

        return false;
    }


    @Override
    public boolean add( E value )
    {
        if (value == null) {
            throw new NullPointerException("value is null");
        }

        Node node = add( value, root );
        if (node == null) return false;

        size++;
        root = node;
        return true;
    }

    private Node add( E value, Node node )
    {
        if( node == null )
            node = new Node( value );
        else if( compare(value, node.value) < 0 )
        {
            node.left = add( value, node.left );
            if( height( node.left ) - height( node.right ) == 2 )
                if( compare(value, node.left.value ) < 0 )
                    node = rotateLeft( node );
                else
                    node = doubleRotateLeft( node );
        }
        else if( compare(value, node.value ) > 0 )
        {
            node.right = add( value, node.right );
            if( height( node.right ) - height( node.left ) == 2 )
                if( compare(value, node.right.value ) > 0 )
                    node = rotateRight( node );
                else
                    node = doubleRotateRight( node );
        }
        else return null;

        node.height = max( height( node.left ), height( node.right ) ) + 1;

        return node;
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
        //if (!contains(value)) return false;
        root = remove(value, root);
        return true;
    }

    private Node remove(E x, Node node) {
        if(node == null)
            return node;
        int compareResult = compare(x, node.value);
        if(compareResult < 0)
            node.left = remove(x, node.left);
        else if(compareResult > 0)
            node.right = remove(x, node.right);
        else if(node.left != null && node.right != null) {
            node.value = findMin(node.right).value;
            node.right = remove(node.value, node.right);
        }
        else {
            size--;
            node = (node.left != null) ? node.left : node.right;
        }
        return balance(node);
    }


    private Node findMin(Node t) {
        if(t == null)
            return t;
        while(t.left != null)
            t = t.left;
        return t;
    }

    private Node balance(Node node) {
        if(node == null)
            return node;
        if(height(node.left) - height(node.right) > 1)
            if(height(node.left.left) >= height(node.left.right))
                node = rotateLeft(node);
            else
                node = doubleRotateLeft(node);
        else
        if(height(node.right) - height(node.left) > 1)
            if(height(node.right.right) >= height(node.right.left))
                node = rotateRight(node);
            else
                node = doubleRotateRight(node);
        node.height = Math.max(height(node.left), height(node.right)) + 1;
        return node;
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }




    private Node doubleRotateLeft( Node k3 )
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

        for (Integer i : tree.inorderTraverse())
            tree.remove(i);

        System.out.println(tree.size());
        for (Integer i : tree.inorderTraverse())
            System.out.println(i);


        for (int i = 0; i < 20; i++) {
            tree.add(rnd.nextInt());
        }

        System.out.println(tree.size());
        for (Integer i : tree.inorderTraverse())
            System.out.println(i);
    }
}
