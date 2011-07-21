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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExampleArticle {
	private String id;
	private String title;
	private String author;
	private String text;
	private Date date;
	private Map<String, byte[]> newmedia;
	private byte[] media;
	
	public ExampleArticle() {
		newmedia = new HashMap<String, byte[]>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public byte[] getMedia() {
		return media;
	}
	
	public void setMedia(byte[] media) {
		this.media = media;
	}
	
	public byte[] getNewMedia(String id) {
		return newmedia.get(id);
	}
	
	public void setNewMedia(String id, byte[] media) {
		this.newmedia.put(id, media);
	}
}
