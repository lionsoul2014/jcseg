package org.lionsoul.jcseg.tokenizer;

import org.lionsoul.jcseg.tokenizer.core.IWord;


/**
 * word class for Jcseg with the {@link org.lionsoul.jcseg.core.IWord} interface implemented
 * 
 * at 2017/03/29: 
 * make the synonyms series method {@link #getSyn()} {@link #setSyn(String[])} {@link #addSyn(String)}
 * and the part of speech series method {@link #getPartSpeech()} {@link #setPartSpeech(String[])} {@link #addPartSpeech(String)}
 * and the {@link #clone()} method synchronized for may coming concurrent access.
 * 
 * @author  chenxin<chenxin619315@gmail.com>
 */
public class Word implements IWord,Cloneable
{
    private String value;
    private int fre = 0;
    private int type;
    private int position;
    
    /**
     * well the we could get the length of the word by invoke #getValue().length
     * owing to the implementation of Jcseg and {@link #getValue()}.length may no equals to {@link #getLength()}
     * 
     * {@link #getLength()} will return the value setted by
     */
    private int length = -1;
    private int h = -1;
    
    /**
     * @Note added at 2016/11/12
     * word string entity name and 
     * it could be assign from the lexicon or the word item setting
     * or assign dynamic during the segment runtime
    */
    private String entity = null;
    
    private String pinyin = null;
    private String[] partspeech = null;
    private String[] syn = null;
    
    /**
     * construct method to initialize the newly created Word instance
     * 
     * @param   value
     * @param   fre
     * @param   type
     * @param   entity
    */
    public Word(String value, int fre, int type, String entity)
    {
        this.value  = value;
        this.fre    = fre;
        this.type   = type;
        this.entity = entity;
    }

    public Word( String value, int fre, int type ) 
    {
        this(value, fre, type, null);
    }
    
    public Word( String value, int type ) 
    {
        this(value, 0, type, null);
    }
    
    public Word(String value, int type, String entity)
    {
        this(value, 0, type, entity);
    }
    
    /**
     * @see IWord#getValue() 
     */
    @Override
    public String getValue() 
    {
        return value;
    }
    
    /**
     * @see IWord#getLength() 
     */
    @Override
    public int getLength() 
    {
        return (length == -1 ) ? value.length() : length;
    }
    

    /**
     * @see IWord#setLength(int) 
     */
    @Override
    public void setLength( int length ) 
    {
        this.length = length;
    }

    /**
     * @see IWord#getFrequency() 
     */
    @Override
    public int getFrequency() 
    {
        return fre;
    }

    /**
     * @see IWord#getType() 
     */
    @Override
    public int getType() 
    {
        return type;
    }
    
    /**
     * @see IWord#setPosition(int)
     */
    @Override
    public void setPosition( int pos ) 
    {
        position = pos;
    }
    
    /**
     * @see IWord#getPosition()
     */
    public int getPosition() 
    {
        return position;
    }
    
    /**
     * @see IWord#getEntity()
    */
    public String getEntity() 
    {
        return entity;
    }
    
    /**
     * @see IWord#setEntity(String)
    */
    public void setEntity(String entity) 
    {
        this.entity = entity;
    }
    
    /**
     * @see IWord#getPinying() 
     */
    @Override
    public String getPinyin() 
    {
        return pinyin;
    }
    
    /**
     * @see IWord#setPinying(String)
     */
    public void setPinyin( String py ) 
    {
        pinyin = py;
    }

    /**
     * @see IWord#getSyn() 
     */
    @Override
    public synchronized String[] getSyn() 
    {
        return syn;
    }

    @Override
    public synchronized void setSyn(String[] syn) 
    {
        this.syn = syn;
    }
    
    /**
     * @see IWord#addSyn(String) 
     */
    @Override
    public synchronized void addSyn( String s ) 
    {
        if ( syn == null ) {
            syn = new String[1];
            syn[0] = s;
        } else {
            String[] tycA = syn;
            syn = new String[syn.length + 1];
            int j;
            for ( j = 0; j < tycA.length; j++ ) {
                syn[j] = tycA[j];
            }
            syn[j] = s;
            tycA = null;
        }
    }
    
    /**
     * @see IWord#getPartSpeech() 
     */
    @Override
    public synchronized String[] getPartSpeech() 
    {
        return partspeech;
    }
    
    @Override
    public synchronized void setPartSpeech(String[] partspeech) 
    {
        this.partspeech = partspeech;
    }
    
    /**
     * @see IWord#addPartSpeech( String );
     */
    @Override
    public synchronized void addPartSpeech( String ps ) 
    {
        if ( partspeech == null ) {
            partspeech = new String[1];
            partspeech[0] = ps;
        } else {
            String[] bak = partspeech;
            partspeech = new String[partspeech.length + 1];
            int j;
            for ( j = 0; j < bak.length; j++ ) {
                partspeech[j] = bak[j];
            }
            partspeech[j] = ps;
            bak = null;
        }
    }
    
    /**
     * Interface to clone the current object
     * 
     * @return IWord
     */
    @Override
    public synchronized IWord clone()
    {
        IWord w = null;
        try {
            w = (IWord) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        
        return w;
    }
    
    
    /**
     * @see Object#equals(Object) 
     * @see IWord#equals(Object)
     */
    public boolean equals( Object o ) 
    {
        if ( this == o ) return true;
        
        if ( o instanceof IWord ) {
            IWord word = (IWord) o;
            boolean bool = word.getValue().equalsIgnoreCase(this.getValue());
            /*
             * value equals and the type of the word must
             * be equals too, for there is many words in
             * different lexicon with a same value but 
             * in different use. 
             */
            return (bool && (word.getType() == this.getType()));
        }
        
        return false;
    }
    
    /**
     * for debug only 
    */
    public String __toString() 
    {
        StringBuilder sb = new StringBuilder();
        sb.append(value);
        sb.append('/');
        //append the cx
        if ( partspeech != null ) {
            for ( int j = 0; j < partspeech.length; j++ ) {
                if ( j == 0 ) {
                    sb.append(partspeech[j]);
                } else {
                    sb.append(',');
                    sb.append(partspeech[j]);
                }
            }
        } else {
            sb.append("null");
        }
        
        sb.append('/');
        sb.append(pinyin);
        sb.append('/');
        //append the tyc
        if ( syn != null ) {
            for ( int j = 0; j < syn.length; j++ ) {
                if ( j == 0 ) {
                    sb.append(syn[j]);
                } else {
                    sb.append(',');
                    sb.append(syn[j]);
                }
            }
        } else {
            sb.append("null");
        }
        
        if ( value.length() == 1 ) {
            sb.append('/');
            sb.append(fre);
        }
        
        if ( entity != null ) {
            sb.append('/');
            sb.append(entity);
        }
        
        return sb.toString();
    }
    
    /**
     * @see Object#toString()
     */
    public String toString() 
    {
            
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        
        /*
         * @Note: Check and pre-process the word value
         * for if there is char "\"" or "\\" inside 
         * the Json Anti-analysis will be end with parse error ...
         */
        if ( value.length() == 1 
                && (value.charAt(0) == '"' || value.charAt(0) == '\\') ) {
            sb.append("\"word\":\"\\").append(value).append('"');
        } else {
            sb.append("\"word\":\"").append(value).append('"');
        }
        
        sb.append(",\"position\":").append(position);
        sb.append(",\"length\":").append(getLength());
        
        if ( pinyin != null ) {
            sb.append(",\"pinyin\":\"").append(pinyin).append('"');
        } else {
            sb.append(",\"pinyin\":null");
        }
        
        if ( partspeech != null ) {
            sb.append(",\"pos\":\"").append(partspeech[0]).append('"');
        } else {
            sb.append(",\"pos\":null");
        }
        
        if ( entity != null ) {
            sb.append(",\"entity\":\"").append(entity).append('"');
        } else {
            sb.append(",\"entity\":null");
        }
        
        sb.append('}');
        return sb.toString();
    }
    
    /**
     * rewrite the hash code generate algorithm
     * take the value as the main factor
     *  
     * @return  int
    */
    @Override
    public int hashCode()
    {
        if ( h == -1 ) {
            /*
             * DJB hash algorithm 2
             * invented by doctor Daniel J. Bernstein.
             */
            h = 5381;
            for ( int j = 0; j < value.length(); j++ ) {
                h = h * 33 ^ value.charAt(j);
            }
            h &= 0x7FFFFFFF;
        }
        
        return h;
    }
    
}
