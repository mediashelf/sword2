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

import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.WorkspaceManager;
import org.apache.abdera.protocol.server.processors.EntryRequestProcessor;

public class EntryRequestProcessorWrapper extends EntryRequestProcessor {
	public ResponseContext process(RequestContext context,
			WorkspaceManager workspaceManager,
			CollectionAdapter collectionAdapter) {
		System.out.println("* EntryRequestProcessorWrapper.process()");
		return super.process(context, workspaceManager, collectionAdapter);
	}
	
	protected ResponseContext processEntry(RequestContext context, CollectionAdapter adapter) {
		System.out.println("* EntryRequestProcessorWrapper.processEntry()");
		System.out.println("** method: " + context.getMethod());
        return super.processEntry(context, adapter);
	}
}
