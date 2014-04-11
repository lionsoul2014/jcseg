package org.lionsoul.jcseg.test;

import org.lionsoul.jcseg.Word;
import org.lionsoul.jcseg.core.IWord;
import org.lionsoul.jcseg.util.IHashQueue;

/**
 * IHashQueue util class test program
 * 
 * @author chenxin<chenxin619315@gmail.com>
 */

public class IHashQueueTest 
{

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		IWord[] ws = {
				new Word("你好", IWord.T_CJK_WORD),
				new Word("你好", IWord.T_BASIC_LATIN),
				new Word("研究", IWord.T_CJK_WORD),
				new Word("研究", IWord.T_BASIC_LATIN),
				new Word("测试", IWord.T_CJK_WORD),
				new Word("java",IWord.T_BASIC_LATIN),
				
				new Word("你好", IWord.T_CJK_WORD),
				new Word("研究", IWord.T_BASIC_LATIN),
				new Word("java",IWord.T_BASIC_LATIN)
		};
		
		//-------------------------------------------------
		
		IHashQueue<IWord> wordPool = new IHashQueue<IWord>();
		
		int idx = 0;
		for ( IWord w : ws )
		{
			if ( wordPool.contains(w) )
			{
				System.out.println("repeat: "+w);
			}
			else
			{
				w.setPosition(idx);
				wordPool.add(w);
				idx++;
			}
		}
		
		//--------------------------------------------
		
		while ( wordPool.size() > 0 )
		{
			System.out.println(wordPool.remove());
		}
		
		System.out.println("Done");
	}

}
