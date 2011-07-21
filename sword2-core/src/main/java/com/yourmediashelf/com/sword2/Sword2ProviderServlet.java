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
package com.yourmediashelf.com.sword2;

import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.protocol.server.Filter;
import org.apache.abdera.protocol.server.RequestProcessor;
import org.apache.abdera.protocol.server.TargetType;

import com.yourmediashelf.atompub.AbstractProviderServlet;
import com.yourmediashelf.atompub.BrowserXmlContentTypeFilter;
import com.yourmediashelf.atompub.EntryRequestProcessorWrapper;

public abstract class Sword2ProviderServlet extends AbstractProviderServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected Filter[] getFilters() {
		Filter[] filters = {new BrowserXmlContentTypeFilter()};
		return filters;
	}
	
	@Override
	protected Map<TargetType, RequestProcessor> getRequestProcessors() {
		Map<TargetType, RequestProcessor> requestProcessors = new HashMap<TargetType, RequestProcessor>();
		requestProcessors.put(TargetType.TYPE_SERVICE, new Sword2ServiceRequestProcessor());
		requestProcessors.put(TargetType.TYPE_ENTRY, new EntryRequestProcessorWrapper());
		return requestProcessors;
	}
}