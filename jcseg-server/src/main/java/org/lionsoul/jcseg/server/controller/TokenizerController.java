package org.lionsoul.jcseg.server.controller;

import java.io.IOException;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.lionsoul.jcseg.server.JcsegController;
import org.lionsoul.jcseg.server.JcsegGlobalResource;
import org.lionsoul.jcseg.server.JcsegTokenizerEntry;
import org.lionsoul.jcseg.server.core.GlobalResource;
import org.lionsoul.jcseg.server.core.ServerConfig;
import org.lionsoul.jcseg.server.core.UriEntry;
import org.lionsoul.jcseg.tokenizer.core.ISegment;
import org.lionsoul.jcseg.tokenizer.core.IWord;
import org.lionsoul.jcseg.tokenizer.core.JcsegException;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;
import org.lionsoul.jcseg.tokenizer.core.SegmentFactory;

/**
 * tokenize service handler
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class TokenizerController extends JcsegController
{

    public TokenizerController(
            ServerConfig config,
            GlobalResource resourcePool, 
            UriEntry uriEntry,
            Request baseRequest, 
            HttpServletRequest request,
            HttpServletResponse response) throws IOException
    {
        super(config, resourcePool, uriEntry, baseRequest, request, response);
    }
    
    
    /**
     *  WordEntry for return data
     *
     * */
    public class WordEntry {
        
        private String  val         = null;
        private String  pinYin      = null;
        private String[] partSpeech = null;
        private int     length      = -1;
        private int     position    = -1;
        private int     frequency   = -1;    
       
     
        public String getVal() {
            return val;
        }
        public void setVal(String val) {
            this.val = val;
        }
        public String getPinYin() {
            return pinYin;
        }
        public void setPinYin(String pinYin) {
            this.pinYin = pinYin;
        }
        public String[] getPartSpeech() {
            return partSpeech;
        }
        public void setPartSpeech(String[] partSpeech) {
            this.partSpeech = partSpeech;
        }
        public int getLength() {
            return length;
        }
        public void setLength(int length) {
            this.length = length;
        }
        public int getPosition() {
            return position;
        }
        public void setPosition(int position) {
            this.position = position;
        }
        public int getFrequency() {
            return frequency;
        }
        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }
        
        
        
        public String toString(){
            StringBuilder sb = new StringBuilder();
            sb.append('{');
            
            sb.append("\"val\":\""+val+"\"");
             
            if (pinYin != null)
                sb.append(",\"pinyin\":\"" + pinYin + "\"");
            
            if (partSpeech != null)
                sb.append(",\"part_speech\":\"" + partSpeech.toString()+"\"");
            
            if (length != -1)
                sb.append(",\"length\":" + length);
            
            if (position != -1)
                sb.append(",\"position\":" + position);
            
            if (frequency != -1)
                sb.append(",\"frequency\":" + frequency);            
            
            sb.append('}');            
            
            return sb.toString();
        }
    }


    @Override
    protected void run(String method) throws IOException
    {
        String text             = getString("text");
        boolean ret_pinyin      = getBoolean("ret_pinyin");
        boolean ret_len         = getBoolean("ret_len");
        boolean ret_pos         = getBoolean("ret_pos");
        boolean ret_fre         = getBoolean("ret_fre");
        boolean ret_ps          = getBoolean("ret_ps");
        
        if ( text == null || "".equals(text) )
        {
            response(false, 1, "Invalid Arguments");
            return;
        }
        
        JcsegGlobalResource resourcePool = (JcsegGlobalResource)globalResource;
        JcsegTokenizerEntry tokenizerEntry = resourcePool.getTokenizerEntry(method);
        if ( tokenizerEntry == null ) 
        {
            response(false, 1, "can't find tokenizer instance [" + method + "]");
            return;
        }
        
        try {
            ISegment seg = SegmentFactory
                    .createJcseg(JcsegTaskConfig.COMPLEX_MODE, 
                            new Object[]{ tokenizerEntry.getConfig(), tokenizerEntry.getDict()});
            
            
            IWord word = null;
            List<WordEntry> list = new ArrayList<WordEntry>();
            seg.reset(new StringReader(text));
            
            long s_time = System.nanoTime();
            while ( (word = seg.next()) != null ) 
            {
                WordEntry w =  new WordEntry();
                String value = word.getValue();
                w.setVal(value == null ? "" : value);
                
                if (ret_len)
                    w.setLength(word.getLength());
                
                if (ret_pinyin){
                    String pinyin = word.getPinyin();
                    w.setPinYin(pinyin == null ? "" : pinyin);
                }
                    
                
                if (ret_pos)
                    w.setPosition(word.getPosition());
                
                if (ret_fre)
                    w.setFrequency(word.getFrequency());
                
                if (ret_ps)
                    w.setPartSpeech(word.getPartSpeech());
                
                list.add(w);
                //clear the allocations of the word.
                word = null;
            }
            double c_time = (System.nanoTime() - s_time)/1E9;
            
            Map<String, Object> map = new HashMap<String, Object>();
            DecimalFormat df = new DecimalFormat("0.00000"); 
            map.put("took", Float.valueOf(df.format(c_time)));
            map.put("list", list);
            
            //response the request
            response(true, 0, map);
        } catch (JcsegException e) {
            response(false, -1, "Internal error...");
        }
    }
}
