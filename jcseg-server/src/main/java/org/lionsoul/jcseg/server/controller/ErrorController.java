package org.lionsoul.jcseg.server.controller;

import java.io.IOException;

import org.lionsoul.jcseg.server.Controller;

/**
 * common error handler
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class ErrorController extends Controller
{
	@Override
	protected void doRequest() throws IOException
	{
		this.response(
				false, 
				-1, 
				"No such controller found for " + request.getRequestURI()
		);
	}
}
