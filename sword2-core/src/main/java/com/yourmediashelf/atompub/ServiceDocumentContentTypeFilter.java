/**
 * Copyright (C) 2011 MediaShelf <http://www.yourmediashelf.com/>
 *
 * This file is part of sword2.
 *
 * sword2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sword2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with sword2.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.yourmediashelf.atompub;

import org.apache.abdera.protocol.server.Filter;
import org.apache.abdera.protocol.server.FilterChain;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.context.ResponseContextWrapper;

/**
 * Sets the Content-Type response header for Service Document requests to 
 * "text/xml" if the User-Agent contains the string "Mozilla".
 * 
 * This is just a convenience so that service documents can be rendered 
 * in-browser.
 * 
 * @author Edwin Shin
 *
 */
public class ServiceDocumentContentTypeFilter implements Filter {
	@Override
	public ResponseContext filter(RequestContext request, FilterChain chain) {
		String userAgent = request.getHeader("User-Agent");
		if (userAgent != null && userAgent.contains("Mozilla")
				&& request.getTarget().getType() == TargetType.TYPE_SERVICE) {
			ResponseContextWrapper response = new ResponseContextWrapper(
					chain.next(request));
			response.setContentType("text/xml");
			return response;
		}
		return chain.next(request);
	}
}
