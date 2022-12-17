package org.lionsoul.jcseg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Synonyms words entry class
 * 
 * @author  chenxin<chenxin619315@gmail.com>
*/
public class SynonymsEntry
{
    /**
     * the Synonyms root word 
    */
    protected IWord rootWord = null;
    
    /**
     * all the Synonym word list
    */
    private final List<IWord> synList = Collections.synchronizedList(new ArrayList<>());
    
    public SynonymsEntry()
    {
        this(null);
    }
    
    public SynonymsEntry(IWord rootWord) 
    {
        this.rootWord = rootWord;
    }
    
    /**
     * return the base word
     * 
     * @return  IWord
    */
    public IWord getRootWord()
    {
        return rootWord;
    }
    
    public void setRootWord(IWord rootWord)
    {
        this.rootWord = rootWord;
    }
    
    /**
     * return the synonyms list
     * 
     * @return  List<IWord>
    */
    public List<IWord> getList()
    {
        return synList;
    }
    
    public int size()
    {
        return synList.size();
    }
    
    /**
     * add a new synonyms word
     * and the newly added word will extend the part of speech and the entity
     *  from the base word if there are not set
     * 
     * @param   word
    */
    public void add(IWord word)
    {
        //check and extends the entity from the base word
        if ( word.getEntity() == null ) {
            word.setEntity(rootWord.getEntity());
        }
        
        //check and extends the part of speech from the base word
        if ( word.getPartSpeech() == null ) {
            word.setPartSpeech(rootWord.getPartSpeech());
        }
        
        word.setSyn(this);
        synList.add(word);
    }
   
}
