package ru.mail.polis;

import java.util.Comparator;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Random;

//TODO: write code here
public class OpenHashTable<E extends Comparable<E>> implements ISet<E> {

    public class HashEntry {
        private int key;
        private E value;

        HashEntry(int key, E value) {
            this.key = key;
            this.value = value;
        }

        public int getKey() {
            return key;
        }

        public E getValue() {
            return value;
        }
    }

    private int Capacity = 128;
    private int size;
    private HashEntry[] table;

    private HashEntry deleted = new HashEntry(-1, null);

    private Comparator<E> comparator;

    public OpenHashTable() {
        table =  (HashEntry[]) Array.newInstance(HashEntry.class, Capacity);
    }

    public OpenHashTable(int size) {
        Capacity = size;
        table =  (HashEntry[]) Array.newInstance(HashEntry.class, Capacity);
    }

    public OpenHashTable(Comparator<E> comparator) {

        this.comparator = comparator;
        table = (HashEntry[]) Array.newInstance(HashEntry.class, Capacity);

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
        while (table[hash] != null && value.compareTo(table[hash].getValue()) != 0)
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
            int hash = hash(value, ++step);
            if (table[hash] == null || table[hash] == deleted) {
                table[hash] = new HashEntry(hash, value);
                size++;
                return true;
            } else if (value.compareTo(table[hash].getValue()) == 0) {
                return false;
            }
        }
    }

    @Override
    public boolean remove(E value) {
        int step = 0;
        int hash = hash(value, 0);
        while (table[hash] != null && value.compareTo(table[hash].getValue()) != 0)
            hash = hash(value, ++step);
        if (table[hash] == null) {
            return false;
        }
        else if(value.compareTo(table[hash].getValue()) == 0) {
            table[hash] = deleted;
            size--;
            return true;
        }
        return true;
    }


    private int hash(E value, int step)
    {
        int key = value.hashCode();
        if (key < 0) key = -key;
        key = hash1(key) + step * hash2(key);
        return key  % Capacity;
    }

    private int hash1(int key) {
        return key % Capacity;
    }

    private int hash2(int key) {
        int h2 = 1 + (key & 7);
        if(h2 % 2 == 0) h2++;
        return h2;
    }

    private void resize() {
        if (size > Capacity / 2) {
            int old_size = Capacity;
            HashEntry[] old_table = table;

            Capacity = Capacity * 2;
            HashEntry[] table = (HashEntry[]) Array.newInstance(HashEntry.class, Capacity);

            rehash(old_table, old_size);

        } else if (size < Capacity / 4) {
            int old_size = Capacity;
            HashEntry[] old_table = table;

            Capacity = Capacity / 2;
            HashEntry[] table = (HashEntry[]) Array.newInstance(HashEntry.class, Capacity);

            rehash(old_table, old_size);
        }
    }

    private void rehash(HashEntry[] old_table, int old_size) {
        for (int i = 0; i < old_size; i++) {
            if (old_table[i] != null && old_table[i] != deleted) {
                add(old_table[i].getValue());
            }
        }
    }


    public Iterator<E> iterator() {
        return new OpenHashTableIterator();
    }

    private class OpenHashTableIterator implements Iterator<E> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            while (index < Capacity && (table[index] == null || table[index] == deleted)) index++;
            return index < Capacity - 1;
        }

        @Override
        public E next() {
            while (table[index] == null || table[index] == deleted) index++;
            return table[index++].getValue();
        }
    }


    public static void main(String[] args) {
        OpenHashTable<String> table = new OpenHashTable<String>();

        Random rnd = new Random();
        for (int i = 0; i < 20; i++) {
            table.add(Integer.toString(rnd.nextInt()));
        }


        System.out.println(table.size());

        Iterator<String> iterator = table.iterator();
        while (iterator.hasNext())
            System.out.println(iterator.next());
    }
}
