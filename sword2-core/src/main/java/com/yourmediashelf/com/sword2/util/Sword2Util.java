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
package com.yourmediashelf.com.sword2.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Sword2Util {
	/**
	 * 100k buffer
	 */
	private static final byte[] buffer = new byte[100000];
	
	/**
     * Copy an InputStream to an OutputStream. 
     * While this method will automatically close the destination OutputStream,
     * the caller is responsible for closing the source InputStream.
     * 
     * @param source
     * @param destination
     * @return <code>true</code> if the operation was successful;
     *         <code>false</code> otherwise (which includes a null input).
     * @see http://java.sun.com/docs/books/performance/1st_edition/html/JPIOPerformance.fm.html#22980
     */
    public static boolean copy(InputStream source, OutputStream destination) {
        try {
            while (true) {
                synchronized (buffer) {
                    int amountRead = source.read(buffer);
                    if (amountRead == -1) {
                        break;
                    }
                    destination.write(buffer, 0, amountRead);
                }
            }
            destination.flush();
            destination.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
