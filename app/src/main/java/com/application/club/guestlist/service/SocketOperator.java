package com.application.club.guestlist.service;



import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;

//import io.socket.IOAcknowledge;
//import io.socket.IOCallback;
//import io.socket.SocketIO;
//import io.socket.SocketIOException;


import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import static io.reactivex.annotations.SchedulerSupport.IO;

//import com.chatt.ChatListener;
//import com.chatt.types.MessageInfo;

//http://nkzawa.tumblr.com/post/46850605422/connecting-to-a-socket-io-server-from-android
//http://stackoverflow.com/questions/25539491/netty-socketio-server-how-to-handler-socket-io-client-authentification-request

public class SocketOperator {   
	private EventListener eventListener;
	//private static String socketURL = "http://192.168.43.159:3000/";
	
//	private static String socketURL = "http://162.144.69.194:3000/";
	 
	//private static String socketURL = "http://192.168.0.102:3080/";// macbook address  http://192.168.0.100:3080/
	//private static String socketURL = "http://198.167.140.169:3080/";// viprus unix box address
	//private static String socketURL = "http://192.168.0.4:3050/";//"http://192.168.0.9:3080/";//"http://199.180.133.121:3080/";

	private static String socketURL = "http://199.180.133.121:3050/";// demo

	//private static String socketURL = "http://199.180.133.121:3080/";// Production




	//https://github.com/fatshotty/socket.io-java-client
	
	public SocketIO socket;

	int reconnectionTryCount = 5;
	
	public SocketOperator(EventListener eventListener){
		this.eventListener = eventListener;
		//if(socket == null)
			setupClient(); 
    }
	
	
	public void setupClient(){
		try {
			//npm install socket.io@0.9.16 - use this version of socketIO
			//System.out.println("Initializing Connection.");
			
			socket = new SocketIO(socketURL);


			socket.connect(new IOCallback() {
				@Override
				public void onDisconnect() {
					System.out.println("Connection terminated.");
				}

				@Override
				public void onConnect() {
					System.out.println("Connection established");
				}

				@Override
				public void onMessage(String data, IOAcknowledge ioAcknowledge) {

					//System.out.println("Server said: " + data);
					try {
						eventListener.eventReceived(data);
						socket.disconnect();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();//// null pointer error is check it IMP
					}

				}

				@Override
				public void onMessage(JsonElement jsonElement, IOAcknowledge ioAcknowledge) {



				}

				@Override
				public void on(String event, IOAcknowledge ioAcknowledge, JsonElement... jsonElements) {
					System.out
							.println("Server triggered event '" + event + "'");

				}

				@Override
				public void onError(SocketIOException socketIOException) {
					System.out.println("an Error occured");
					socketIOException.printStackTrace();
				}
			});


//			socket.connect(new IOCallback() {
//
//
//				void onMessage(JsonElement var1, IOAcknowledge var2){
//
//				}
//				@Override
//				public void onMessage(JSONObject json, IOAcknowledge ack) {
//					//System.out.println("json");
//				}
//
//				@Override
//				public void onMessage(String data, IOAcknowledge ack) {
//
//					//System.out.println("Server said: " + data);
//					try {
//						eventListener.eventReceived(data);
//						socket.disconnect();
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}catch (Exception e) {
//						// TODO: handle exception
//						e.printStackTrace();//// null pointer error is check it IMP
//					}
//				}
//
//				@Override
//				public void onError(SocketIOException socketIOException) {
//					System.out.println("an Error occured");
//					socketIOException.printStackTrace();
//					try {
//						//setupClient();
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					System.out.println("Reconnected");
//				}
//
//				@Override
//				public void onDisconnect() {
//					//System.out.println("Connection terminated.");
//					try {
//						//socket = new SocketIO("http://192.168.2.3:3000");
//						//setupClient();
////						System.out.println("Reconnected");
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//				}
//
//				@Override
//				public void onConnect() {
////					System.out.println("Connection established");
//				}
//
//				@Override
//				public void on(String event, IOAcknowledge ack, Object... args) {
////					System.out
////							.println("Server triggered event '" + event + "'");
//				}
//			});

			
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}catch (Exception ex){
			ex.printStackTrace();
			while (reconnectionTryCount > -1){
				setupClient();
				reconnectionTryCount--;
				break;
			}
		}
	}
	
	private void sendRequest(JSONObject event){
		
		try {
			// This line is cached until the connection is established.
			//System.out.println("Sending message to server.");
			socket.send(event.toString());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		
	}
	
	public void sendMessage(JSONObject event)
			throws  JSONException {		
		sendRequest(event);
	}
	
}

