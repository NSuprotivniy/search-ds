package ru.mail.polis;

import java.util.Comparator;
import java.util.List;
import java.util.LinkedList;
import java.util.Random;

//TODO: write code here
public class RedBlackTree<E extends Comparable<E>> implements ISortedSet<E> {

    enum Color { RED, BLACK };


    private class Node {
        E value;
        Color color;
        Node left = nil;
        Node right = nil;
        Node parent = nil;

        Node(E value) {
            this.value = value;
        }

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

    private final Node nil = new Node(null);
    private Node root = nil;

    private int size;
    private final Comparator<E> comparator;

    public RedBlackTree() {
        this.comparator = null;
    }

    public RedBlackTree(Comparator<E> comparator) {
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
        if (r != nil) {
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
        return false;
    }

    @Override
    public boolean contains(E value) {
        if(contains(value, root) == nil)
            return false;
        return true;
    }

    private Node contains(E value, Node t) {
        if (root == nil) {
            return nil;
        }
        if (value.compareTo(t.value) < 0) {
            if (t.left != nil) {
                return contains(value, t.left);
            }
        }
        else if (value.compareTo(t.value) > 0) {
            if (t.right != nil) {
                return contains(value, t.right);
            }
        }
        else if (value.equals(t.value)) {
            return t;
        }
        return nil;
    }

    @Override
    public boolean add(E value) {
        Node temp = root;
        Node node = new Node(value);
        if (root == nil) {
            root = node;
            node.color = Color.BLACK;
            node.parent = nil;
        } else {
            node.color = Color.RED;
            while (true) {
                if (node.value.compareTo(temp.value) < 0) {
                    if (temp.left == nil) {
                        temp.left = node;
                        node.parent = temp;
                        break;
                    } else {
                        temp = temp.left;
                    }
                } else if (node.value.compareTo(temp.value) >= 0) {
                    if (temp.right == nil) {
                        temp.right = node;
                        node.parent = temp;
                        break;
                    } else {
                        temp = temp.right;
                    }
                }
            }
            fixTree(node);
        }
        size++;
        return true;
    }

    private void fixTree(Node node) {
        while (node.parent.color == Color.RED) {
            Node uncle = nil;
            if (node.parent == node.parent.parent.left) {
                uncle = node.parent.parent.right;

                if (uncle != nil && uncle.color == Color.RED) {
                    node.parent.color = Color.BLACK;
                    uncle.color = Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    node = node.parent.parent;
                    continue;
                }
                if (node == node.parent.right) {
                    node = node.parent;
                    rotateLeft(node);
                }
                node.parent.color = Color.BLACK;
                node.parent.parent.color = Color.RED;
                rotateRight(node.parent.parent);
            } else {
                uncle = node.parent.parent.left;
                if (uncle != nil && uncle.color == Color.RED) {
                    node.parent.color = Color.BLACK;
                    uncle.color = Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    node = node.parent.parent;
                    continue;
                }
                if (node == node.parent.left) {
                    node = node.parent;
                    rotateRight(node);
                }
                node.parent.color = Color.BLACK;
                node.parent.parent.color = Color.RED;
                rotateLeft(node.parent.parent);
            }
        }
        root.color = Color.BLACK;
    }

    void rotateLeft(Node node) {
        if (node.parent != nil) {
            if (node == node.parent.left) {
                node.parent.left = node.right;
            }
            else {
                node.parent.right = node.right;
            }
            node.right.parent = node.parent;
            node.parent = node.right;
            if (node.right.left != nil) {
                node.right.left.parent = node;
            }
            node.right = node.right.left;
            node.parent.left = node;
        }
        else {
            Node right = root.right;
            root.right = right.left;
            right.left.parent = root;
            root.parent = right;
            right.left = root;
            right.parent = nil;
            root = right;
        }
    }

    void rotateRight(Node node) {
        if (node.parent != nil) {
            if (node == node.parent.left) {
                node.parent.left = node.left;
            }
            else {
                node.parent.right = node.left;
            }
            node.left.parent = node.parent;
            node.parent = node.left;
            if (node.left.right != nil) {
                node.left.right.parent = node;
            }
            node.left = node.left.right;
            node.parent.right = node;
        }
        else {
            Node left = root.left;
            root.left = root.left.right;
            left.right.parent = root;
            root.parent = left;
            left.right = root;
            left.parent = nil;
            root = left;
        }
    }


    void transplant(Node target, Node with) {
        if(target.parent == nil) {
            root = with;
        }
        else if(target == target.parent.left) {
            target.parent.left = with;
        }
        else
            target.parent.right = with;
        with.parent = target.parent;
    }

    Node treeMinimum(Node subTreeRoot) {
        while(subTreeRoot.left!=nil) {
            subTreeRoot = subTreeRoot.left;
        }
        return subTreeRoot;
    }

    @Override
    public boolean remove(E value) {
        Node z;
        if((z = contains(value, root))==null)
            return false;
        Node x;
        Node y = z;
        Color y_original_color = y.color;

        if(z.left == nil) {
            x = z.right;
            transplant(z, z.right);
        }
        else if(z.right == nil) {
            x = z.left;
            transplant(z, z.left);
        }
        else {
            y = treeMinimum(z.right);
            y_original_color = y.color;
            x = y.right;
            if(y.parent == z)
                x.parent = y;
            else {
                transplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
            transplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.color = z.color;
        }
        if(y_original_color == Color.BLACK)
            deleteFixup(x);
        size--;
        return true;
    }

    void deleteFixup(Node x) {
        while(x!=root && x.color == Color.BLACK) {
            if(x == x.parent.left) {
                Node w = x.parent.right;
                if(w.color == Color.RED) {
                    w.color = Color.BLACK;
                    x.parent.color = Color.RED;
                    rotateLeft(x.parent);
                    w = x.parent.right;
                }
                if(w.left.color == Color.BLACK && w.right.color == Color.BLACK) {
                    w.color = Color.RED;
                    x = x.parent;
                    continue;
                }
                else if(w.right.color == Color.BLACK) {
                    w.left.color = Color.BLACK;
                    w.color = Color.RED;
                    rotateRight(w);
                    w = x.parent.right;
                }
                if(w.right.color == Color.RED) {
                    w.color = x.parent.color;
                    x.parent.color = Color.BLACK;
                    w.right.color = Color.BLACK;
                    rotateLeft(x.parent);
                    x = root;
                }
            }
            else {
                Node w = x.parent.left;
                if(w.color == Color.RED) {
                    w.color = Color.BLACK;
                    x.parent.color = Color.RED;
                    rotateRight(x.parent);
                    w = x.parent.left;
                }
                if(w.right.color == Color.BLACK && w.left.color == Color.BLACK) {
                    w.color = Color.RED;
                    x = x.parent;
                    continue;
                }
                else if(w.left.color == Color.BLACK) {
                    w.right.color = Color.BLACK;
                    w.color = Color.RED;
                    rotateLeft(w);
                    w = x.parent.left;
                }
                if(w.left.color == Color.RED) {
                    w.color = x.parent.color;
                    x.parent.color = Color.BLACK;
                    w.left.color = Color.BLACK;
                    rotateRight(x.parent);
                    x = root;
                }
            }
        }
        x.color = Color.BLACK;
    }

    private int compare(E v1, E v2) {
        return comparator == nil ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }

    @Override
    public String toString() {
        return "RBT{" + root + "}";
    }

    public static void main(String[] args) {
        RedBlackTree<Integer> tree = new RedBlackTree<Integer>();

        Random rnd = new Random();
        for (int i = 0; i < 20; i++) {
            tree.add(rnd.nextInt());
        }

        System.out.println(tree.size());
        for (Integer i : tree.inorderTraverse())
            System.out.println(i);
    }
}
