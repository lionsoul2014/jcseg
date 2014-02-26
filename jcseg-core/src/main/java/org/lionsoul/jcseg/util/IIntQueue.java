package org.lionsoul.jcseg.util;

/**
 * char queue class base on double link
 * 	(Not thread safe)
 * 
 * @author chenxin <chenxin619315@gmail.com>
 */
public class IIntQueue 
{
	private int size;		//size of the current queue
	private Entry head;		//head of the queue
	private Entry tail;		//tail of the queue
	
	public IIntQueue()
	{
		size = 0;
		tail = new Entry(-1, null, null);
		head = new Entry(-1, null, tail);
		tail.prev = head;
	}
	
	/**
	 * append a int from the tail
	 * 
	 * @param	data
	 * @return	boolean
	 */
	public boolean enQueue( int data )
	{
		Entry o = new Entry(data, tail.prev, tail);
		tail.prev.next = o;
		tail.prev = o;
		
		//set the size
		size++;
		
		return true;
	}
	
	/**
	 * remove the node from the head
	 * 	and you should make sure the size is larger than 0 by calling size()
	 * before you invoke the method or you will just get -1
	 * 
	 * @param	int
	 */
	public int deQueue()
	{
		if ( size == 0 ) return -1;
		
		//remove the first element
		Entry o = head.next;
		head.next = o.next;
		o.next.prev = head;
		
		//bakup the data
		int v = o.data;
		size--;
		o = null;		//Let gc do its work
		
		return v;
	}
	
	/**
	 * get the size of the queue
	 * 
	 * @return int
	 */
	public int size()
	{
		return size;
	}
	
	/**
	 * innner Entry node class 
	 */
	public static class Entry
	{
		public int data;			//data of the current node
		public Entry prev;			//prev entry quote
		public Entry next;			//next entry quote
		
		public Entry( int data, Entry prev, Entry next )
		{
			this.data = data;
			this.prev = prev;
			this.next = next;
		}
	}
}
