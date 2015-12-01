package org.lionsoul.jcseg.server.controller;

import java.io.IOException;

import org.lionsoul.jcseg.server.Controller;

/**
 * tokenize service handler
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class SummaryController extends Controller
{

	@Override
	protected void doRequest() throws IOException
	{
		this.response(
				false, 
				-1, 
				"summary coming soon ... "
		);
	}

}
