package com.vasyutinskiy.list;


import com.sun.org.apache.xpath.internal.SourceTree;

import java.util.*;

public class UnrolledLinkedList<E>
        extends AbstractSequentialList<E>
        implements List<E>, Deque<E>, Cloneable, java.io.Serializable {

    transient int size = 0;

    /**
     * Pointer to first node.
     * Invariant: (first == null && last == null) ||
     * (first.prev == null && first.item != null)
     */
    transient Node<E> first;

    /**
     * Pointer to last node.
     * Invariant: (first == null && last == null) ||
     * (last.next == null && last.item != null)
     */
    transient Node<E> last;

    /**
     * Constructs an empty list.
     */
    public UnrolledLinkedList() {
        first = new Node<E>();
        last = new Node<E>();
        first.next = last;
        last.prev = first;
    }

    /**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * @param c the collection whose elements are to be placed into this list
     * @throws NullPointerException if the specified collection is null
     */
    public UnrolledLinkedList(Collection<? extends E> c) {
        this();
        addAll(c);
    }

    /**
     * Links e as last element.
     */
    void linkLast(E e) {
        final Node<E> l = last;
        if (l != null) {
            l.addToTail(e);
        } else {
            final Node<E> newNode = new Node<E>(null, e, null);
            first = newNode;
            last = newNode;
        }
        size++;
        modCount++;
    }

    /**
     * Unlinks non-null first node f.
     */
    private E unlinkFirst(Node<E> f) {
        E e = f.deleteFirst();
        size--;
        modCount++;
        return e;
    }

    /**
     * Unlinks non-null last node l.
     */
    private E unlinkLast(Node<E> l) {
        E e = l.deleteLast();
        size--;
        modCount++;
        return e;
    }

    /**
     * Returns the first element in this list.
     *
     * @return the first element in this list
     * @throws NoSuchElementException if this list is empty
     */
    public E getFirst() {
        final Node<E> f = first;
        if (f == null)
            throw new NoSuchElementException();
        return f.items[0];
    }

    /**
     * Returns the last element in this list.
     *
     * @return the last element in this list
     * @throws NoSuchElementException if this list is empty
     */
    public E getLast() {
        final Node<E> l = last;
        if (l == null)
            throw new NoSuchElementException();
        return l.items[l.numElements - 1];
    }

    public E removeFirst() {
        final Node<E> f = first;
        if (f == null)
            throw new NoSuchElementException();
        return unlinkFirst(f);
    }

    public E removeLast() {
        final Node<E> l = last;
        if (l == null)
            throw new NoSuchElementException();
        return unlinkLast(l);
    }

    public void addFirst(E e) {
        final Node<E> f = first;
        if (f != null) {
            f.addToHead(e);
        } else {
            final Node<E> newNode = new Node<E>(null, e, null);
            first = newNode;
            last = newNode;
        }
        size++;
        modCount++;
    }

    public void addLast(E e) {
        linkLast(e);
    }

    /**
     * Returns {@code true} if this list contains the specified element.
     * More formally, returns {@code true} if and only if this list contains
     * at least one element {@code e} such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
     *
     * @param o element whose presence in this list is to be tested
     * @return {@code true} if this list contains the specified element
     */
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
    public int size() {
        return size;
    }

    /**
     * Appends the specified element to the end of this list.
     * <p/>
     * <p>This method is equivalent to {@link #addLast}.
     *
     * @param e element to be appended to this list
     * @return {@code true} (as specified by {@link Collection#add})
     */
    public boolean add(E e) {
        linkLast(e);
        return true;
    }

    /**
     * Removes the first occurrence of the specified element from this list,
     * if it is present.  If this list does not contain the element, it is
     * unchanged.  More formally, removes the element with the lowest index
     * {@code i} such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>
     * (if such an element exists).  Returns {@code true} if this list
     * contained the specified element (or equivalently, if this list
     * changed as a result of the call).
     *
     * @param o element to be removed from this list, if present
     * @return {@code true} if this list contained the specified element
     */
    public boolean remove(Object o) {
        if (o == null) {
            for (Node<E> x = first; x != null; x = x.next) {
                for (int i = 0; i < x.numElements; i++) {
                    E item = x.items[i];
                    if (item == null) {
                        x.deleteElementWithIndex(i);
                        return true;
                    }
                }
            }
        } else {
            for (Node<E> x = first; x != null; x = x.next) {
                for (int i = 0; i < x.numElements; i++) {
                    E item = x.items[i];
                    if (o.equals(item)) {
                        x.deleteElementWithIndex(i);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Appends all of the elements in the specified collection to the end of
     * this list, in the order that they are returned by the specified
     * collection's iterator.  The behavior of this operation is undefined if
     * the specified collection is modified while the operation is in
     * progress.  (Note that this will occur if the specified collection is
     * this list, and it's nonempty.)
     *
     * @param c collection containing elements to be added to this list
     * @return {@code true} if this list changed as a result of the call
     * @throws NullPointerException if the specified collection is null
     */
    public boolean addAll(Collection<? extends E> c) {
        return addAll(size, c);
    }

    /**
     * Inserts all of the elements in the specified collection into this
     * list, starting at the specified position.  Shifts the element
     * currently at that position (if any) and any subsequent elements to
     * the right (increases their indices).  The new elements will appear
     * in the list in the order that they are returned by the
     * specified collection's iterator.
     *
     * @param index index at which to insert the first element
     *              from the specified collection
     * @param c     collection containing elements to be added to this list
     * @return {@code true} if this list changed as a result of the call
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws NullPointerException      if the specified collection is null
     */
    public boolean addAll(int index, Collection<? extends E> c) { //ToDo change for nel linked list for array creation
        checkPositionIndex(index);

        Object[] a = c.toArray();
        int numNew = a.length;
        if (numNew == 0)
            return false;
        Node<E> ins;
        if (index == size) {
            ins = last;
        } else {
            ins = node(index);
        }

        for (Object o : a) {
            @SuppressWarnings("unchecked") E e = (E) o;
            ins.addOnIndex(ins.elementOffset, e);
            ins = node(index++);
        }

        size += numNew;
        modCount++;
        return true;
    }

    /**
     * Removes all of the elements from this list.
     * The list will be empty after this call returns.
     */
    public void clear() {
        // Clearing all of the links between nodes is "unnecessary", but:
        // - helps a generational GC if the discarded nodes inhabit
        //   more than one generation
        // - is sure to free memory even if there is a reachable Iterator
        for (Node<E> x = first; x != null; ) {
            Node<E> next = x.next;
            x.items = null;
            x.next = null;
            x.prev = null;
            x = next;
        }
        first = last = null;
        size = 0;
        modCount++;
    }


    // Positional Access Operations

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E get(int index) {
        checkElementIndex(index);
        Node<E> node = node(index);
        return node.items[node.elementOffset];
    }

    /**
     * Replaces the element at the specified position in this list with the
     * specified element.
     *
     * @param index   index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E set(int index, E element) {
        checkElementIndex(index);
        Node<E> x = node(index);
        E oldVal = x.items[x.elementOffset];
        x.items[x.elementOffset] = element;
        return oldVal;
    }

    /**
     * Inserts the specified element at the specified position in this list.
     * Shifts the element currently at that position (if any) and any
     * subsequent elements to the right (adds one to their indices).
     *
     * @param index   index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public void add(int index, E element) {
        checkPositionIndex(index);

        if (index == size)
            linkLast(element);
        else {
            Node<E> node = node(index);
            node.addOnIndex(node.elementOffset, element);
        }
    }

    /**
     * Removes the element at the specified position in this list.  Shifts any
     * subsequent elements to the left (subtracts one from their indices).
     * Returns the element that was removed from the list.
     *
     * @param index the index of the element to be removed
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E remove(int index) {
        checkElementIndex(index);
        Node<E> node = node(index);
        return node.deleteElementWithIndex(node.elementOffset);
    }

    /**
     * Tells if the argument is the index of an existing element.
     */
    private boolean isElementIndex(int index) {
        return index >= 0 && index < size;
    }

    /**
     * Tells if the argument is the index of a valid position for an
     * iterator or an add operation.
     */
    private boolean isPositionIndex(int index) {
        return index >= 0 && index <= size;
    }

    /**
     * Constructs an IndexOutOfBoundsException detail message.
     * Of the many possible refactorings of the error handling code,
     * this "outlining" performs best with both server and client VMs.
     */
    private String outOfBoundsMsg(int index) {
        return "Index: " + index + ", Size: " + size;
    }

    private void checkElementIndex(int index) {
        if (!isElementIndex(index))
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    private void checkPositionIndex(int index) {
        if (!isPositionIndex(index))
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**
     * Returns the (non-null) Node at the specified element index.
     */
    Node<E> node(int index) {
        // assert isElementIndex(index);
        if (index < (size >> 1)) {
            Node<E> x = first;
            int i = 0;
            while (i < index) {
                i += x.numElements;
                x = x.next;
            }
            x.elementOffset = i - index;
            return x;
        } else {
            Node<E> x = last;
            int i = size - x.numElements;
            while (i > index) {
                x = x.prev;
                i -= x.numElements;
            }
            x.elementOffset = index - i;
            return x;

        }
//        return first;
    }


    // Search Operations

    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the lowest index {@code i} such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     *
     * @param o element to search for
     * @return the index of the first occurrence of the specified element in
     *         this list, or -1 if this list does not contain the element
     */
    public int indexOf(Object o) {
        int index = 0;
        if (o == null) {
            for (Node<E> x = first; x != null; x = x.next) {
                for (int i = 0; i < x.numElements; i++) {
                    if (x.items[i] == null)
                        return index;
                    index++;
                }
            }
        } else {
            for (Node<E> x = first; x != null; x = x.next) {
                for (int i = 0; i < x.numElements; i++) {
                    if (o.equals(x.items[i]))
                        return index;
                    index++;
                }
            }
        }
        return -1;
    }

    /**
     * Returns the index of the last occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the highest index {@code i} such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     *
     * @param o element to search for
     * @return the index of the last occurrence of the specified element in
     *         this list, or -1 if this list does not contain the element
     */
    public int lastIndexOf(Object o) {
        int index = size;
        if (o == null) {
            for (Node<E> x = last; x != null; x = x.prev) {
                for (int i = x.numElements - 1; i >= 0; i--) {
                    index--;
                    if (x.items[i] == null)
                        return index;
                }
            }
        } else {
            for (Node<E> x = last; x != null; x = x.prev) {
                for (int i = x.numElements - 1; i >= 0; i--) {
                    index--;
                    if (o.equals(x.items[i]))
                        return index;
                }
            }
        }
        return -1;
    }

    // Queue operations.

    /**
     * Retrieves, but does not remove, the head (first element) of this list.
     *
     * @return the head of this list, or {@code null} if this list is empty
     * @since 1.5
     */
    public E peek() {
        final Node<E> f = first;
        return (f == null) ? null : f.items[0];
    }

    /**
     * Retrieves, but does not remove, the head (first element) of this list.
     *
     * @return the head of this list
     * @throws NoSuchElementException if this list is empty
     * @since 1.5
     */
    public E element() {
        return getFirst();
    }

    /**
     * Retrieves and removes the head (first element) of this list.
     *
     * @return the head of this list, or {@code null} if this list is empty
     * @since 1.5
     */
    public E poll() {
        final Node<E> f = first;
        return (f == null) ? null : unlinkFirst(f);
    }

    /**
     * Retrieves and removes the head (first element) of this list.
     *
     * @return the head of this list
     * @throws NoSuchElementException if this list is empty
     * @since 1.5
     */
    public E remove() {
        return removeFirst();
    }

    /**
     * Adds the specified element as the tail (last element) of this list.
     *
     * @param e the element to add
     * @return {@code true} (as specified by {@link Queue#offer})
     * @since 1.5
     */
    public boolean offer(E e) {
        return add(e);
    }

    // Deque operations

    /**
     * Inserts the specified element at the front of this list.
     *
     * @param e the element to insert
     * @return {@code true} (as specified by {@link Deque#offerFirst})
     * @since 1.6
     */
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    /**
     * Inserts the specified element at the end of this list.
     *
     * @param e the element to insert
     * @return {@code true} (as specified by {@link Deque#offerLast})
     * @since 1.6
     */
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    /**
     * Retrieves, but does not remove, the first element of this list,
     * or returns {@code null} if this list is empty.
     *
     * @return the first element of this list, or {@code null}
     *         if this list is empty
     * @since 1.6
     */
    public E peekFirst() {
        final Node<E> f = first;
        return (f == null) ? null : f.items[0];
    }

    /**
     * Retrieves, but does not remove, the last element of this list,
     * or returns {@code null} if this list is empty.
     *
     * @return the last element of this list, or {@code null}
     *         if this list is empty
     * @since 1.6
     */
    public E peekLast() {
        final Node<E> l = last;
        return (l == null) ? null : l.items[l.numElements - 1];
    }

    /**
     * Retrieves and removes the first element of this list,
     * or returns {@code null} if this list is empty.
     *
     * @return the first element of this list, or {@code null} if
     *         this list is empty
     * @since 1.6
     */
    public E pollFirst() {
        final Node<E> f = first;
        return (f == null) ? null : unlinkFirst(f);
    }

    /**
     * Retrieves and removes the last element of this list,
     * or returns {@code null} if this list is empty.
     *
     * @return the last element of this list, or {@code null} if
     *         this list is empty
     * @since 1.6
     */
    public E pollLast() {
        final Node<E> l = last;
        return (l == null) ? null : unlinkLast(l);
    }

    /**
     * Pushes an element onto the stack represented by this list.  In other
     * words, inserts the element at the front of this list.
     * <p/>
     * <p>This method is equivalent to {@link #addFirst}.
     *
     * @param e the element to push
     * @since 1.6
     */
    public void push(E e) {
        addFirst(e);
    }

    /**
     * Pops an element from the stack represented by this list.  In other
     * words, removes and returns the first element of this list.
     * <p/>
     * <p>This method is equivalent to {@link #removeFirst()}.
     *
     * @return the element at the front of this list (which is the top
     *         of the stack represented by this list)
     * @throws NoSuchElementException if this list is empty
     * @since 1.6
     */
    public E pop() {
        return removeFirst();
    }

    /**
     * Removes the first occurrence of the specified element in this
     * list (when traversing the list from head to tail).  If the list
     * does not contain the element, it is unchanged.
     *
     * @param o element to be removed from this list, if present
     * @return {@code true} if the list contained the specified element
     * @since 1.6
     */
    public boolean removeFirstOccurrence(Object o) {
        return remove(o);
    }

    /**
     * Removes the last occurrence of the specified element in this
     * list (when traversing the list from head to tail).  If the list
     * does not contain the element, it is unchanged.
     *
     * @param o element to be removed from this list, if present
     * @return {@code true} if the list contained the specified element
     * @since 1.6
     */
    public boolean removeLastOccurrence(Object o) {
        if (o == null) {
            for (Node<E> x = last; x != null; x = x.prev) {
                for (int i = x.numElements - 1; i >= 0; i--) {
                    if (x.items[i] == null) {
                        x.deleteElementWithIndex(i);
                        return true;
                    }
                }
            }
        } else {
            for (Node<E> x = last; x != null; x = x.prev) {
                for (int i = x.numElements - 1; i >= 0; i--) {
                    if (o.equals(x.items[i])) {
                        x.deleteElementWithIndex(i);
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public ListIterator<E> listIterator(int index) {
        checkPositionIndex(index);
        return new ListItr(index);
    }

    private class ListItr implements ListIterator<E> {
        private Node<E> next;
        private int nextIndex;
        private int expectedModCount = modCount;

        ListItr(int index) {
            next = (index == size) ? null : node(index);
            nextIndex = index;
        }

        public boolean hasNext() {
            return nextIndex < size;
        }

        public E next() {
            checkForComodification();
            if (!hasNext())
                throw new NoSuchElementException();
            if (next.elementOffset == next.numElements) {
                next = next.next;
                next.elementOffset = 0;
            }
            nextIndex++;
            return next.items[next.elementOffset++];
        }

        public boolean hasPrevious() {
            return nextIndex > 0;
        }

        public E previous() {
            checkForComodification();
            if (!hasPrevious())
                throw new NoSuchElementException();

            if (next.elementOffset < 0) {
                next = next.prev;
                if (next == null) {
                    next = last;
                }
                next.elementOffset = next.numElements;
            }
            nextIndex--;
            return next.items[next.elementOffset--];
        }

        public int nextIndex() {
            return nextIndex;
        }

        public int previousIndex() {
            return nextIndex - 1;
        }

        public void remove() {
            checkForComodification();
            if (next == null)
                throw new IllegalStateException();

            next.deleteElementWithIndex(next.elementOffset);
            Node<E> lastNext = node(nextIndex);
            nextIndex--;
            next = lastNext;
            expectedModCount++;
        }

        public void set(E e) {
            if (next == null)
                throw new IllegalStateException();
            checkForComodification();
            next.items[next.elementOffset] = e;
        }

        public void add(E e) {
            checkForComodification();
            if (next == null)
                linkLast(e);
            else
                next.addOnIndex(next.elementOffset, e);
            nextIndex++;
            expectedModCount++;
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    private static class Node<E> {
        public static final int NODE_CAPACITY = 32;
        public static final int HALF_NODE_CAPACITY = NODE_CAPACITY / 2;
        public static final Object[] NULL_ARRAY = new Object[HALF_NODE_CAPACITY];
        int numElements = 0;
        E[] items;
        Node<E> next;
        Node<E> prev;
        int elementOffset = 0;

        Node(Node<E> prev, E element, Node<E> next) {
            this.items = (E[]) new Object[NODE_CAPACITY];
            this.items[0] = element;
            numElements++;
            this.next = next;
            this.prev = prev;
        }

        private Node() {
            this.items = (E[]) new Object[NODE_CAPACITY];
        }

        void addToHead(E e) {
            if (numElements == NODE_CAPACITY) {
                moveHalfToNext();
            }
            System.arraycopy(items, 0, items, 1, numElements);
            items[numElements] = e;
            numElements++;
        }

        void addToTail(E e) {
            if (numElements == NODE_CAPACITY) {
                moveHalfToPrev();

            }
            items[numElements] = e;
            numElements++;
        }

        void addOnIndex(int index, E e) {

            if (numElements == NODE_CAPACITY) {
                moveHalfToNext();
            }
            if (index > NODE_CAPACITY) {
                next.addOnIndex(index, e);
            } else {
                System.arraycopy(items, index, items, index + 1, numElements - index);
                numElements++;
            }
        }

        private void moveHalfToNext() {
            Node<E> newNode;
            if (next != null && next.numElements < HALF_NODE_CAPACITY) {
                newNode = next;
            } else {
                newNode = new Node<E>();
                newNode.prev = this;
                newNode.next = this.next;
                next.prev = newNode;
                next = newNode;

            }

            System.arraycopy(newNode.items, 0, newNode.items, HALF_NODE_CAPACITY, newNode.numElements);
            System.arraycopy(items, HALF_NODE_CAPACITY, newNode.items, 0, HALF_NODE_CAPACITY);
            System.arraycopy(NULL_ARRAY, 0, items, HALF_NODE_CAPACITY, HALF_NODE_CAPACITY);

            newNode.numElements += HALF_NODE_CAPACITY;
            numElements = HALF_NODE_CAPACITY;
        }

        private void moveHalfToPrev() {
            Node<E> newNode;
            if (prev != null && prev.numElements <= HALF_NODE_CAPACITY) {
                newNode = prev;
            } else {
                newNode = new Node<E>();
                newNode.next = this;
                newNode.prev = prev;
                prev.next = newNode;
                prev = newNode;
            }

            System.arraycopy(items, 0, newNode.items, newNode.numElements, HALF_NODE_CAPACITY);
            System.arraycopy(items, HALF_NODE_CAPACITY, items, 0, HALF_NODE_CAPACITY);
            System.arraycopy(NULL_ARRAY, 0, items, HALF_NODE_CAPACITY, HALF_NODE_CAPACITY);
            newNode.numElements += HALF_NODE_CAPACITY;
            this.numElements = HALF_NODE_CAPACITY;
        }

//        private void rearrangeElementsWithAdjacent() {
//            E[] temp = (E[]) new Object[3 * NODE_CAPACITY];
//            int num = 0;
//            if (next != null) {
//                System.arraycopy(next.items, 0, temp, 0, next.numElements);
//                num = next.numElements;
//            }
//            System.arraycopy(items, 0, temp, num, numElements);
//            num += numElements;
//            Node<E> p = null;
//            if (prev != null) {
//                System.arraycopy(prev.items, 0, temp, num, prev.numElements);
//                num += prev.numElements;
//                p = prev.prev;
//                prev.prev = null;
//            }
//            int i = 0;
//            while (num > 0) {
//                Node<E> newNode = new Node<E>();
//                System.arraycopy(temp, i, newNode.items, 0, NODE_CAPACITY);
//                i += NODE_CAPACITY;
//                num -= NODE_CAPACITY;
//                newNode.prev = p;
//                if (p != null)
//                    p.next = newNode;
//                p = newNode;
//            }
//            if (p != null && next != null) {
//                p.next = next.next;
//                if (next.next != null) {
//                    next.next.prev = p;
//                    next.next = null;
//                }
//            }
//        }

        private void rearrangeElementsWithAdjacent() {
            if (prev == null || next == null)
                return;

            int num = numElements + prev.numElements + next.numElements;

            if (num < 2 * NODE_CAPACITY) {
                int length = (NODE_CAPACITY - prev.numElements) > numElements ? numElements : (NODE_CAPACITY - prev.numElements);
                System.arraycopy(items, 0, prev.items, prev.numElements, length);
                prev.numElements += length;

                length = numElements - length;
                if (length == 0)
                    return;
                System.arraycopy(next.items, 0, next.items, next.numElements, length);
                System.arraycopy(items, numElements - length, next.items, 0, length);
                next.numElements += length;

                prev.next = next;
                next.prev = prev;
                this.items = null;
            }
        }

        E deleteFirst() {
            E e = items[0];
            numElements--;
            System.arraycopy(items, 1, items, 0, numElements);
            if (numElements == HALF_NODE_CAPACITY) {
                rearrangeElementsWithAdjacent();
            }
            return e;
        }

        E deleteLast() {
            E e = items[numElements - 1];
            items[numElements - 1] = null;
            numElements--;
            if (numElements == HALF_NODE_CAPACITY) {
                rearrangeElementsWithAdjacent();
            }
            return e;
        }

        E deleteElementWithIndex(int i) {
            E e = items[i];
            numElements--;
            System.arraycopy(items, i, items, i - 1, numElements - (i));
            if (numElements == HALF_NODE_CAPACITY) {
                rearrangeElementsWithAdjacent();
            }
            return e;
        }
    }

    /**
     * @since 1.6
     */
    public Iterator<E> descendingIterator() {
        return new DescendingIterator();
    }

    /**
     * Adapter to provide descending iterators via ListItr.previous
     */
    private class DescendingIterator implements Iterator<E> {
        private final ListItr itr = new ListItr(size());

        public boolean hasNext() {
            return itr.hasPrevious();
        }

        public E next() {
            return itr.previous();
        }

        public void remove() {
            itr.remove();
        }
    }

    @SuppressWarnings("unchecked")
    private UnrolledLinkedList<E> superClone() {
        try {
            return (UnrolledLinkedList<E>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    /**
     * Returns a shallow copy of this {@code LinkedList}. (The elements
     * themselves are not cloned.)
     *
     * @return a shallow copy of this {@code LinkedList} instance
     */
    public Object clone() {
        UnrolledLinkedList<E> clone = superClone();

        // Put clone into "virgin" state
        clone.first = clone.last = null;
        clone.size = 0;
        clone.modCount = 0;

        // Initialize clone with our elements
        for (Node<E> x = first; x != null; x = x.next)
            clone.addAll(Arrays.asList(x.items).subList(0, x.numElements));

        return clone;
    }

    /**
     * Returns an array containing all of the elements in this list
     * in proper sequence (from first to last element).
     * <p/>
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this list.  (In other words, this method must allocate
     * a new array).  The caller is thus free to modify the returned array.
     * <p/>
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     *
     * @return an array containing all of the elements in this list
     *         in proper sequence
     */
    public Object[] toArray() {
        Object[] result = new Object[size];
        int i = 0;
        for (Node<E> x = first; x != null; x = x.next) {
            System.arraycopy(x.items, 0, result, i, x.numElements);
            i += x.numElements;
        }

        return result;
    }

    /**
     * Returns an array containing all of the elements in this list in
     * proper sequence (from first to last element); the runtime type of
     * the returned array is that of the specified array.  If the list fits
     * in the specified array, it is returned therein.  Otherwise, a new
     * array is allocated with the runtime type of the specified array and
     * the size of this list.
     * <p/>
     * <p>If the list fits in the specified array with room to spare (i.e.,
     * the array has more elements than the list), the element in the array
     * immediately following the end of the list is set to {@code null}.
     * (This is useful in determining the length of the list <i>only</i> if
     * the caller knows that the list does not contain any null elements.)
     * <p/>
     * <p>Like the {@link #toArray()} method, this method acts as bridge between
     * array-based and collection-based APIs.  Further, this method allows
     * precise control over the runtime type of the output array, and may,
     * under certain circumstances, be used to save allocation costs.
     * <p/>
     * <p>Suppose {@code x} is a list known to contain only strings.
     * The following code can be used to dump the list into a newly
     * allocated array of {@code String}:
     * <p/>
     * <pre>
     *     String[] y = x.toArray(new String[0]);</pre>
     *
     * Note that {@code toArray(new Object[0])} is identical in function to
     * {@code toArray()}.
     *
     * @param a the array into which the elements of the list are to
     *          be stored, if it is big enough; otherwise, a new array of the
     *          same runtime type is allocated for this purpose.
     * @return an array containing the elements of the list
     * @throws ArrayStoreException  if the runtime type of the specified array
     *                              is not a supertype of the runtime type of every element in
     *                              this list
     * @throws NullPointerException if the specified array is null
     */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size)
            a = (T[]) java.lang.reflect.Array.newInstance(
                    a.getClass().getComponentType(), size);
        int i = 0;
        Object[] result = a;
        for (Node<E> x = first; x != null; x = x.next) {
            System.arraycopy(x.items, 0, result, i, x.numElements);
            i += x.numElements;
        }

        if (a.length > size)
            a[size] = null;

        return a;
    }

    private static final long serialVersionUID = 876323262645176354L;

    /**
     * Saves the state of this {@code LinkedList} instance to a stream
     * (that is, serializes it).
     *
     * @serialData The size of the list (the number of elements it
     * contains) is emitted (int), followed by all of its
     * elements (each an Object) in the proper order.
     */
    private void writeObject(java.io.ObjectOutputStream s)
            throws java.io.IOException {
        // Write out any hidden serialization magic
        s.defaultWriteObject();

        // Write out size
        s.writeInt(size);

        // Write out all elements in the proper order.
        for (Node<E> x = first; x != null; x = x.next)
            for (int i = 0; i < x.numElements; i++)
                s.writeObject(x.items[i]);
    }

    /**
     * Reconstitutes this {@code LinkedList} instance from a stream
     * (that is, deserializes it).
     */
    @SuppressWarnings("unchecked")
    private void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
        // Read in any hidden serialization magic
        s.defaultReadObject();

        // Read in size
        int size = s.readInt();

        // Read in all elements in the proper order.
        for (int i = 0; i < size; i++)
            linkLast((E) s.readObject());
    }

    public static void main(String[] args) throws InterruptedException {
        UnrolledLinkedList<Integer> unrolledList = new UnrolledLinkedList<Integer>();
//        unrolledList.add(1);
//        unrolledList.add(2);
//        unrolledList.add(3);
//        unrolledList.add(4);
//        unrolledList.add(5);
//        unrolledList.add(6);
//        unrolledList.add(7);
//        unrolledList.add(8);
//        unrolledList.add(9);
//        unrolledList.add(10);
//        unrolledList.add(11);
//        unrolledList.add(12);
//        unrolledList.add(13);
//        unrolledList.add(14);
//        unrolledList.add(15);
//
//        for (Integer integer : unrolledList) {
//            System.out.println(integer);
//        }

        LinkedList<Integer> linkedList = new LinkedList<Integer>();
        ArrayList<Integer> arrayList = new ArrayList<Integer>();

        Random rand = new Random(42);
        for (int i = 0; i < 10000; i++) {
            unrolledList.add(rand.nextInt());
            linkedList.add(rand.nextInt());
            arrayList.add(rand.nextInt());
        }
//        Thread.sleep(30000);
        Thread.sleep(3000);
        System.out.println("Start");
        long time = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            unrolledList.add(rand.nextInt());
        }
        System.out.println("Unrolled add " + (System.nanoTime() - time));
        time = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            linkedList.add(rand.nextInt());
        }
        System.out.println("  Linked add " + (System.nanoTime() - time));
        time = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            arrayList.add(rand.nextInt());
        }
        System.out.println("  Array add " + (System.nanoTime() - time));

        System.out.println("Start get");

        time = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            linkedList.get(rand.nextInt(110000));
        }
        System.out.println("  Linked get " + (System.nanoTime() - time));
        time = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            arrayList.get(rand.nextInt(110000));
        }
        System.out.println("   Array get " + (System.nanoTime() - time));
        time = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            unrolledList.get(rand.nextInt(110000));
        }
        System.out.println("Unrolled get " + (System.nanoTime() - time));

        System.out.println("Start iterate");
        Integer acc;
        acc = 0;
        for (Integer integer : linkedList) {
            acc += integer;
        }
        time = System.nanoTime();
        for (Integer integer : linkedList) {
            acc += integer;
        }
        System.out.println("  Linked it " + (System.nanoTime() - time));
        for (Integer integer : arrayList) {
            acc += integer;
        }
        acc = 0;
        time = System.nanoTime();
        for (Integer integer : arrayList) {
            acc += integer;
        }
        System.out.println("   Array it " + (System.nanoTime() - time));
        acc = 0;
        for (Integer integer : unrolledList) {
            acc += integer;
        }
        time = System.nanoTime();
        for (Integer integer : unrolledList) {
            acc += integer;
        }
        System.out.println("Unrolled it " + (System.nanoTime() - time));

        System.out.println("Start size");
        putOutTheGarbage();
        Thread.sleep(1000);
        System.out.println("Unrolled size " + getObjectSize(100000));
        putOutTheGarbage();
        Thread.sleep(1000);
        System.out.println("Linked size " + getLinkedSize(100000));
        putOutTheGarbage();
        Thread.sleep(1000);
        System.out.println("Array size " + getArraySize(100000));
    }


    public static long getObjectSize(int size) {
        long result = 0;

        //this array will simply hold a bunch of references, such that
        //the objects cannot be garbage-collected
        Object[] objects = new Object[fSAMPLE_SIZE];

        //build a bunch of identical objects

        Object throwAway = new UnrolledLinkedList<Integer>();

        long startMemoryUse = getMemoryUse();
        Random rand = new Random(42);
        for (int idx = 0; idx < objects.length; ++idx) {
            UnrolledLinkedList<Integer> list = new UnrolledLinkedList<Integer>();
            for (int i = 0; i < size; i++) {
                list.add(rand.nextInt());
            }
            objects[idx] = list;
        }
        long endMemoryUse = getMemoryUse();

        float approximateSize = (endMemoryUse - startMemoryUse) / fSAMPLE_SIZE;
        result = Math.round(approximateSize);
        return result;
    }

     public static long getLinkedSize(int size) {
        long result = 0;

        //this array will simply hold a bunch of references, such that
        //the objects cannot be garbage-collected
        Object[] objects = new Object[fSAMPLE_SIZE];

        //build a bunch of identical objects

        Object throwAway = new LinkedList<Integer>();

        long startMemoryUse = getMemoryUse();
        Random rand = new Random(42);
        for (int idx = 0; idx < objects.length; ++idx) {
            LinkedList<Integer> list = new LinkedList<Integer>();
            for (int i = 0; i < size; i++) {
                list.add(rand.nextInt());
            }
            objects[idx] = list;
        }
        long endMemoryUse = getMemoryUse();

        float approximateSize = (endMemoryUse - startMemoryUse) / fSAMPLE_SIZE;
        result = Math.round(approximateSize);
        return result;
    }

    public static long getArraySize(int size) {
        long result = 0;

        //this array will simply hold a bunch of references, such that
        //the objects cannot be garbage-collected
        Object[] objects = new Object[fSAMPLE_SIZE];

        //build a bunch of identical objects

        Object throwAway = new ArrayList<Integer>();

        long startMemoryUse = getMemoryUse();
        Random rand = new Random(42);
        for (int idx = 0; idx < objects.length; ++idx) {
            ArrayList<Integer> list = new ArrayList<Integer>();
            for (int i = 0; i < size; i++) {
                list.add(rand.nextInt());
            }
            objects[idx] = list;
        }
        long endMemoryUse = getMemoryUse();

        float approximateSize = (endMemoryUse - startMemoryUse) / fSAMPLE_SIZE;
        result = Math.round(approximateSize);
        return result;
    }

    // PRIVATE //
    private static int fSAMPLE_SIZE = 1000;
    private static long fSLEEP_INTERVAL = 100;

    private static long getMemoryUse() {
        putOutTheGarbage();
        long totalMemory = Runtime.getRuntime().totalMemory();

        putOutTheGarbage();
        long freeMemory = Runtime.getRuntime().freeMemory();

        return (totalMemory - freeMemory);
    }

    private static void putOutTheGarbage() {
        collectGarbage();
        collectGarbage();
    }

    private static void collectGarbage() {
        try {
            System.gc();
            Thread.currentThread().sleep(fSLEEP_INTERVAL);
            System.runFinalization();
            Thread.currentThread().sleep(fSLEEP_INTERVAL);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}

