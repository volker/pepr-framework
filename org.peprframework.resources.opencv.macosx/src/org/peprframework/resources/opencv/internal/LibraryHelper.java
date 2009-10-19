/**
 * Copyright 2009 pepr Framework
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.peprframework.resources.opencv.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.sun.jna.Native;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
public class LibraryHelper {
	
	/**
	 * @param <T>
	 * @param name
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T loadLibrary(String name, Class<T> clazz) {
		T library = null;
		File tmp = null;
		try {
			tmp = File.createTempFile("pepr", "tmp");
			FileOutputStream out = new FileOutputStream(tmp);
			byte[] buffer = new byte[16 * 1024];
			InputStream in = LibraryHelper.class.getResourceAsStream(name);
			for (int bytes = 0; (bytes = in.read(buffer)) != -1;) {
				out.write(buffer, 0, bytes);
			}
			library = (T) Native.loadLibrary(tmp.getAbsolutePath(), clazz);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			tmp.deleteOnExit();			
		}
		
		return library;
	}
}
