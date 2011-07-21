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

import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.protocol.server.Filter;
import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.RequestProcessor;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.impl.AbstractCollectionAdapter;
import org.apache.abdera.protocol.server.impl.SimpleWorkspaceInfo;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;

public abstract class AbstractProviderServlet extends AbderaServlet {

	private static final long serialVersionUID = 1L;
	
	protected abstract String getHref();
	
	protected abstract String getWorkspaceTitle();
	
	protected abstract AbstractCollectionAdapter getCollectionAdapter();
	
	protected Map<TargetType, RequestProcessor> getRequestProcessors() {
		return null;
	}
	
	protected Filter[] getFilters() {
		return new Filter[0];
	}

	@Override
	protected Provider createProvider() {
		AbstractCollectionAdapter ca = getCollectionAdapter();
        ca.setHref(getHref());

        SimpleWorkspaceInfo wi = new SimpleWorkspaceInfo();
        wi.setTitle(getWorkspaceTitle());
        wi.addCollection(ca);

        DefaultMediaProvider provider = new DefaultMediaProvider();
        
        provider.addWorkspace(wi);
        
        Map<TargetType, RequestProcessor> requestProcessors = new HashMap<TargetType, RequestProcessor>();
        requestProcessors.putAll(provider.getRequestProcessors());     
        requestProcessors.putAll(getRequestProcessors());
        provider.setRequestProcessors(requestProcessors);
        provider.init(getAbdera(), null);        
        provider.addFilter(new ServiceDocumentContentTypeFilter());
        provider.addFilter(getFilters());
        return provider;
    }
}