package ru.mail.polis;

import java.util.Comparator;
//import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Random;

//TODO: write code here
public class OpenHashTable<E extends Comparable<E>> implements ISet<E> {

//    public class HashEntry {
//        private int key;
//        private E value;
//
//        HashEntry(int key, E value) {
//            this.key = key;
//            this.value = value;
//        }
//
//        public int getKey() {
//            return key;
//        }
//
//        public E getValue() {
//            return value;
//        }
//    }

    private int capacity = 8;
    private int size;
    //private HashEntry[] table;
    private Object[] table;

    //private HashEntry deleted = new HashEntry(-1, null);
    private Object deleted = new Object();

    private Comparator<E> comparator;

//    public OpenHashTable() {
//        comparator = null;
//        table =  (HashEntry[]) Array.newInstance(HashEntry.class, capacity);
//    }
//
//    public OpenHashTable(int size) {
//        comparator = null;
//        capacity = size;
//        table =  (HashEntry[]) Array.newInstance(HashEntry.class, capacity);
//    }
//
//    public OpenHashTable(Comparator<E> comparator) {
//        this.comparator = comparator;
//        table = (HashEntry[]) Array.newInstance(HashEntry.class, capacity);
//
//    }

    public OpenHashTable() {
        comparator = null;
        table = new Object[capacity];
    }

    public OpenHashTable(int size) {
        comparator = null;
        capacity = size;
        table = new Object[capacity];
    }

    public OpenHashTable(Comparator<E> comparator) {
        this.comparator = comparator;
        table = new Object[capacity];

    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(E value) {
        int step = 0;
        int hash = hash(value, 0);
        //while (table[hash] != null && compare(value, table[hash].getValue()) != 0)
        while (table[hash] != null && compare(value, (E)table[hash]) != 0)
            hash = hash(value, ++step);
        if (table[hash] == null)
            return false;
        else
            return true;
    }

    @Override
    public boolean add(E value) {
        int step = 0;
        while (true) {
            int hash = hash(value, step++);
            if (table[hash] == null || table[hash] == deleted) {
                //table[hash] = new HashEntry(hash, value);
                table[hash] = value;
                size++;
                resize();
                return true;
            //} else if ( compare(value, table[hash].getValue()) == 0) {
            } else if ( compare(value, (E)table[hash]) == 0) {
                return false;
            }
        }
    }

    @Override
    public boolean remove(E value) {
        int step = 0;
        int hash = hash(value, 0);
        //while (table[hash] != null && table[hash] != deleted && compare(value, table[hash].getValue()) != 0)
        while (table[hash] != null && table[hash] == deleted || compare(value, (E)table[hash]) != 0)
            hash = hash(value, ++step);
        if (table[hash] == null) {
//            System.out.println("DEBUG");
            return false;
        }
        //else if(table[hash] == deleted || compare(value, table[hash].getValue()) == 0) {
        else if(table[hash] == deleted) {
            return false;
        }
        else if(compare(value, (E)table[hash]) == 0) {
            table[hash] = deleted;
            size--;
            return true;
        }
        return false;
    }


    private int hash(E value, int step)
    {
        int key = value.hashCode();
        if (key < 0) key = -key;
        key = hash1(key) + step * hash2(key);
        //System.out.println(capacity + " " + value + " " + key + " " + step);
        return key  % capacity;
    }

    private int hash1(int key) {
        return key % capacity;
    }

    private int hash2(int key) {
        int h2 = 1 + (key & 7);
        if(h2 % 2 == 0) h2++;
        return h2;
    }

    private void resize() {
        if (size > capacity / 2) {
            int oldCapacity = capacity;
            //HashEntry[] old_table = table;
            Object[] old_table = table;

            size = 0;
            capacity = capacity * 2;
            //table = (HashEntry[]) Array.newInstance(HashEntry.class, capacity);
            table = new Object[capacity];

            rehash(old_table, oldCapacity);

        }
    }

    //private void rehash(HashEntry[] old_table, int oldCapacity) {
    private void rehash(Object[] old_table, int oldCapacity) {
        for (int i = 0; i < oldCapacity; i++) {
            if (old_table[i] != null && old_table[i] != deleted) {
                //add(old_table[i].getValue());
                add((E)old_table[i]);
            }
        }
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }


    public Iterator<E> iterator() {
        return new OpenHashTableIterator();
    }

    private class OpenHashTableIterator implements Iterator<E> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            while (index < capacity && (table[index] == null || table[index] == deleted)) index++;
            return index < capacity - 1;
        }

        @Override
        public E next() {
            while (index < capacity && (table[index] == null || table[index] == deleted)) index++;
            //return table[index++].getValue();
            return (E)table[index++];
        }
    }



    public static void main(String[] args) {
        OpenHashTable<String> table = new OpenHashTable<String>();

        Random rnd = new Random();
        for (int i = 0; i < 100; i++) {
            table.add(Integer.toString(rnd.nextInt()));
        }

        Iterator<String> iterator;

        System.out.println(table.size());
        iterator = table.iterator();
        while (iterator.hasNext())
            System.out.println(iterator.next());


        boolean result;
        String value;
        iterator = table.iterator();
        while (iterator.hasNext()) {
            value = iterator.next();
            result = table.remove(value);
            System.out.println(value + " " + result);
        }


        System.out.println(table.size());
        iterator = table.iterator();
        while (iterator.hasNext())
            System.out.println(iterator.next());

        for (int i = 0; i < 100; i++) {
            table.add(Integer.toString(rnd.nextInt()));
        }

        System.out.println(table.size());
        iterator = table.iterator();
        while (iterator.hasNext())
            System.out.println(iterator.next());
    }
}
