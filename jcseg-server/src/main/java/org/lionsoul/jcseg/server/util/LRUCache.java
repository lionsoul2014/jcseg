package org.lionsoul.jcseg.server.util;

import java.util.HashMap;

/**
 *  LRU cache based on HashMap
 * 
 * @author dongyado<dongyado@gmail.com>
 * */
public class LRUCache<E, T> {

    @SuppressWarnings("hiding")
    private class Entry<E, T> {
        
        public E key;
        public T value;
        public Entry<E, T> prev;
        public Entry<E, T> next;
        
        public Entry(E _key, T _val, Entry<E, T> _prev, Entry<E, T> _next)
        {
            this.key    = _key;
            this.value  = _val;
            this.prev   = _prev;
            this.next   = _next;
        }
    }
    
    private int capacity        = 32; // default capacity
    private int removePercent   = 10; // 10% of the end of the list element to be removed when list fulled
    private int length          = 0;
    private HashMap<E, Entry<E, T>> map = null;

    private Entry<E, T> head;
    private Entry<E, T> tail;
    
    public LRUCache(int capacity)
    {
        this.capacity = capacity;
        this.init();
    }
    
    
    public LRUCache(int capacity, int percentToRemove)
    {
        this.capacity      = capacity;
        this.removePercent = percentToRemove;
        this.init();
    }
    
    
    private void init() {
        this.head = new Entry<E, T>( null, null, null, null);
        this.tail = new Entry<E, T>( null, null, this.head, null);
        this.head.next = this.tail;
        
        this.map = new HashMap<E, Entry<E, T>>();
    }
    
    
    /**
     * get a element from map with specified key
     * 
     * @param  key
     * @return value
     * */
    public T get( E key )
    {
        Entry<E, T> entry = null;
        synchronized(this) {
            entry = map.get(key);
            if (map.get(key) == null)
                return null;
        
            entry.prev.next = entry.next;
            entry.next.prev = entry.prev;
            
            entry.prev = this.head;
            entry.next = this.head.next;
            this.head.next.prev = entry;
            this.head.next = entry;
        }
        
        return entry.value;
    }
    
    /**
     * set a element to list
     * 
     * @param key
     * @param value
     * */
    public void set(E key, T value) 
    {
        Entry<E, T> entry = new Entry<E, T>(key, value, null, null);
        
        synchronized(this) {
            if (map.get(key) == null) {
                
                if (this.length >= this.capacity) 
                    this.removeLeastUsedElements();

                entry.prev = this.head;
                entry.next = this.head.next;
                this.head.next.prev = entry;
                this.head.next = entry;
                
                this.length++;
                map.put(key, entry);
            } else {
                entry = map.get(key);
                
                entry.value = value;
                
                entry.prev.next = entry.next;
                entry.next.prev = entry.prev;
                
                entry.prev = this.head;
                entry.next = this.head.next;
                this.head.next.prev = entry;
                this.head.next = entry;
            }
        }
    }
    
    
    /**
     * remove a element from list
     * 
     * @param key
     * */
    public synchronized void remove(E key)
    {    
        Entry<E, T> entry = map.get(key);
        this.tail.prev = entry.prev;
        entry.prev.next = this.tail;
        map.remove(entry.key);
        this.length--;   
    }
    
    // remove least used elements
    public synchronized void removeLeastUsedElements()
    {    
        int rows = this.removePercent / 100 *  this.length;
        rows     = rows == 0 ? 1 : rows;
        
        while(rows > 0 && this.length > 0) {
            // remove the last element
            Entry<E, T> entry = this.tail.prev;
            
            this.tail.prev = entry.prev;
            entry.prev.next = this.tail;
            map.remove(entry.key);
            this.length--;
            rows--;
        }
    }
   
    /**
     * return the length of list
     * 
     * @return int
     * */
    public synchronized int  getLength(){
        return this.length;
    }
    
    /**
     * print the list 
     * 
     * @NOTE for test
     * */
    public synchronized void printList(){
        Entry<E, T> entry = this.head.next;
        
        System.out.println("\n|----- key list----|");
        while( entry != this.tail)
        {
            System.out.println(" -> " + entry.key );
            entry = entry.next;
        }
        System.out.println("|------- end --------|\n");
    }
}
