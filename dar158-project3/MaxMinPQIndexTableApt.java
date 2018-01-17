/**
 * Max Min Priority Queue for Apt class. HashMap is used as an index table where
 * address + apartment number + zip code is a Key and index of the item in
 * Priority Queue is the Value. Max or Min is set by implementing the abstract
 * method isHigherPriority(Apt a1, Apt a2). IsHigherPriority returns true if a1
 * is higher priority that a2 false otherwise
 */

import java.util.HashMap;
import java.util.NoSuchElementException;

public abstract class MaxMinPQIndexTableApt {

    private HashMap<String, Integer> indexTable = new HashMap<>();
    private Apt[] pq;                    // store items at indices 1 to n
    private int n;                       // number of items on priority queue

    /**
     * Check if the given Apt class instance is the the queue
     *
     * @param apt
     * @return true if it is, false otherwise
     */
    public boolean isInPQ(Apt apt) {
        return !(indexTable.get(apt.getUniqueIdentifier()) == null);
    }
    /**
     * Get Apt instance for given Apt object's unique identifier which is 
     * streetAddress + aptNumber + zip.   
     * @param apt
     * @return 
     */
    public Apt getAptByUnigueIdentifier(Apt apt ){
        return pq[indexTable.get(apt.getUniqueIdentifier())];
    }
    /**
     * Update the price of Apt if it exist in the queue.
     *
     * @param apt instance of Apt class
     */
    public void updatePriceIfExist(Apt apt) {
        if (indexTable.get(apt.getUniqueIdentifier()) != null) {
            int index = indexTable.get(apt.getUniqueIdentifier());
            updatePrice(index, apt);
        }
    }

    public void deleteIfExist(Apt apt) {
        if (indexTable.get(apt.getUniqueIdentifier()) != null) {
            int index = indexTable.get(apt.getUniqueIdentifier());
            delete(index);
        }
    }

    /**
     * Delete an item from the queue with given index in the priority queue.
     * Index starts from 1 to n.
     *
     * @param index index of the instance to be deleted. Index starts with 1
     */
    public void delete(int index) {
        exch(index, n); // put at the end 
//        pq[n--] = null;
        n--;
        if (index == 1) {//this is root
            sink(index);
            return;
        }
        if (isHigherPriority(pq[index], pq[index / 2])) {// higher priority than its parent node in the tree
            swim(index);
        } else {
            sink(index);
        }
    }

    /**
     * Update the price for the apartment with a price of a given Apt instance.
     * Only the price is updated nothing else.
     * 
     * @param index index to be updated
     * @param apt Apt instance to be updatePrice with
     */
    public void updatePrice(int index, Apt apt) {
        pq[index].setPrice(apt.getPrice());
        if (index == 1) {
            sink(index);
        } else if (isHigherPriority(pq[index], pq[index / 2])) {// higher priority than its parent node in the tree
            swim(index);
        } else {
            sink(index);
        }
    }

    /**
     * this method is used to set up pick min
     *
     * @param a1
     * @param a2
     * @return true if a1 is higher priority that a2 false otherwise
     */
    public abstract boolean isHigherPriority(Apt a1, Apt a2);

    /**
     * Initializes an empty priority queue with the given initial capacity.
     *
     * @param initCapacity the initial capacity of this priority queue
     */
    public MaxMinPQIndexTableApt(int initCapacity) {
        pq = new Apt[initCapacity + 1];
        n = 0;
    }

    public HashMap<String, Integer> getIndexTable() {
        return indexTable;
    }

    /**
     * Initializes an empty priority queue.
     */
    public MaxMinPQIndexTableApt() {
        this(1);
    }

    /**
     * Returns true if this priority queue is empty.
     *
     * @return {@code true} if this priority queue is empty; {@code false}
     * otherwise
     */
    public boolean isEmpty() {
        return n == 0;
    }

    /**
     * Returns the number of keys on this priority queue.
     *
     * @return the number of keys on this priority queue
     */
    public int size() {
        return n;
    }

    /**
     * Returns a highest priority key on this priority queue.
     *
     * @return a largest key on this priority queue
     * @throws NoSuchElementException if this priority queue is empty
     */
    public Apt pick() {
        if (isEmpty()) {
            throw new NoSuchElementException("Priority queue underflow");
        }
        return pq[1];
    }

    // helper function to double the size of the heap array
    private void resize(int capacity) {
        assert capacity > n;
        Apt[] temp = new Apt[capacity];
        for (int i = 1; i <= n; i++) {
            temp[i] = pq[i];
        }
        pq = temp;
    }

    /**
     * Adds a new key to this priority queue.
     *
     * @param x the new key to add to this priority queue
     */
    public void insert(Apt x) {

        // double size of array if necessary
        if (n >= pq.length - 1) {
            resize(2 * pq.length);
        }

        // add x, and percolate it up to maintain heap invariant
        pq[++n] = x;
        indexTable.put(pq[n].getUniqueIdentifier(), n);
        swim(n);
        assert isMaxHeap();
    }

    /**
     * Removes and returns a largest key on this priority queue.
     *
     * @return a largest key on this priority queue
     * @throws NoSuchElementException if this priority queue is empty
     */
    public Apt pull() {
        if (isEmpty()) {
            throw new NoSuchElementException("Priority queue underflow");
        }
        Apt max = pq[1];
        exch(1, n--);
        sink(1);
        indexTable.remove(pq[n + 1].getUniqueIdentifier());
        pq[n + 1] = null;     // to avoid loiterig and help with garbage collection
        if ((n > 0) && (n == (pq.length - 1) / 4)) {
            resize(pq.length / 2);
        }
        assert isMaxHeap();
        return max;
    }

    /**
     * *************************************************************************
     * Helper functions for compares and swaps.
     * *************************************************************************
     */
    private boolean less(int i, int j) {
        return !isHigherPriority(pq[i], pq[j]);
    }

    private void exch(int i, int j) {
        Apt swap = pq[i];
        pq[i] = pq[j];
        indexTable.put(pq[i].getUniqueIdentifier(), i);// updatePrice the index table 
        pq[j] = swap;
        indexTable.put(pq[j].getUniqueIdentifier(), j);// updatePrice the index table
    }

    // is pq[1..N] a pick heap?
    private boolean isMaxHeap() {
        return isMaxHeap(1);
    }

    // is subtree of pq[1..n] rooted at k a pick heap?
    private boolean isMaxHeap(int k) {
        if (k > n) {
            return true;
        }
        int left = 2 * k;
        int right = 2 * k + 1;
        if (left <= n && less(k, left)) {
            return false;
        }
        if (right <= n && less(k, right)) {
            return false;
        }
        return isMaxHeap(left) && isMaxHeap(right);
    }

    /**
     * *************************************************************************
     * Helper functions to restore the heap invariant.
     * *************************************************************************
     */
    private void swim(int k) {
        while (k > 1 && less(k / 2, k)) {
            exch(k, k / 2);
            k = k / 2;
        }
    }

    private void sink(int k) {
        while (2 * k <= n) {
            int j = 2 * k;
            if (j < n && less(j, j + 1)) {
                j++;
            }
            if (!less(k, j)) {
                break;
            }
            exch(k, j);
            k = j;
        }
    }

}
