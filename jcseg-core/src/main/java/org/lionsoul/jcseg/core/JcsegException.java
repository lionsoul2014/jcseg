package org.lionsoul.jcseg.core;

/**
 * JCSeg exception class
 * 
 * @author	chenxin<chenxin619315@gmail.com>
 */
public class JcsegException extends Exception {

	private static final long serialVersionUID = 4495714680349884838L;
	
	public JcsegException( String info ) {
		super(info);
	}
	
	public JcsegException( Throwable res ) {
		super(res);
	}
	
	public JcsegException( String info, Throwable res ) {
		super(info, res);
	}

}
