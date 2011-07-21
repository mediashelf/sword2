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

import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.impl.DefaultProvider;

/**
 * Subclass of DefaultProvider that adds support for Media entries.
 * 
 * @author Edwin Shin
 *
 */
public class DefaultMediaProvider extends DefaultProvider {
	public DefaultMediaProvider() {
		this("/");
	}

	public DefaultMediaProvider(String base) {
		super(base);
		routeManager.addRoute("media", base + ":collection/:entry/media/:media",
		TargetType.TYPE_MEDIA);
	}
}
