package com.elevenquest.sol.upnp.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.elevenquest.sol.upnp.common.Logger;
import com.elevenquest.sol.upnp.description.DeviceDescription;
import com.elevenquest.sol.upnp.gena.Subscriber;
import com.elevenquest.sol.upnp.network.HttpRequestSender;
import com.elevenquest.sol.upnp.network.HttpTcpSender;
import com.elevenquest.sol.upnp.network.IHttpRequestSuplier;

public class UPnPEventManager extends UPnPBase {
	
	static UPnPEventManager defaultManager = null;
	HashMap<String, UPnPService> activeServiceList = null;
	BlockingQueue<UPnPService> waitingQueue = null;
	
	private UPnPEventManager() {
		activeServiceList = new HashMap<String,UPnPService>();
		waitingQueue = new ArrayBlockingQueue<UPnPService>(10);
	}
	
	public static UPnPEventManager getUPnPEventManager() {
		if ( defaultManager == null )
			defaultManager = new UPnPEventManager();
		return defaultManager;
	}
	
	public void addServiceToBeRegistered(UPnPService service) {
		service.setSubscribed(true);
		service.setSubscribeId(null);
		waitingQueue.offer(service);
	}
	
	public UPnPService pollWaitingServiceToBeRegistered() {
		return waitingQueue.poll();
	}
	
	public void addActiveGenaServiceList(String sid, UPnPService service) {
		activeServiceList.put(sid, service);
	}
	
	public void removeActiveGenaService(String sid) {
		activeServiceList.remove(sid);
	}
	
	public void removeActiveGenaService(UPnPService service) {
		Set<Entry<String,UPnPService>> entrySet = activeServiceList.entrySet();
		for ( Iterator<Entry<String,UPnPService>> entryIter = entrySet.iterator() ; entryIter.hasNext() ; ) {
			Entry<String,UPnPService> entry = entryIter.next();
			if ( entry.getValue().equals(service) ) {
				Logger.println(Logger.INFO, "Service ssid[" + entry.getKey() + "] found in active gena queue. We'll remove it from the list." );
				activeServiceList.remove(entry.getKey());
				break;
			}
		}
	}
	
	public UPnPService getActiveGenaService(String serviceId) {
		return this.activeServiceList.get(serviceId);
	}

	static class SubscribeEventThread extends Thread {
		UPnPService innerService = null;
		public SubscribeEventThread(UPnPService outerService) {
			innerService = outerService;
		}
		
		public void run() {
			try {
				HttpRequestSender sender = new HttpTcpSender(innerService.getDevice().getNetworkInterface(),
						innerService.getEventsubUrl() );
				IHttpRequestSuplier handler = new Subscriber(innerService, true);
				sender.setSenderHandler(handler);
				sender.sendData();
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}
}
