package org.lionsoul.jcseg.nlp.impl;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lionsoul.jcseg.core.ISegment;
import org.lionsoul.jcseg.core.IWord;
import org.lionsoul.jcseg.nlp.KeyphraseExtractor;
import org.lionsoul.jcseg.util.IStringBuffer;

/**
 * document key phrase extractor base on textRank algorithm
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class TextRankKeyphraseExtractor extends KeyphraseExtractor
{
	//page rank damping factor
	public static final float D = 0.85F;
	
	//default keywords number
	protected int keywordsNum = 10;
	
	//max iterate times
	protected int maxIterateNum = 201;
	
	//window size
	protected int windowSize = 5;
	
	/**
	 * auto append the words with a length over the specifield value
	 * as a phrase
	*/
	protected int autoMinLength = -1;
	
	/**
	 * max phrase length
	*/
	protected int maxPhraseLength = 9;

	public TextRankKeyphraseExtractor(ISegment seg) {
		super(seg);
	}

	@Override
	public List<String> getKeyphrase(Reader reader) throws IOException 
	{
		Map<IWord, List<IWord>> winMap = new HashMap<IWord, List<IWord>>();
		List<IWord> wordsPool = new ArrayList<IWord>();
		
		///document segment
		IWord w = null;
		seg.reset(reader);
		while ( (w = seg.next()) != null ) 
		{
			if ( filter(w) == false ) continue;
			if ( ! winMap.containsKey(w) ) 
			{
				winMap.put(w, new LinkedList<IWord>());
			}
			
			wordsPool.add(w);
		}
		
		///neighbour define
		for ( int i = 0; i < wordsPool.size(); i++ )
		{
			IWord word = wordsPool.get(i);
			List<IWord> support = winMap.get(word);
			
			int sIdx = Math.max(0, i - windowSize);
			int eIdx = Math.min(i + windowSize, wordsPool.size() - 1);
			
			for ( int j = sIdx; j <= eIdx; j++ )
			{
				if ( j == i ) continue;
				support.add(wordsPool.get(j));
			}
		}
		
		///do the page rank socres count
		Map<IWord, Float> score = null;
		for ( int c = 0; c < maxIterateNum; c++ )
		{
			Map<IWord, Float> T = new HashMap<IWord, Float>();
			for ( Map.Entry<IWord, List<IWord>> entry : winMap.entrySet() )
			{
				IWord key = entry.getKey();
				List<IWord> value = entry.getValue();
				
				float segema = 0F;
				for ( IWord ele : value )
				{
					int size = winMap.get(ele).size();
					if ( size == 0 
							|| ele.getValue().equals(key.getValue()) ) {
						continue;
					}
					
					float Sy = 0F;
					if ( score != null && score.containsKey(ele) ) {
						Sy = score.get(ele);
					}
					
					segema += Sy / size;
				}
				
				//core page rank algorithm
				T.put(key, 1 - D + D * segema);
			}
			
			score = T;
		}
		
		//sort the items by PR value
		List<Map.Entry<IWord, Float>> entryList = new ArrayList<Map.Entry<IWord, Float>>(score.entrySet());
		Collections.sort(entryList, new Comparator<Map.Entry<IWord, Float>>(){
			@Override
			public int compare(Entry<IWord, Float> o1, Entry<IWord, Float> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		
		if ( entryList.size() == 0 )
		{
			return new ArrayList<String>(1);
		}
		
		/*
		 * we consider the conjoint keywords as a key phrase
		 * so, get all the conjointed keywords
		*/
		IStringBuffer isb = new IStringBuffer();
		List<String> phraseList = new LinkedList<String>();
		
		for ( int i = 0; i < entryList.size();  )
		{
			Map.Entry<IWord, Float> entry = entryList.get(i);
			IWord seed = entry.getKey();
			int len = 0, pos = seed.getPosition() + seed.getLength() - 1;
			isb.clear().append(seed.getValue());
			
			for ( i++; i < entryList.size(); i++ )
			{
				Map.Entry<IWord, Float> te = entryList.get(i);
				IWord walker = te.getKey();
				if ( walker.getPosition() - pos != 1 )
				{
					break;
				}
				
				len++;
				pos = walker.getPosition() + walker.getLength() - 1;
				isb.append(walker.getValue());
			}
			
			//no matching
			if ( isb.length() > maxPhraseLength ) continue;
			if ( len == 0 ) 
			{
				//return the mix words
				if ( seed.getType() == IWord.T_MIXED_WORD ) {
					phraseList.add(seed.getValue());
				} else if ( autoMinLength > 0 
						&& seed.getLength() >= autoMinLength ) {
					phraseList.add(seed.getValue());
				}
				continue;
			}
			
			phraseList.add(isb.toString());
		}
		
		return phraseList;
	}

	public int getKeywordsNum() {
		return keywordsNum;
	}

	public void setKeywordsNum(int keywordsNum) {
		this.keywordsNum = keywordsNum;
	}

	public int getMaxIterateNum() {
		return maxIterateNum;
	}

	public void setMaxIterateNum(int maxIterateNum) {
		this.maxIterateNum = maxIterateNum;
	}

	public int getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}

	public int getAutoMinLength() {
		return autoMinLength;
	}

	public void setAutoMinLength(int autoMinLength) {
		this.autoMinLength = autoMinLength;
	}
	
	public int getMaxPhraseLength() {
		return maxPhraseLength;
	}

	public void setMaxPhraseLength(int maxPhraseLength) {
		this.maxPhraseLength = maxPhraseLength;
	}
}
