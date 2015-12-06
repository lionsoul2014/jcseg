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
		
		private String 	val 	= null;
		private String 	pinyin  = null;
		private int 	len     = 0;
		private int     pos     = -1;
		private int     fre     = 0;	
		private int 	type    = -1;
		
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		public int getFre() {
			return fre;
		}
		public void setFre(int fre) {
			this.fre = fre;
		}
		public String getVal() {
			return val;
		}
		public void setVal(String val) {
			this.val = val;
		}
		public String getPinyin() {
			return pinyin;
		}
		public void setPinyin(String pinyin) {
			this.pinyin = pinyin;
		}
		public int getLen() {
			return len;
		}
		public void setLen(int len) {
			this.len = len;
		}
		public int getPos() {
			return pos;
		}
		public void setPos(int pos) {
			this.pos = pos;
		}
		
	}


	@Override
	protected void run(String method) throws IOException
	{
		String text 		= getString("text");
		String ret_pinyin 	= getString("ret_pinyin");
		String ret_len		= getString("ret_len");
		String ret_pos      = getString("ret_pos");
		String ret_fre		= getString("ret_fre");
		
		
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
				w.setVal(word.getValue());
				
				if (ret_len.equals("true"))
					w.setLen(word.getLength());
				
				if (ret_pinyin.equals("true"))
					w.setPinyin(word.getPinyin());
				
				if (ret_pos.equals("true"))
					w.setPos(word.getPosition());
				
				if (ret_fre.equals("true"))
					w.setFre(word.getFrequency());
				
				if (ret_fre.equals("true") )
					w.setType(word.getType());
				
				
				
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
