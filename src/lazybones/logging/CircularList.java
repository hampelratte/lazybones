/* $Id: CircularList.java,v 1.3 2011-01-18 13:13:53 hampelratte Exp $
 * 
 * Copyright (c) Henrik Niehaus & Lazy Bones development team
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the project (Lazy Bones) nor the names of its 
 *    contributors may be used to endorse or promote products derived from this 
 *    software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package lazybones.logging;

import java.util.Iterator;


public class CircularList<T> implements Iterable<T>, Iterator<T> {
    /** Points to the start of the list */
    private Node start;
    
    /** Points to the next free node */
    private Node current;
    
    /** Holds the current Node for iteration */
    private Node iterPos;
    
    private boolean firstRotation = true;
    
    public CircularList(int capacity) {
        
        start = new Node(null, null, null);
        current = start;
        
        // create a link list
        for (int i = 0; i < capacity-1; i++) {
            Node next = new Node(current, null, null);
            current.setNext(next);
            current = next;
        }
        
        // link the last node to first, so we get an circular list
        current.setNext(start);
        start.setPrev(current);
        
        // reset the current node to the start
        current = start;
    }
    
    public void add(T data) {
        current.setData(data);
        if(current == start) {
            if(firstRotation) {
                firstRotation = false;
            } else {
                start = current.getNext();
            }
        }
        current = current.getNext();
    }
    
    private class Node {
        private T data;
        private Node prev;
        private Node next;
        
        public Node(Node prev, Node next, T data) {
            this.prev = prev;
            this.next = next;
            this.data = data;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        @SuppressWarnings("unused")
        public Node getPrev() {
            return prev;
        }

        public void setPrev(Node prev) {
            this.prev = prev;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }
    }

    public Iterator<T> iterator() {
        iterPos = start;
        firstRotation = true;
        return this;
    }

    public boolean hasNext() {
        if(iterPos == start) {
            if(firstRotation) {
                firstRotation = false;
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public T next() {
        Node temp = iterPos;
        iterPos = temp.getNext();
        return temp.getData();
    }

    public void remove() {
        throw new RuntimeException("remove() is not implemented");
    }
    
    public static void main(String[] args) {
        final int size = 5;
        CircularList<String> list = new CircularList<String>(size);
        for (int i = 0; i < size+20; i++) {
            list.add(Integer.toString(i));
        }
        
        for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
            String s = iterator.next();
            System.out.println(s);
        }
    }
}
