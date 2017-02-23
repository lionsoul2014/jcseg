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
    public final static boolean isMailAddress(String str)
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
    public final static boolean isUrlAddress(String str, ADictionary dic)
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
    public static final boolean isMobileNumber(String str)
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
    public static final boolean isIpAddress(String str)
    {
        if ( str.length() < 7 && str.length() > 15 ) {
            return false;
        }
        
        int sIdx = 0, eIdx, diff, pcount = 0;
        while ( (eIdx = str.indexOf('.', sIdx)) > -1 ) {
            pcount++;
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
        
        return pcount == 3;
    }
    
    /**
     * check if the specified string is an valid Latin Date string
     * like "2017/02/22", "2017-02-22" or "2017.02.22"
     * 
     * @param   str
     * @return  boolean
    */
    public static final String isDate(String str, char delimiter)
    {
        int length = str.length();
        if ( length > 10 ) {
            return null;
        }
        
        int sIdx = 0, eIdx = 0, idx = 0;
        String[] parts = new String[]{null,null,null};
        while ( (eIdx = str.indexOf(delimiter, sIdx)) > -1 ) {
            parts[idx++] = str.substring(sIdx, eIdx);
            sIdx = eIdx + 1;
            if ( idx > 2 ) {
                return null;
            }
        }
        
        if ( sIdx < length ) {
            parts[idx++] = str.substring(sIdx);
        }
        
        if ( idx < 2 || idx > 3 ) {
            return null;
        }
        
        String y, m, d = null;
        if ( idx == 2 ) {
            y = parts[0];
            m = parts[1];
        } else {
            y = parts[0];
            m = parts[1];
            d = parts[2];
        }
        
        //System.out.println(y+","+m+","+d);
        //year format check
        if ( y.length() != 4 || ! StringUtil.isDigit(y) 
                || y.charAt(0) == '0' ) {
            return null;
        }
        
        //month format check
        int len = m.length();
        if ( len < 1 || len > 2 ) {
            return null;
        }
        
        if ( len == 1 ) {
            char chr = m.charAt(0);
            if ( chr < '1' || chr > '9' ) {
                return null;
            }
        } else if ( len == 2 ) {
            char chr1 = m.charAt(0);
            char chr2 = m.charAt(1);
            if ( ! (chr1 == '0' || chr1 == '1') ) {
                return null;
            }
            if ( chr1 == '0' ) {
                if ( chr2 < '1' || chr2 > '9' ) {
                    return null;
                }
            } else if ( chr2 < '0' || chr2 > '2' ) {
                return null;
            }
        }
        
        //day format check
        if ( idx == 3 ) {
            len = d.length();
            if ( len < 1 || len > 2 ) {
                return null;
            }
            
            if ( len == 1 ) {
                char chr = d.charAt(0);
                if ( chr < '1' || chr > '9' ) {
                    return null;
                }
            } else if ( len == 2 ) {
                char chr1 = d.charAt(0);
                char chr2 = d.charAt(1);
                if ( "0123".indexOf(chr1) == -1 ) {
                    return null;
                }
                if ( chr1 < '3' ) {
                    if ( chr2 < '1' && chr2 > '9' ) {
                        return null;
                    }
                } else if ( chr2 < '0' || chr2 > '1' ) {
                    return null;
                }
            }
        }
        
        if ( delimiter == '.' ) {
            return idx == 2 ? (y+"-"+m) : (y+"-"+m+"-"+d);
        }
        
        return str;
    }
    
    /**
     * check if the specified string is a valid time string
     * like '12:45', '12:45:12'
     * 
     * @param   str
     * @return  boolean
    */
    public static final boolean isTime(String str)
    {
        int length = str.length();
        if ( length > 8 ) {
            return false;
        }
        
        int sIdx = 0, eIdx = 0, idx = 0;
        String[] parts = new String[]{null,null,null};
        while ( (eIdx = str.indexOf(':', sIdx)) > -1 ) {
            parts[idx++] = str.substring(sIdx, eIdx);
            sIdx = eIdx + 1;
            if ( idx > 2 ) {
                return false;
            }
        }
        
        if ( sIdx < length ) {
            parts[idx++] = str.substring(sIdx);
        }
        
        if ( idx < 2 || idx > 3 ) {
            return false;
        }
        
        String h,i,s = null;
        if ( idx == 2 ) {
            h = parts[0];
            i = parts[1];
        } else {
            h = parts[0];
            i = parts[1];
            s = parts[2];
        }
        
        //hour format check
        int len = h.length();
        if ( len < 1 || len > 2 ) {
            return false;
        }
        
        if ( len == 1 ) {
            char chr = h.charAt(0);
            if ( chr < '0' || chr > '9' ) {
                return false;
            }
        } else if ( len == 2 ) {
            char chr1 = h.charAt(0);
            char chr2 = h.charAt(1);
            if ( chr1 == '0' || chr1 == '1' ) {
                if ( chr2 < '0' || chr2 > '9' ) {
                    return false;
                }
            } else if ( chr1 == '2' ) {
                if ( chr2 < '0' || chr2 > '4' ) {
                    return false;
                }
            } else {
                return false;
            }
        }
        
        //minute format check
        len = i.length();
        if ( len < 1 || len > 2 ) {
            return false;
        }
        
        if ( len == 1 ) {
            char chr = i.charAt(0);
            if ( chr < '0' || chr > '9' ) {
                return false;
            }
        } else if ( len == 2 ) {
            char chr1 = i.charAt(0);
            char chr2 = i.charAt(1);
            if ( chr1 >= '0' && chr1 <= '5' ) {
                if ( chr2 < '0' || chr2 > '9' ) {
                    return false;
                }
            } else {
                return false;
            }
        }
        
        //second format check
        if ( idx == 3 ) {
            len = s.length();
            if ( len < 1 || len > 2 ) {
                return false;
            }
            
            if ( len == 1 ) {
                char chr = s.charAt(0);
                if ( chr < '0' || chr > '9' ) {
                    return false;
                }
            } else if ( len == 2 ) {
                char chr1 = s.charAt(0);
                char chr2 = s.charAt(1);
                if ( chr1 >= '0' && chr1 <= '5' ) {
                    if ( chr2 < '0' || chr2 > '9' ) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        
        return true;
    }
}
