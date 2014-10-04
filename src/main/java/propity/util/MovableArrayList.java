/*
 *   Project: Speedith.Core
 * 
 * File name: MovableArrayList.java
 *    Author: Matej Urbas [matej.urbas@gmail.com]
 * 
 *  Copyright © 2012 Matej Urbas
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package propity.util;

import java.util.*;

/**
 * This is a wrapper class that wraps a single {@link ArrayList} and delegates
 * all its operations to it.
 *
 * <p>One important difference between this container and the {@link ArrayList}
 * is that this class provides methods {@link MovableArrayList#moveTo(propity.util.MovableArrayList)
 * }
 * and {@link MovableArrayList#swapWith(propity.util.MovableArrayList) }
 * which either move the backing array list from this instance to the other or
 * swaps the backing array (both in O(1)).</p>
 *
 * <p>This class does not provide a way to get or change the backing array list
 * in any other way. This (together with the move-functionality described above)
 * provides a way to safely pass ownership of array lists to other classes,
 * which can properly encapsulate the reference without fear for
 * reference-escape.</p>
 *
 * <p><span style="font-weight:bold">Important</span>: this class is not
 * thread-safe. Wrapping it into
 * {@link Collections#synchronizedList(java.util.List) a synchronised wrapper}
 * helps only if no
 * {@link MovableArrayList#swapWith(propity.util.MovableArrayList) swapping} or
 * {@link MovableArrayList#moveTo(propity.util.MovableArrayList) moving} is
 * performed (as this may change the backing array while it is being accessed in
 * any way).</p>
 *
 * <p>To prevent a leaked reference from the {@link MovableArrayList#iterator()
 * }, {@link MovableArrayList#listIterator()}, and {@link MovableArrayList#listIterator(int)
 * } methods, we implement a custom iterator. The iterator is a naive
 * implementation and does not check for concurrent modifications or
 * similar.</p>
 *
 * <p>This class is <span style="font-weight:bold">not thread-safe</span>.</p>
 *
 * @param <E> the type of elements stored in this collection.
 * @author Matej Urbas [matej.urbas@gmail.com]
 */
public final class MovableArrayList<E> implements List<E>, RandomAccess {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private ArrayList<E> store;
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Creates a new empty movable array list.
     */
    public MovableArrayList() {
        store = new ArrayList<>();
    }

    /**
     * Creates an instance of the movable array list and copies the contents of
     * the given {@link Collection} to this instance.
     *
     * @param c the collection from which to copy the contents.
     */
    public MovableArrayList(Collection<? extends E> c) {
        store = new ArrayList<>(c);
    }

    /**
     * Creates an instance of the movable array list and initialises its backing
     * array list to the given capacity.
     *
     * @param initialCapacity the initial capacity of the backing
     * {@link ArrayList}.
     */
    public MovableArrayList(int initialCapacity) {
        store = new ArrayList<>(initialCapacity);
    }

    /**
     * Creates an instance of the movable array list and either copies or moves
     * the contents of the given {@link MovableArrayList} to this instance.
     *
     * <p>The new and the given instance will <span
     * style="font-weight:bold">not</span> share the same backing store. In
     * fact, two movable array lists will never share the same backing
     * store.</p>
     *
     * @param c the other movable array list from which to either copy or move
     * the contents.
     *
     * @param move indicates whether the contents of the given movable array
     * list should be copied or moved.
     */
    public MovableArrayList(MovableArrayList<? extends E> c, boolean move) {
        if (move) {
            // NOTE: Here we are passing the uninitialised `this` reference to
            // the `move` method, which does not leak the reference further and
            // initialises the backing store. It doesn't call any other method
            // or constructor which could further leak the reference, so it's
            // okay.
            move(c, this);
        } else {
            store = new ArrayList<>(c.store);
        }
    }

    /**
     * Creates an instance of the movable array list that uses the given array
     * as the backing store.
     *
     * @param list this array will be used as the backing store (no copy of it
     * will be created).
     */
    private MovableArrayList(ArrayList<E> list) {
        store = list;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Move Semantics">
    /**
     * Moves the contents (the backing array) of this list to
     * {@code destination}.
     *
     * <p>This collection will be empty after this operation finishes.</p>
     *
     * <p><span style="font-weight:bold">Important</span>: this method is not
     * thread-safe.</p>
     */
    public void moveTo(MovableArrayList<? super E> destination) {
        move(this, destination);
    }

    /**
     * Swaps the backing array of this list with the one in {@code other}.
     *
     * <p><span style="font-weight:bold">Important</span>: this method is not
     * thread-safe.</p>
     *
     * @param other the other list with which to swap contents.
     */
    public void swapWith(MovableArrayList<E> other) {
        swap(this, other);
    }

    /**
     * This method moves the backing array of {@code source} to
     * {@code destination}.
     *
     * <p>Additionally, {@code source}'s backing array is initialised with a new
     * empty {@link ArrayList}.</p>
     *
     * <p><span style="font-weight:bold">Important</span>: this method is not
     * thread-safe.</p>
     *
     * @param <TDestination> the type of elements stored in {@code destination}.
     * @param source the collection from which to remove the backing array and
     * put it into {@code destination}.
     * @param destination the collection of which the old backing array will be
     * forfeited and replaced by the backing array of {@code source}.
     */
    @SuppressWarnings("unchecked")
    public static <TDestination> void move(
            MovableArrayList<? extends TDestination> source,
            MovableArrayList<TDestination> destination) {
        ArrayList<TDestination> tmp = (ArrayList<TDestination>) source.store;
        source.store = new ArrayList();
        destination.store = tmp;
    }

    /**
     * This method swaps the backing array of {@code source} with the one of
     * {@code destination} (in O(1)).
     *
     * <p><span style="font-weight:bold">Important</span>: this method is not
     * thread-safe.</p>
     *
     * @param <E> the type of elements stored involved collections.
     * @param a the first collection.
     * @param b the second collection.
     */
    public static <E> void swap(MovableArrayList<E> a, MovableArrayList<E> b) {
        ArrayList<E> tmpA = a.store;
        a.store = b.store;
        b.store = tmpA;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Read-only Backing-Store Access">
    /**
     * Returns an unmodifiable view of the backing store (which is an
     * {@link ArrayList}).
     *
     * <p>The list returned by this method wraps the backing store directly.
     * Thus, if the backing store changes in this movable array, this does not
     * affect the returned list (i.e., the returned list will still wrap the old
     * backing store).</p>
     *
     * @return an unmodifiable view of the backing store (which is an
     * {@link ArrayList}).
     */
    public List<E> getReadOnlyStore() {
        return Collections.unmodifiableList(store);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Static Creation Methods">
    public static <E> MovableArrayList<E> create(E el1) {
        ArrayList<E> newList = new ArrayList<>();
        newList.add(el1);
        return new MovableArrayList<>(newList);
    }

    public static <E> MovableArrayList<E> create(E el1, E el2) {
        ArrayList<E> newList = new ArrayList<>();
        newList.add(el1);
        newList.add(el2);
        return new MovableArrayList<>(newList);
    }

    public static <E> MovableArrayList<E> create(E el1, E el2, E el3) {
        ArrayList<E> newList = new ArrayList<>();
        newList.add(el1);
        newList.add(el2);
        newList.add(el3);
        return new MovableArrayList<>(newList);
    }

    public static <E> MovableArrayList<E> create(E el1, E el2, E el3, E el4) {
        ArrayList<E> newList = new ArrayList<>();
        newList.add(el1);
        newList.add(el2);
        newList.add(el3);
        newList.add(el4);
        return new MovableArrayList<>(newList);
    }
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="List implementation">
    @Override
    public int size() {
        return store.size();
    }

    @Override
    public boolean isEmpty() {
        return store.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return store.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return new ListIteratorImpl(0);
    }

    @Override
    public Object[] toArray() {
        return store.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return store.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return store.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return store.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return store.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return store.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return store.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return store.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return store.retainAll(c);
    }

    @Override
    public void clear() {
        store.clear();
    }

    @Override
    public E get(int index) {
        return store.get(index);
    }

    @Override
    public E set(int index, E element) {
        return store.set(index, element);
    }

    @Override
    public void add(int index, E element) {
        store.add(index, element);
    }

    @Override
    public E remove(int index) {
        return store.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return store.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return store.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return new ListIteratorImpl(0);
    }

    @Override
    public ListIterator<E> listIterator(final int index) {
        return new ListIteratorImpl(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return store.subList(fromIndex, toIndex);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Iterator">
    private class ListIteratorImpl implements ListIterator<E> {

        /**
         * Index of the next element to return.
         */
        private int nextIdx;
        /**
         * Index of last element returned. Has value of -1 if no such.
         */
        private int lastIdx = -1;

        public ListIteratorImpl() {
            this(0);
        }

        public ListIteratorImpl(int nextIndex) {
            this.nextIdx = nextIndex;
        }

        @Override
        public boolean hasNext() {
            return nextIdx < MovableArrayList.this.size();
        }

        @Override
        public E next() {
            E tmp = MovableArrayList.this.get(nextIdx);
            lastIdx = nextIdx;
            ++nextIdx;
            return tmp;
        }

        @Override
        public boolean hasPrevious() {
            return nextIdx > 0;
        }

        @Override
        public E previous() {
            int i = nextIdx - 1;
            E tmp = MovableArrayList.this.get(i);
            nextIdx = i;
            lastIdx = i;
            return tmp;
        }

        @Override
        public int nextIndex() {
            return nextIdx;
        }

        @Override
        public int previousIndex() {
            return nextIdx - 1;
        }

        @Override
        public void remove() {
            MovableArrayList.this.remove(lastIdx);
            nextIdx = lastIdx;
            lastIdx = -1;
        }

        @Override
        public void set(E e) {
            if (lastIdx < 0) {
                throw new IllegalStateException();
            }
            MovableArrayList.this.set(lastIdx, e);
        }

        @Override
        public void add(E e) {
            MovableArrayList.this.add(nextIdx, e);
            ++nextIdx;
            lastIdx = -1;
        }
    }
    //</editor-fold>
}
