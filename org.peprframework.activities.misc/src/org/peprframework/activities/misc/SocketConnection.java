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
package org.peprframework.activities.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.xml.bind.annotation.XmlRootElement;

import org.peprframework.core.Activity;


/**
 * @author Stefan Scherer
 * @version 1.0
 *
 */
@XmlRootElement(name = "SocketConnection")
public class SocketConnection extends Activity<SocketConnectionInput,SocketConnectionOutput,SocketConnectionConfiguration> {

	public static final String SOCKETCONNECT_ID = "org.peprframework.activities.misc.socketconnect";
	private transient Socket socket = null;
	private transient BufferedReader in = null;
	private transient PrintWriter out = null;
	
	@Override
	public String getId() {
		return SOCKETCONNECT_ID;
	}
	
	@Override
	public void initialize() {
		super.initialize();
		
		try {
			socket = new Socket(getConfiguration().getAddress(), getConfiguration().getPort());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (UnknownHostException e) {
			System.out.println("Unknown host: "+ getConfiguration().getAddress()+":"+getConfiguration().getPort());
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("SocketConnection: initialized");
	}

	@Override
	public SocketConnectionOutput handleMessage(SocketConnectionInput msg) {
		
		out.println(msg.toSend);
		
		SocketConnectionOutput output = new SocketConnectionOutput();
		
		try {
			output.result = Integer.parseInt(in.readLine());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("No I/O from server.");
			e.printStackTrace();
		}
		
		return output;
	}
	
	@Override
	public void terminate() {
		try {
			socket.close();
			out.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		super.terminate();
	}

}
