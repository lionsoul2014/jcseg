package org.lionsoul.jcseg.util;

import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.ILexicon;

/**
 * Entity format manager class
 * 
 * @author  chenxin<chenxin619315@gmail.com>
*/
public class EntityFormat
{
    /**
     * check if the specified string is an email address or not 
     * 
     * @param   str
     * @return  boolean
    */
    public static boolean isMailAddress(String str)
    {
        int atIndex = str.indexOf('@');
        if ( atIndex == -1 ) {
            return false;
        }
        
        if ( ! StringUtil.isLetterOrNumeric(str, 0, atIndex) ) {
            return false;
        }
        
        int ptIndex, ptStart = atIndex + 1;
        while ( (ptIndex = str.indexOf('.', ptStart)) > 0 ) {
            if ( ptIndex == ptStart ) {
                return false;
            }
            
            if ( ! StringUtil.isLetterOrNumeric(str, ptStart, ptIndex) ) {
                return false;
            }
            
            ptStart = ptIndex + 1;
        }
        
        if ( ptStart < str.length() 
                && ! StringUtil.isLetterOrNumeric(str, ptStart, str.length()) ) {
            return false;
        }
        
        return true;
    }
    
    /**
     * check if the specified string is an URL address or not
     * 
     * @param   str
     * @param   dic optional dictionary object
     * @return  boolean
    */
    public static boolean isUrlAddress(String str, ADictionary dic)
    {
        int prIndex = str.indexOf("://");
        if ( prIndex > -1 && ! StringUtil.isLatin(str, 0, prIndex) ) {
            return false;
        }
        
        int sIdx = prIndex > -1 ? prIndex + 3 : 0;
        int slIndex = str.indexOf('/', sIdx), sgIndex = str.indexOf('?', sIdx);
        int eIdx = slIndex > -1 ? slIndex : (sgIndex > -1 ? sgIndex : str.length());
        int lpIndex = -1;
        for ( int i = sIdx; i < eIdx; i++ ) {
            char chr = str.charAt(i);
            if ( chr == '.' ) {
                if ( lpIndex == -1 ) {
                    lpIndex = i;
                    continue;
                }
                
                if ( (i - lpIndex) == 1 || i == (eIdx - 1)) {
                    return false;
                }
                
                lpIndex = i;
            } else if ( ! StringUtil.isEnLetter(chr) 
                    && ! StringUtil.isEnNumeric(chr) ) {
                return false;
            }
        }
        
        if ( dic != null && ! dic.match(ILexicon.DOMAIN_SUFFIX, 
                str.substring(lpIndex+1, eIdx)) ) {
            return false;
        }
        
        //check the path part
        if ( slIndex > -1 ) {
            sIdx = slIndex;
            eIdx = sgIndex > -1 ? sgIndex : str.length();
            lpIndex = -1;
            for ( int i = sIdx; i < eIdx; i++ ) {
                char chr = str.charAt(i);
                if ( "./-_".indexOf(chr) > -1 ) {
                    if ( lpIndex == -1 ) {
                        lpIndex = i;
                        continue;
                    }
                    
                    if ( i - lpIndex == 1 || (chr == '.' && i == (eIdx - 1)) ) {
                        return false;
                    }
                    
                    lpIndex = i;
                } else if ( ! StringUtil.isEnLetter(chr) 
                        &&  ! StringUtil.isEnNumeric(chr) ) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * check if the specified string is a mobile number
     * 
     * @param   str
     * @return  boolean
    */
    public static boolean isMobileNumber(String str)
    {
        if ( str.length() != 11 ) {
            return false;
        }
        
        if ( str.charAt(0) != '1' ) {
            return false;
        }
        
        if ( "34578".indexOf(str.charAt(1)) == -1 ) {
            return false;
        }
        
        return StringUtil.isNumeric(str, 2, str.length());
    }
    
    /**
     * check if the specified string is a IPv4/v6 address
     * v6 is not supported for now
     * 
     * @param   str
     * @return  boolean
    */
    public static boolean isIpAddress(String str)
    {
        if ( str.length() < 7 && str.length() > 15 ) {
            return false;
        }
        
        int sIdx = 0, eIdx, diff;
        while ( (eIdx = str.indexOf('.', sIdx)) > -1 ) {
            diff = eIdx - sIdx;
            switch ( diff ) {
            case 0:
                return false;
            case 1:
            case 2:
                if ( ! StringUtil.isNumeric(str, sIdx, eIdx) ) return false;
                break;
            case 3:
                char chr = str.charAt(sIdx);
                if ( chr == '1' ) {
                    if ( ! StringUtil.isNumeric(str, sIdx+1, eIdx) ) return false;
                } else if ( chr == '2' ) {
                    chr = str.charAt(sIdx+1);
                    if ( chr < 48 || chr > 53 ) return false;
                    chr = str.charAt(sIdx+2);
                    if ( chr < 48 || chr > 53 ) return false;
                }
                break;
            default:
                return false;
            }
            
            sIdx = eIdx + 1;
        }
        
        return true;
    }
    
}
