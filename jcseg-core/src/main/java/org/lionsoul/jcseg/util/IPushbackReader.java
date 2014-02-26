package org.lionsoul.jcseg.util;

import java.io.IOException;
import java.io.Reader;

/**
 * IPushBackReader based on Reader
 * Not thread safe support unlimited unread operation
 * 
 * @author chenxin <chenxin619315@gmail.com>
 */
public class IPushbackReader 
{
	//reader
	private Reader reader = null;
	
	//push buffer
	private IIntFIFO queue = null;
	
	public IPushbackReader( Reader reader )
	{
		this.reader = reader;
		queue = new IIntFIFO();
	}
	
	/**
	 * read the next int from the stream
	 * 	this will check the buffer queue first
	 * and take the first item of the buffer as the result
	 * 
	 * @return	int
	 * @throws	IOException 
	 */
	public int read() throws IOException
	{
		//check the queue first
		if ( queue.size() > 0 ) return queue.deQueue();
		
		//load from the normal reader
		return reader.read();
	}
	
	/**
	 * read the specified block from the stream
	 * @see #read()
	 * 
	 * @return int
	 * @throws IOException 
	 */
	public int read( char[] cbuf, int off, int len ) throws IOException
	{
		//check the buffer queue
		int size = queue.size();
		if ( size > 0 )
		{
			//TODO
			//int num = size <= len ? size : len;
			//System.arraycopy(src, srcPos, dest, destPos, length)
			throw new IOException("Method not implemented yet");
		}
		
		return reader.read(cbuf, off, len);
	}
	
	/**
	 * unread the speicfied data to the stream
	 * 	push the data back to the queue in fact, you know
	 */
	public void unread( int data )
	{
		queue.enQueue(data);
	}
	
	/**
	 * get the buffer size - the number of buffered data
	 * 
	 * @return	int
	 */
	public int getQueueSize()
	{
		return queue.size();
	}
	
	/**
	 * unread a block from a char array to the stream
	 * 
	 * @see	#unread(int)
	 */
	public void unread( char[] cbuf, int off, int len )
	{
		for ( int i = 0; i < len; i++ )
			queue.enQueue(cbuf[off+i]);
	}
}
