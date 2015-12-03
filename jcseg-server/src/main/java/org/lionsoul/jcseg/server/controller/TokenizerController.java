package org.lionsoul.jcseg.server.controller;

import java.io.IOException;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.lionsoul.jcseg.server.JcsegController;
import org.lionsoul.jcseg.server.GlobalProjectSetting;
import org.lionsoul.jcseg.server.GlobalResourcePool;
import org.lionsoul.jcseg.server.TokenizerEntry;
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
			GlobalProjectSetting setting,
			GlobalResourcePool resourcePool, 
			UriEntry uriEntry,
			Request baseRequest, 
			HttpServletRequest request,
			HttpServletResponse response) throws IOException
	{
		super(setting, resourcePool, uriEntry, baseRequest, request, response);
	}

	@Override
	protected void run(String method) throws IOException
	{
		String text = getString("text");
		
		if ( text == null || "".equals(text) )
		{
			response(false, 1, "Invalid Arguments");
			return;
		}
		
		
		TokenizerEntry entry = resourcePool.getTokenizerEntry(method);
		
		if ( entry == null)
		    response(false, 1, "Invalid method...");
		
		
		try {
			
			ISegment seg = SegmentFactory
					.createJcseg(JcsegTaskConfig.COMPLEX_MODE, 
							new Object[]{ entry.getConfig(), entry.getDic()});
			
			long s_time = System.nanoTime();
			
		    IWord word 				= null;
			ArrayList<String> list 	= new ArrayList<String>();
			
			seg.reset(new StringReader(text));
			
			while ( (word = seg.next()) != null ) 
			{
				String val = word.getValue();
				list.add(val);
				//clear the allocations of the word.
				word = null;
			}
			
			
			Map<String, Object> map = new HashMap<String, Object>();
			DecimalFormat df = new DecimalFormat("0.00000"); 
			map.put("took", df.format((System.nanoTime() - s_time)/1E9));
			map.put("list", list);
			
			//response the request
			response(true, 0, map);
		} catch (JcsegException e) {
			response(false, -1, "Internal error...");
		}
	}
}
