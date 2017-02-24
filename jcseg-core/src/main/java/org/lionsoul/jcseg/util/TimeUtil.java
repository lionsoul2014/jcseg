package org.lionsoul.jcseg.util;

import org.lionsoul.jcseg.tokenizer.core.IWord;

/**
 * Time Util class
 * 
 * @author  chenxin<chenxin619315@gmail.com>
*/
public class TimeUtil
{
    /**
     * date-time part index constants
     * we consider a date-time as the following seven parts:
     * +------+-------+-----+---------------+------+--------+--------+
     * | 0    | 1     | 2   | 3             | 4    | 5      | 6      |
     * +------+-------+-----+---------------+------+--------+--------+
     * | year | month | day | timing method | hour | minute | second |
     * +------+-------+-----+---------------+------+--------+--------+
     * and the numeric value before every part.
     * 
     * Note:
     * 1, the odd index is the entity part
     * 2, the even index is the numeric value part
    */
    public static final int DATETIME_NONE = -1;
    public static final int DATETIME_YV = 0;
    public static final int DATETIME_Y = 1;
    public static final int DATETIME_MV = 2;
    public static final int DATETIME_M = 3;
    public static final int DATETIME_DV = 4;
    public static final int DATETIME_D = 5;
    public static final int DATETIME_TMV = 6;
    public static final int DATETIME_TM = 7;
    public static final int DATETIME_HV = 8;
    public static final int DATETIME_H = 9;
    public static final int DATETIME_IV = 10;
    public static final int DATETIME_I = 11;
    public static final int DATETIME_SV = 12;
    public static final int DATETIME_S = 13;
    
    private static final String[] KeyMap = {
        "y", "y", "m", "m", "d", "d", 
        "tm",
        "h", "h", "i", "i", "s", "s"
    };
    
    /**
     * get and return the time part index of the specified IWord#entity
     * 
     * @param   entity
     * @return  int or -1 for failed
    */
    public static final int getDateTimeIndex(String entity)
    {
        if ( entity == null ) {
            return -1;
        }
        
        
        int sIdx, aIdx = entity.indexOf('#');
        if ( aIdx > -1 ) {
            sIdx = entity.indexOf('.', aIdx + 1);
        } else {
            sIdx = entity.indexOf('.');
        }
        
        if ( sIdx == -1 ) {
            return DATETIME_NONE;
        }
        
        String attr = entity.substring(sIdx+1);
        if ( attr.length() < 1 ) {
            return DATETIME_NONE;
        }
        
        if ( aIdx == -1 ) { //time unit word value
            if ( attr.equals("y") ) return DATETIME_Y;
            if ( attr.equals("m") ) return DATETIME_M;
            if ( attr.equals("d") ) return DATETIME_D;
            if ( attr.equals("tm")) return DATETIME_TM;
            if ( attr.equals("h") ) return DATETIME_H;
            if ( attr.equals("i") ) return DATETIME_I;
            if ( attr.equals("s") ) return DATETIME_S;
        }
        
        if ( attr.equals("y") ) return DATETIME_YV;
        if ( attr.equals("m") ) return DATETIME_MV;
        if ( attr.equals("d") ) return DATETIME_DV;
        if ( attr.equals("h") ) return DATETIME_HV;
        if ( attr.equals("i") ) return DATETIME_IV;
        if ( attr.equals("s") ) return DATETIME_SV;
          
        return DATETIME_NONE;
    }
    
    /**
     * create and return a date-time pool
     * 
     * @return  IWord[]
    */
    public static final IWord[] createDateTimePool()
    {
        return new IWord[]{
            null,   //year value
            null,   //year
            null,   //month value
            null,   //month
            null,   //day value
            null,   //day
            null,   //timing method value
            null,   //timing method
            null,   //hour value
            null,   //hour
            null,   //minute value
            null,   //minute
            null,   //second value
            null,   //seconds
        };
    }
    
    /**
     * fill the date-time pool specified part through the specified
     * time entity string.
     * 
     * @param   wPool
     * @param   word
     * @return  int
    */
    public static final int fillDateTimePool(IWord[] wPool, IWord word)
    {
        int pIdx = getDateTimeIndex(word.getEntity());
        if ( pIdx == DATETIME_NONE ) {
            return DATETIME_NONE;
        }
        
        wPool[pIdx] = word;
        return pIdx;
    }
    
    /**
     * fill the date-time pool specified part with part index constant
     * 
     * @param   wPool
     * @param   int
     * @param   word
    */
    public static final void fillDateTimePool(
            IWord[] wPool, int pIdx, IWord word)
    {
        wPool[pIdx] = word;
    }

    /**
     * get and return the time key part of the specified entity string 
     * 
     * @param   entity
     * @return  String
    */
    public static final String getTimeKey(String entity)
    {
        if ( entity == null ) {
            return null;
        }
        
        int sIdx, aIdx = entity.indexOf('#');
        if ( aIdx > -1 ) {
            sIdx = entity.indexOf('.', aIdx + 1);
        } else {
            sIdx = entity.indexOf('.');
        }
        
        return entity.substring(sIdx);
    }
    
    public static final String getTimeKey(IWord word)
    {
        return getTimeKey(word.getEntity());
    }
    
    /**
     * get and return the time key part with the part index value
     * 
     * @param   int
     * @return  String
    */
    public static final String getTimeKey(int pIdx)
    {
        if ( pIdx < 0 || pIdx > 12 ) {
            return null;
        }
        
        return KeyMap[pIdx];
    }
}
