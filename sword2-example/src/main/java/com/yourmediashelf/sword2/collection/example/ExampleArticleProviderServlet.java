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
package com.yourmediashelf.sword2.collection.example;

import org.apache.abdera.protocol.server.impl.AbstractCollectionAdapter;

import com.yourmediashelf.com.sword2.Sword2ProviderServlet;

public class ExampleArticleProviderServlet extends Sword2ProviderServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected String getHref() {
		return "articles";
	}

	@Override
	protected String getWorkspaceTitle() {
		return "SWORD2 Example Article Collection Workspace";
	}

	@Override
	protected AbstractCollectionAdapter getCollectionAdapter() {
		return new ExampleArticleCollection();
	}
}