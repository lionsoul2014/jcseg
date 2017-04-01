package org.lionsoul.jcseg.tokenizer.core;

import java.lang.reflect.Field;

/**
 * word item entity class
 * 
 * @author  chenxin<chenxin619315@gmail.com>
*/
public class Entity
{
    public static final String E_EMAIL = "email";
    public static final String E_URL = "url";
    public static final String E_URL_HTTP = "url.http";
    public static final String E_URL_FTP = "url.ftp";
    public static final String E_IP = "ip";
    public static final String E_MOBILE_NUMBER = "mobile";
    
    public static final String E_NUMERIC = "numeric";                       //数字
    public static final String E_NUMERIC_ARABIC = "numeric.integer";       //整数
    public static final String E_NUMERIC_DECIMAL = "numeric.decimal";      //小数
    public static final String E_NUMERIC_FRACTION = "numeric.fraction";    //分数
    public static final String E_NUMERIC_CN = "numeric.cn";                 //中文数字
    public static final String E_NUMERIC_CN_FRACTION = "numeric.cn.fraction";  //中文分数
    public static final String E_NUMERIC_PERCENTAGE = "numeric.percentage"; //百分比
    
    public static final String E_NAME = "name";                     //人名
    public static final String E_NAME_CN = "name.cn";               //中国
    public static final String E_NAME_FOREIGN = "name.foreign";    //国外
    public static final String E_NAME_NICKNAME = "name.nickname";  //昵称
    
    public static final String E_PLACE = "place";                        //地区
    public static final String E_PLACE_CONTINENT = "place.continent";   //洲
    public static final String E_PLACE_NATION = "place.nation";         //国家
    public static final String E_PLACE_PROVINCE = "place.province";     //省份-国家的下一级地域
    public static final String E_PLACE_CITY = "place.city";             //城市
    public static final String E_PLACE_DISTRICT = "place.district";     //区或者县
    public static final String E_PLACE_TOWNSHIP = "place.township";     //乡
    public static final String E_PLACE_VILLAGE = "place.village";       //村
    public static final String E_PLACE_VIEWPOINT = "place.viewpoint";   //景点
    
    public static final String E_LENGTH = "length";             //长度
    public static final String E_LENGTH_M  = "length.m";        //米
    public static final String E_LENGTH_KM = "length.km";       //千米
    public static final String E_LENGTH_CM = "length.cm";       //分米
    public static final String E_LENGTH_DM = "length.dm";       //厘米
    public static final String E_LENGTH_MM = "length.mm";       //毫米
    public static final String E_LENGTH_UM = "length.um";       //微米
    public static final String E_LENGTH_NM = "length.nm";       //纳米
    public static final String E_LENGTH_IN = "length.in";       //英寸
    public static final String E_LENGTH_FT = "length.ft";       //英尺
    public static final String E_LENGTH_YD = "length.yd";       //码
    public static final String E_LENGTH_FM = "length.fm";       //英寻
    public static final String E_LENGTH_FUR = "length.fur";     //弗隆
    public static final String E_LENGTH_TFT = "length.tft";     //仗
    public static final String E_LENGTH_CFT = "length.cft";     //尺
    public static final String E_LENGTH_CIN = "length.cin";     //寸
    
    public static final String E_DISTANCE = "distance";
    public static final String E_DISTANCE_LI  = "distance.li";  //里
    public static final String E_DISTANCE_KM  = "distance.km";  //千米
    public static final String E_DISTANCE_MI  = "distance.mi";  //英里
    public static final String E_DISTANCE_NMI = "distance.nmi"; //海里
    public static final String E_DISTANCE_LY  = "distance.ly";  //光年
    
    
    public static final String E_DATETIME = "datetime";         //date time
    public static final String E_DATETIME_Y = "datetime.y";     //年
    public static final String E_DATETIME_M = "datetime.m";     //月
    public static final String E_DATETIME_D = "datetime.d";     //日
    public static final String E_DATETIME_A = "datetime.a";     //时断：上午，下午
    public static final String E_DATETIME_H = "datetime.h";     //小时
    public static final String E_DATETIME_I = "datetime.i";     //分
    public static final String E_DATETIME_S = "datetime.s";     //秒
    public static final String E_DATETIME_YM = "datetime.ym";   //year-month
    public static final String E_DATETIME_YMD = "datetime.ymd"; //date
    public static final String E_DATETIME_AH = "datetime.ah";
    public static final String E_DATETIME_AHI = "datetime.ahi";
    public static final String E_DATETIME_AHIS = "datetime.ahis";
    public static final String E_DATETIME_HI = "datetime.hi";   //hour-minute
    public static final String E_DATETIME_HIS = "datetime.his"; //time
    public static final String E_DATETIME_YMDHIS = "datetime.ymdhis";//std style
    public static final String E_DATETIME_YMDZHIS = "datetime.ymdahis";
    
    public static final String E_TIME = "time";             //时间
    public static final String E_TIME_YEAR = "time.y";      //年
    public static final String E_TIME_MON  = "time.m";      //月
    public static final String E_TIME_WEEK = "time.w";      //周
    public static final String E_TIME_D   = "time.d";       //天
    public static final String E_TIME_H   = "time.h";       //时
    public static final String E_TIME_I   = "time.i";       //分
    public static final String E_TIME_S   = "time.s";       //秒
    public static final String E_TIME_MS  = "time.ms";      //毫秒
    public static final String E_TIME_US  = "time.us";      //微妙
    public static final String E_TIME_NS  = "time.ns";      //纳秒
    
    //check the following link for more information
    //http://baike.baidu.com/link?url=CwmERmQUN2_pIaGDUv9eeHgQnh3Do5g4VN7jp9HHuJg2qx5XedVKKUVhHstNymJJWd4eNsjVTptJJdLpxvGNwa
    public static final String E_MONEY = "currency";             //货币
    public static final String E_MONEY_CNY = "currency.cny";       //人民币/money.cny
    public static final String E_MONEY_HKD = "currency.hkd";       //港元/money.hkd
    public static final String E_MONEY_USD = "currency.usd";       //美元/money.usd
    public static final String E_MONEY_MOP = "currency.mop";       //澳门元/money.mop
    public static final String E_MONEY_TWD = "currency.twd";       //台币/money.twd
    public static final String E_MONEY_KWP = "currency.kwp";       //朝鲜元/money.kwp
    public static final String E_MONEY_EUR = "currency.eur";       //欧元/money.eur
    public static final String E_MONEY_KRW = "currency.krw";       //韩元/money.krw
    public static final String E_MONEY_VND = "currency.vnd";       //越南盾/money.vnd
    public static final String E_MONEY_JPY = "currency.jpy";       //日元/money.jpy
    public static final String E_MONEY_SGD = "currency.sgd";       //新加坡元/money.sgd
    public static final String E_MONEY_AUD = "currency.aud";       //澳大利亚元/money.aud
    public static final String E_MONEY_THB = "currency.thb";       //泰铢/money.thb
    public static final String E_MONEY_BUK = "currency.buk";       //缅元/money.buk
    public static final String E_MONEY_INR = "currency.inr";       //卢比/money.inr
    public static final String E_MONEY_GBP = "currency.gbp";       //英镑/money.gbp
    public static final String E_MONEY_FRF = "currency.frf";       //法郎/money.frf
    public static final String E_MONEY_CAD = "currency.cad";       //加元/money.cad
    public static final String E_MONEY_DEM = "currency.dem";       //马克/money.dem
    public static final String E_MONEY_ITL = "currency.itl";       //里拉/money.itl
    public static final String E_MONEY_EGP = "currency.egp";       //埃及镑/money.egp
    public static final String E_MONEY_NZD = "currency.nzd";       //新西兰元/money.nzd
    public static final String E_MONEY_MYR = "currency.myr";       //林吉特/money.myr
    public static final String E_MONEY_SUR = "currency.sur";       //卢布/money.sur
    public static final String E_MONEY_ZAR = "currency.zar";       //兰特/money.zar
    public static final String E_MONEY_PHP = "currency.php";       //比索/money.php
    public static final String E_MONEY_MXP = "currency.mxp";       //墨西哥比索/money.mxp
    public static final String E_MONEY_CUP = "currency.cup";       //古巴比索/money.cup
    public static final String E_MONEY_SKK = "currency.skk";       //克朗/money.skk
    public static final String E_MONEY_ISK = "currency.isk";       //冰岛克朗/money.isk
    public static final String E_MONEY_SEK = "currency.sek";       //瑞典克朗/money.sek
    public static final String E_MONEY_DKK = "currency.dkk";       //丹麦克朗/money.dkk
    public static final String E_MONEY_NOK = "currency.nok";       //挪威克朗/money.nok
    public static final String E_MONEY_CSK = "currency.csk";       //捷克克朗/money.csk
    
    public static final String E_VOLUME = "volume";             //体积
    public static final String E_VOLUME_M3  = "volume.m3";      //立方米/volume.m3
    public static final String E_VOLUME_DM3 = "volume.dm3";     //立方分米/volume.dm3
    public static final String E_VOLUME_CM3 = "volume.cm3";     //立方厘米/volume.cm3
    public static final String E_VOLUME_MM3 = "volume.mm3";     //立方毫米/volume.mm3
    public static final String E_VOLUME_L  = "volume.l";        //升/volume.l
    public static final String E_VOLUME_DL = "volume.dl";       //分升/volume.dl
    public static final String E_VOLUME_CL = "volume.cl";       //厘升/volume.cl
    public static final String E_VOLUME_ML = "volume.ml";       //毫升/volume.ml
    public static final String E_VOLUME_UL = "volume.ul";       //微升/volume.ul
    public static final String E_VOLUME_NL = "volume.nl";       //纳升/volume.nl
    public static final String E_VOLUME_HL = "volume.hl";       //公石/volume.hl
    public static final String E_VOLUME_FT3 = "volume.ft3";     //立方英尺/volume.ft3
    public static final String E_VOLUME_IN3 = "volume.in3";     //立方英寸/volume.in3
    public static final String E_VOLUME_YD3 = "volume.yd3";     //立方码/volume.yd3
    public static final String E_VOLUME_MFT = "volume.mft";     //亩英尺/volume.mft
    public static final String E_VOLUME_GAL_UK = "volume.gal.uk";   //英制加仑/volume.ukgal
    public static final String E_VOLUME_GAL_US = "volume.gal.us";   //美制加仑/volume.usgal
    
    public static final String E_QUALITY = "quality";           //质量
    public static final String E_QUALITY_KG = "quality.kg";     //千克/quality.kg
    public static final String E_QUALITY_G = "quality.g";       //克/quality.g
    public static final String E_QUALITY_MG = "quality.mg";     //毫克/quality.mg
    public static final String E_QUALITY_T = "quality.t";       //吨/quality.t
    public static final String E_QUALITY_Q = "quality.q";       //公担/quality.q
    public static final String E_QUALITY_LB = "quality.lb";     //磅/quality.lb
    public static final String E_QUALITY_OZ = "quality.oz";     //盎司/quality.oz 
    public static final String E_QUALITY_CT = "quality.ct";     //克拉/quality.ct  
    public static final String E_QUALITY_GR = "quality.gr";     //格令/quality.gr 
    public static final String E_QUALITY_LT = "quality.lt";     //长吨/quality.lt 
    public static final String E_QUALITY_ST = "quality.st";     //短吨/quality.st
    public static final String E_QUALITY_DAN_UK = "quality.dan.uk";     //英担/quality.dan.uk
    public static final String E_QUALITY_DAN_US = "quality.dan.us";     //美担/quality.dan.us
    public static final String E_QUALITY_BP = "quality.bp";         //英石/quality.bp
    public static final String E_QUALITY_DR = "quality.dr";         //打兰/quality.dr
    public static final String E_QUALITY_DAN = "quality.dan";       //担/quality.dan
    public static final String E_QUALITY_JIN = "quality.jin";       //斤/quality.jin
    public static final String E_QUALITY_LIANG = "quality.liang";   //两/quality.liang
    public static final String E_QUALITY_QIAN = "quality.qian";     //钱/quality.qian
    
    public static final String E_AREA = "area";             //面积
    public static final String E_AREA_KM2 = "area.km2";     //平方千米/area.km2
    public static final String E_AREA_M2  = "area.m2";      //平方米/area.m2
    public static final String E_AREA_DM2 = "area.dm2";     //平方分米/area.dm2
    public static final String E_AREA_CM2 = "area.cm2";     //平方厘米/area.cm2
    public static final String E_AREA_MM2 = "area.mm2";     //平方毫米/area.mm2
    public static final String E_AREA_UM2 = "area.um2";     //平方微米/area.um2
    public static final String E_AREA_NM2 = "area.nm2";     //平方纳米/area.nm2
    public static final String E_AREA_HA  = "area.ha";      //公顷/area.ha
    public static final String E_AREA_ARE = "area.are";     //公亩/area.are
    public static final String E_AREA_ACRE  = "area.acre";  //英亩/area.acre
    public static final String E_AREA_SQ_MI = "area.sq.mi"; //平方英里/area.sq.mi
    public static final String E_AREA_SQ_YD = "area.sq.yd"; //平方码/area.sq.yd
    public static final String E_AREA_SQ_FT = "area.sq.ft"; //平方英尺/area.sq.ft
    public static final String E_AREA_SQ_IN = "area.sq.in"; //平方英寸/area.sq.in
    public static final String E_AREA_SQ_RD = "area.sq.rd"; //平方竿/area.sq.rd
    public static final String E_AREA_QING = "area.qing";   //顷/area.qing
    public static final String E_AREA_MU   = "area.mu";     //亩/area.mu
    public static final String E_AREA_FT2  = "area.ft2";    //平方尺/area.ft2
    public static final String E_AREA_IN2  = "area.in2";    //平方寸/area.in2
    
    public static final String E_TEMPERATURE = "temperature";       //温度
    public static final String E_TEMPERATURE_C = "temperature.c";   //摄氏度/temperature.c
    public static final String E_TEMPERATURE_F = "temperature.f";   //华氏度/temperature.f
    public static final String E_TEMPERATURE_K = "temperature.k";   //开氏度/temperature.k
    public static final String E_TEMPERATURE_R = "temperature.r";   //兰氏度/temperature.r
    public static final String E_TEMPERATURE_RE = "temperature.re"; //列氏度/temperature.re
    
    public static final String E_FORCE = "force";           //力
    public static final String E_FORCE_N   = "force.n";     //牛/force.n
    public static final String E_FORCE_KN  = "force.kn";    //千牛/force.kn
    public static final String E_FORCE_KGF = "force.kgf";   //千克力/force.kgf
    public static final String E_FORCE_GF  = "force.gf";    //克力/force.gf
    public static final String E_FORCE_TF  = "force.tf";    //公吨力/force.tf
    public static final String E_FORCE_LBF = "force.lbf";   //磅力/force.lbf
    public static final String E_FORCE_KIP = "force.kip";   //千磅力/force.kip
    
    public static final String E_PRESSURE = "pressure";         //压力
    public static final String E_PRESSURE_PA  = "pressure.pa";  //帕斯卡/pressure.pa
    public static final String E_PRESSURE_KPA = "pressure.kpa"; //千帕/pressure.kpa
    public static final String E_PRESSURE_HPA = "pressure.hpa"; //百帕/pressure.hpa
    public static final String E_PRESSURE_ATM = "pressure.atm"; //标准大气压/pressure.atm
    public static final String E_PRESSURE_HG_MM = "pressure.gh.mm"; //毫米汞柱/pressure.hg.mm
    public static final String E_PRESSURE_HG_IN = "pressure.hg.in"; //英寸汞柱/pressure.hg.in
    public static final String E_PRESSURE_BAR   = "pressure.bar";   //巴/pressure.bar
    public static final String E_PRESSURE_MBAR  = "pressure.mbar";  //毫巴/pressure.mbar
    public static final String E_PRESSURE_WG_MM = "pressure.wg.mm"; //毫米水柱/pressure.wg.mm
    
    public static final String E_ANGLE = "angle";           //角度
    public static final String E_ANGLE_360  = "angle.360";  //圆周/angle.360
    public static final String E_ANGLE_90   = "angle.90";   //直角/angle.90
    public static final String E_ANGLE_DU   = "angle.du";   //度
    public static final String E_ANGLE_FEN  = "angle.fen";  //分
    public static final String E_ANGLE_GON  = "angle.gon";  //百分度/angle.gon
    public static final String E_ANGLE_RAD  = "angle.rad";  //弧度/angle.rad
    public static final String E_ANGLE_MRAD = "angle.mrad"; //毫弧度/angle.mrad
    
    public static final String E_STORAGE = "storage";           //存储
    public static final String E_STORAGE_BIT = "storage.bit";   //比特/storage.bit
    public static final String E_STORAGE_B   = "storage.b";     //字节/storage.b
    public static final String E_STORAGE_KB  = "storage.kb";    //千字节/storage.kb
    public static final String E_STORAGE_MB  = "storage.mb";    //兆字节/storage.mb
    public static final String E_STORAGE_GB  = "storage.gb";    //千兆字节/storage.gb
    public static final String E_STORAGE_TB  = "storage.tb";    //太字节/storage.tb
    public static final String E_STORAGE_PB  = "storage.pb";    //拍字节/storage.pb
    public static final String E_STORAGE_EB  = "storage.eb";    //艾字节/storage.eb
    
    public static final String E_UNIT_DAN     = "unit.dan";         //担
    public static final String E_UNIT_BOTTLE  = "unit.bottle";      //瓶
    public static final String E_UNIT_BAG = "unit.bag";             //袋
    public static final String E_UNIT_BOX = "unit.box";             //盒
    public static final String E_UNIT_DISCOUNT = "unit.discount";   //折
    public static final String E_UNIT_ITEM = "unit.item";           //件
    
    public static String[] fieldsArr = null;
    static {
        Field[] fields = Entity.class.getDeclaredFields();
        fieldsArr = new String[fields.length];
        try { 
            int i = 0;
            for ( Field f : fields ) {
                if ( ! f.getName().startsWith("E_") ) {
                    continue;
                }
                
                fieldsArr[i++] = (String) f.get(Entity.class);
            }
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {}
    }
    
    /**
     * get the entity string by the specified key
     * @Note well, this may puzzle you more or less
     * thousands of word items quote the same string constants
     * of course, this will save lots of runtime memory (constants string pool)
    */
    public static String get(String key)
    {
        key = key.toLowerCase();
        for ( String f : fieldsArr ) {
            if ( key.equals(f) ) {
                return f;
            }
        }
        
        return key;
    }
    
}
