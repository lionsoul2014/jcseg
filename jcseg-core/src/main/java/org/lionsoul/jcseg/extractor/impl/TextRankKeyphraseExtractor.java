package org.lionsoul.jcseg.extractor.impl;

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

import org.lionsoul.jcseg.extractor.KeyphraseExtractor;
import org.lionsoul.jcseg.tokenizer.core.ISegment;
import org.lionsoul.jcseg.tokenizer.core.IWord;
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
    protected int maxIterateNum = 120;
    
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
    protected int maxWordsNum = 5;

    public TextRankKeyphraseExtractor(ISegment seg)
    {
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
        while ( (w = seg.next()) != null ) {
            if ( filter(w) == false ) continue;
            if ( ! winMap.containsKey(w) ) {
                winMap.put(w, new LinkedList<IWord>());
            }
            
            wordsPool.add(w);
        }
        
        ///neighbour define
        for ( int i = 0; i < wordsPool.size(); i++ ) {
            IWord word = wordsPool.get(i);
            List<IWord> support = winMap.get(word);
            
            int sIdx = Math.max(0, i - windowSize);
            int eIdx = Math.min(i + windowSize, wordsPool.size() - 1);
            
            for ( int j = sIdx; j <= eIdx; j++ ) {
                if ( j == i ) continue;
                support.add(wordsPool.get(j));
            }
        }
        
        ///do the page rank socres count
        Map<IWord, Float> score = new HashMap<IWord, Float>();
        for ( int c = 0; c < maxIterateNum; c++ ) {
            for ( Map.Entry<IWord, List<IWord>> entry : winMap.entrySet() ) {
                IWord key = entry.getKey();
                List<IWord> value = entry.getValue();
                
                float sigema = 0F;
                for ( IWord ele : value ) {
                    int size = winMap.get(ele).size();
                    if ( size == 0 
                            || ele.getValue().equals(key.getValue()) ) {
                        continue;
                    }
                    
                    float Sy = 0F;
                    if ( score != null && score.containsKey(ele) ) {
                        Sy = score.get(ele);
                    }
                    
                    sigema += Sy / size;
                }
                
                //core page rank algorithm
                score.put(key, 1 - D + D * sigema);
            }
        }
        
        //sort the items by PR value
        List<Map.Entry<IWord, Float>> entryList = new ArrayList<Map.Entry<IWord, Float>>(score.entrySet());
        Collections.sort(entryList, new Comparator<Map.Entry<IWord, Float>>(){
            @Override
            public int compare(Entry<IWord, Float> o1, Entry<IWord, Float> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        
        if ( entryList.size() == 0 ) {
            return new ArrayList<String>(1);
        }
        
        float tScores = 0F, avgScores = 0F, stdScores = 0F;
        for ( Map.Entry<IWord, Float> entry : entryList ) {
            tScores += entry.getValue();
            //System.out.println(entry.getKey().getValue()+"="+entry.getValue());
        }
        
        avgScores = tScores / wordsPool.size();
        stdScores = avgScores * (1 + D);
        
        /*
         * we consider the conjoint keywords as a key phrase
         * so, get all the conjointed keywords
        */
        IStringBuffer isb = new IStringBuffer();
        List<String> phraseList = new LinkedList<String>();
        for ( int i = 0; i < entryList.size(); ) {
            Map.Entry<IWord, Float> entry = entryList.get(i);
            IWord seed = entry.getKey();
            if ( entry.getValue() < stdScores ) {
                i++;
                continue;
            }
            
            int len = 0;
            List<IWord> sortQueue = null;
            List<IWord> listQueue = new ArrayList<IWord>(maxWordsNum);
            listQueue.add(seed);
            
            //make a chunk
            for ( int j = 1; j < maxWordsNum; j++ ) {
                int idx = i + j;
                if ( idx >= entryList.size() ) break;
                listQueue.add(entryList.get(idx).getKey());
            }
            
            for ( ; listQueue.size() > 1 ; ) {
                /*
                 * sort the sort queue and check all the words could
                 * make a phrase by its original position
                */
                sortQueue = new ArrayList<IWord>(listQueue);
                Collections.sort(sortQueue, new Comparator<IWord>(){
                    @Override
                    public int compare(IWord o1, IWord o2) {
                        return (o1.getPosition() - o2.getPosition());
                    }
                });
                
                IWord t = sortQueue.get(0);
                int pos = t.getPosition() + t.getLength() - 1;
                boolean match = true;
                for ( int k = 1; k < sortQueue.size(); k++ ) {
                    IWord kw = sortQueue.get(k);
                    if ( kw.getPosition() - pos != 1  ) {
                        match = false;
                        break;
                    }
                    
                    //reset the pos
                    pos = kw.getPosition() + kw.getLength() - 1;
                }
                
                /*
                 * not matched, remove the last word item
                 * from the list queue and continue the next match check ... 
                */
                if ( match == false ) {
                    //let gc do its work
                    sortQueue.clear();
                    sortQueue = null;
                    listQueue.remove(listQueue.size()-1);
                    continue;
                }
                
                len = listQueue.size();
                break;
            }
            
            //no matching
            if ( len == 0 ) {
                //return the mix words
                if ( seed.getType() == IWord.T_MIXED_WORD ) {
                    phraseList.add(seed.getValue());
                } else if ( autoMinLength > 0 
                        && seed.getLength() >= autoMinLength ) {
                    phraseList.add(seed.getValue());
                }
                
                i++;
                continue;
            }
            
            isb.clear();
            for ( IWord word : sortQueue ) isb.append(word.getValue());
            phraseList.add(isb.toString());
            i += sortQueue.size();
            
            //let gc do its work
            listQueue.clear();
            listQueue = null;
        }
        
        return phraseList;
    }

    public int getKeywordsNum()
    {
        return keywordsNum;
    }

    public void setKeywordsNum(int keywordsNum)
    {
        this.keywordsNum = keywordsNum;
    }

    public int getMaxIterateNum()
    {
        return maxIterateNum;
    }

    public void setMaxIterateNum(int maxIterateNum)
    {
        this.maxIterateNum = maxIterateNum;
    }

    public int getWindowSize()
    {
        return windowSize;
    }

    public void setWindowSize(int windowSize)
    {
        this.windowSize = windowSize;
    }

    public int getAutoMinLength()
    {
        return autoMinLength;
    }

    public void setAutoMinLength(int autoMinLength)
    {
        this.autoMinLength = autoMinLength;
    }
    
    public int getMaxWordsNum()
    {
        return maxWordsNum;
    }

    public void setMaxWordsNum(int maxPhraseLength)
    {
        this.maxWordsNum = maxPhraseLength;
    }
}
