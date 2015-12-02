package org.lionsoul.jcseg.server.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.lionsoul.jcseg.extractor.SummaryExtractor;
import org.lionsoul.jcseg.extractor.impl.TextRankSummaryExtractor;
import org.lionsoul.jcseg.server.GlobalResourcePool;
import org.lionsoul.jcseg.server.core.Controller;
import org.lionsoul.jcseg.server.core.UriEntry;
import org.lionsoul.jcseg.tokenizer.SentenceSeg;
import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.ISegment;
import org.lionsoul.jcseg.tokenizer.core.JcsegException;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;
import org.lionsoul.jcseg.tokenizer.core.SegmentFactory;

/**
 * tokenize service handler
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class SummaryController extends Controller
{

	public SummaryController(
			GlobalResourcePool resourcePool, 
			UriEntry uriEntry, 
			Request baseRequest, 
			HttpServletRequest request,
			HttpServletResponse response) throws IOException 
	{
		super(resourcePool, uriEntry, baseRequest, request, response);
	}

	@Override
	protected void run(String method) throws IOException
	{
		/*StringBuilder sb = new StringBuilder();
        java.io.BufferedReader reader = request.getReader();

        String line;
        while((line = reader.readLine()) != null){
        	sb.append(line);
        }
        System.out.println("post: "+sb.toString());
        */
		
		String text = getString("text");
		int length = getInt("length", 86);
		if ( text == null || "".equals(text) )
		{
			this.response(false, 1, "Invalid Arguments");
			return;
		}
		
		String summary = null;
		JcsegTaskConfig config = resourcePool.getConfig("extractor");
		ADictionary dic = resourcePool.getDic("main");
		
		try {
			ISegment seg = SegmentFactory
					.createJcseg(JcsegTaskConfig.COMPLEX_MODE, new Object[]{config, dic});
			SummaryExtractor extractor = new TextRankSummaryExtractor(seg, new SentenceSeg());
			summary = extractor.getSummaryFromString(text, length);
		} catch (JcsegException e) {
			
		}
		
		if ( summary == null ) {
			this.response(false, -1, "Internal error...");
		} else {
			this.response(true, 1, summary);
		}
	}

}
