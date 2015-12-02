package org.lionsoul.jcseg.server.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.lionsoul.jcseg.server.GlobalResourcePool;
import org.lionsoul.jcseg.server.core.Controller;
import org.lionsoul.jcseg.server.core.UriEntry;

/**
 * keywords extractor handler
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class KeywordsController extends Controller
{

	public KeywordsController(
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
		this.response(
				false, 
				-1, 
				"keywords coming soon ... "
		);
	}

}
