package org.lionsoul.jcseg.segmenter;

import java.io.Serializable;
import java.util.List;

import org.lionsoul.jcseg.IWord;
import org.lionsoul.jcseg.SynonymsEntry;
import org.lionsoul.jcseg.util.ArrayUtil;


/**
 * word class for Jcseg with the {@link org.lionsoul.jcseg.IWord} interface implemented
 * 
 * at 2017/03/29: 
 * make the synonym series method {@link #getSyn()} {@link #setSyn(SynonymsEntry)}
 * and the part of speech series method {@link #getPartSpeech()} {@link #setPartSpeech(String[])} {@link #addPartSpeech(String)}
 * and the {@link #clone()} method synchronized for might happen concurrent access.
 * 
 * @author  chenxin<chenxin619315@gmail.com>
 */
public class Word implements IWord,Cloneable, Serializable
{
    private static final long serialVersionUID = 1L;
    private final String value;
    private int fre;
    private int type;
    private int position;
    
    /**
     * well we could get the length of the word by invoke #getValue().length
     * owing to the implementation of Jcseg and {@link #getValue()}.length may no equals to {@link #getLength()}
     * 
     * {@link #getLength()} will return the value set by #setLength
     */
    private int length = -1;
    private int h = -1;
    
    /**
     * NOTE added at 2016/11/12
     * word string entity name.
     * and it could be assigned from the lexicon or the word item setting
     * or assign dynamic during the segment runtime
     * 
     * NOTE make it an Array at 2017/06/06
    */
    private String[] entity = null;
    
    private String pinyin = null;
    private String[] partSpeech = null;
    private volatile SynonymsEntry syn = null;
    
    /**
     * NOTE added at 2017/10/02
     * 
     * with IWord additional parameter support
    */
    private String parameter = null;
    
    /**
     * construct method to initialize the newly created Word instance
     * 
     * @param   value
     * @param   fre
     * @param   type
     * @param   entity
    */
    public Word(String value, int fre, int type, String[] entity)
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
    
    public Word(String value, int type, String[] entity)
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

    @Override
    public void setFrequency(int freq) {
        this.fre = freq;
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
     * @see IWord#setType(int)
     */
    @Override
    public void setType(int type) 
    {
        this.type = type;
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
    public synchronized String[] getEntity() 
    {
        return entity;
    }
    
    /**
     * @see IWord#getEntity(int)
    */
    public synchronized String getEntity(int idx) 
    {
        if ( entity == null ) {
            return null;
        }
        
        if ( idx < 0 || idx > entity.length ) {
            return null;
        }
        
        return entity[idx];
    }
    
    /**
     * @see IWord#setEntity(String[])
    */
    public synchronized void setEntity(String[] entity) 
    {
        this.entity = entity;
    }
    
    public synchronized void setEntityForNull(String[] entity)
    {
    	if ( this.entity == null ) {
    		this.entity = entity;
    	}
    }
    
    /**
     * @see IWord#addEntity(String) 
    */
    public synchronized void addEntity(String e)
    {
        if ( e == null ) {
            // do nothing here
        } else if ( entity == null ) {
            entity = new String[]{e};
        } else if ( ArrayUtil.indexOf(e, entity) == -1 ) {
            String[] dest = new String[entity.length+1];
            System.arraycopy(entity, 0, dest, 0, entity.length);
            dest[entity.length] = e;
            entity = dest;
        }
    }
    
    /**
     * @see IWord#getPinyin()
     */
    @Override
    public String getPinyin() 
    {
        return pinyin;
    }
    
    /**
     * @see IWord#setPinyin(String)
     */
    public void setPinyin( String py ) 
    {
        pinyin = py;
    }

    /**
     * @see IWord#getSyn() 
     */
    @Override
    public SynonymsEntry getSyn() 
    {
        return syn;
    }

    @Override
    public void setSyn(SynonymsEntry syn) 
    {
        this.syn = syn;
    }
    
    /**
     * @see IWord#getPartSpeech() 
     */
    @Override
    public synchronized String[] getPartSpeech() 
    {
        return partSpeech;
    }
    
    @Override
    public synchronized void setPartSpeech(String[] partSpeech)
    {
        this.partSpeech = partSpeech;
    }
    
    public synchronized void setPartSpeechForNull(String[] partSpeech)
    {
    	if ( this.partSpeech == null ) {
    		this.partSpeech = partSpeech;
    	}
    }
    
    /**
     * @see IWord#addPartSpeech(String)
     */
    @Override
    public synchronized void addPartSpeech( String ps ) 
    {
        if ( partSpeech == null ) {
            partSpeech = new String[]{ps};
        } else {            
            String[] dest = new String[partSpeech.length+1];
            System.arraycopy(partSpeech, 0, dest, 0, partSpeech.length);
            dest[partSpeech.length] = ps;
            partSpeech = dest;
        }
    }
    

    @Override
    public String getParameter()
    {
        return parameter;
    }

    @Override
    public void setParameter(String param)
    {
        this.parameter = param;
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
        if ( partSpeech != null ) {
            for (int j = 0; j < partSpeech.length; j++ ) {
                if ( j == 0 ) {
                    sb.append(partSpeech[j]);
                } else {
                    sb.append(',');
                    sb.append(partSpeech[j]);
                }
            }
        } else {
            sb.append("null");
        }
        
        sb.append('/');
        sb.append(pinyin);
        sb.append('/');
        
        if ( syn != null ) {
            final List<IWord> synsList = syn.getList();
            synchronized ( synsList ) {
                for ( int i = 0; i < synsList.size(); i++ ) {
                    if ( i == 0 ) {
                        sb.append(synsList.get(i));
                    } else {
                        sb.append(',');
                        sb.append(synsList.get(i));
                    }
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
            sb.append(ArrayUtil.implode("|", entity));
        }
        
        if ( parameter != null ) {
            sb.append('/');
            sb.append(parameter);
        }
        
        return sb.toString();
    }
    
    /**
     * @see Object#toString()
     */
    public String toString() 
    {
            
        final StringBuilder sb = new StringBuilder();
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
        
        sb.append(",\"type\":").append(type);
        sb.append(",\"position\":").append(position);
        sb.append(",\"freq\":").append(fre);
        sb.append(",\"length\":").append(getLength());
        
        if ( pinyin != null ) {
            sb.append(",\"pinyin\":\"").append(pinyin).append('"');
        } else {
            sb.append(",\"pinyin\":null");
        }
        
        if ( partSpeech != null ) {
            sb.append(",\"pos\":\"").append(partSpeech[0]).append('"');
        } else {
            sb.append(",\"pos\":null");
        }
        
        if ( entity != null ) {
            sb.append(",\"entity\":")
                .append(ArrayUtil.toJsonObject(entity));
        } else {
            sb.append(",\"entity\":null");
        }
        
        //check and append the base word of the synonyms
        SynonymsEntry synEntry = getSyn();
        if ( synEntry != null ) {
            IWord rootWord = synEntry.getRootWord();
            sb.append(",\"root\":{")
                .append("\"word\":\"").append(rootWord.getValue())
                .append("\",\"length\":").append(rootWord.getLength())
                    .append(",\"pinyin\":");
            if ( rootWord.getPinyin() == null ) {
                sb.append("null");
            } else {
                sb.append('"').append(rootWord.getPinyin()).append('"');
            }
            
            sb.append('}');
        } else {
            sb.append(",\"root\":null");
        }
        
        //check and append the parameter
        if ( parameter != null ) {
            sb.append(",\"parameter\":\"");
            sb.append(parameter.replaceAll("\"", "\\\\\"")).append('"');
        } else {
            sb.append(",\"parameter\":null");
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
