package org.lionsoul.jcseg.tokenizer.core;

import java.util.LinkedList;
import java.util.List;

/**
 * abstract segmentor kit class 
 * 
 * @author  chenxin<chenxin619315@gmail.com>
*/
public class SegKit
{
    /**
     * quick interface to do the synonyms append word
     * You got check if the specified has any synonyms first
     * 
     * @param   wordPool
     * @param   wd
    */
    public final static void appendSynonyms(LinkedList<IWord> wordPool, IWord wd)
    {
        List<IWord> synList = wd.getSyn().getList();
        synchronized (synList) {
            for ( int j = 0; j < synList.size(); j++ ) {
                IWord curWord = synList.get(j);
                if ( curWord.getValue()
                        .equals(wd.getValue()) ) {
                    continue;
                }
                
                IWord synWord = synList.get(j).clone();
                synWord.setPosition(wd.getPosition());
                synWord.setLength(wd.getLength());	/* Force the length equals to the root word's */
                wordPool.add(synWord);
            }
        }
    }
    
}
