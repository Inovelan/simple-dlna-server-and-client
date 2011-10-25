package com.lgcns.sol.upnp.server;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.lgcns.sol.upnp.discovery.SSDPMessage;
import com.lgcns.sol.upnp.discovery.UPnPDevice;
import com.lgcns.sol.upnp.exception.AbnormalException;
import com.lgcns.sol.upnp.network.CommonReceiveHandler;
import com.lgcns.sol.upnp.network.CommonReceiver;
import com.lgcns.sol.upnp.network.CommonSendHandler;
import com.lgcns.sol.upnp.network.CommonSender;
import com.lgcns.sol.upnp.network.UDPReceiver;
import com.lgcns.sol.upnp.network.UDPSender;

public class CommonServer {

	static int CORE_INI_THREAD = 5;
	static int CORE_MAX_THREAD = 8;
	static int KEEP_ALIVE_TIME = 5;	// 5 sec.
	static TimeUnit BASE_TIME_UNIT = TimeUnit.SECONDS;  

	CommonReceiver receiver = null;
	CommonSender sender = null;
	BlockingQueue<Runnable> queue = null;
	ThreadPoolExecutor threadPool = null;
	
	SendEvent event = null;
	
	boolean needStop = false;
	
	public CommonServer() {
		queue = new ArrayBlockingQueue<Runnable>(CORE_MAX_THREAD);
		threadPool = new ThreadPoolExecutor(CORE_INI_THREAD, CORE_MAX_THREAD, KEEP_ALIVE_TIME, BASE_TIME_UNIT, queue);
	}
	
	public void setReceiver(CommonReceiver receiver) {
		this.receiver = receiver;
	}
	
	public void setSender(CommonSender sender, SendEvent event) {
		this.sender = sender;
		this.event = event;
	}
	
	int numberOfErrors = 0;

	public void startServer() throws AbnormalException {
		System.out.println("Start Server...." + threadPool.toString() + ":" + receiver + ":" + sender );
		if ( receiver == null && sender == null ) {
			throw new AbnormalException("A listener or a Sender isn't set. Before use this api, you should set listener or sender in this class.");
		}
		if ( receiver != null ) {
			needStop = false;
			threadPool.execute(new Runnable() {
				public void run() {
					try {
						threadPool.execute(new Runnable() {
							public void run() {
								while( !needStop ) {
									try {
										receiver.receiveData();
										Thread.sleep(1000);
									} catch ( Exception e ) {
										numberOfErrors++;
										System.out.println("Listener can't Listen.");
										e.printStackTrace();
									}
									if ( numberOfErrors > 3 )
										needStop = true;
								}
							}
						});
					} catch ( Exception e ) {
						System.out.println("There are some errors in Thread Pool.");
						e.printStackTrace();
					}
				}
			});
		}
		if ( sender != null ) {
			needStop = false;
			threadPool.execute(new Runnable() {
				public void run() {
					try {
						threadPool.execute(new Runnable() {
							public void run() {
								do {
									try {
										sender.sendData();
										if ( !needStop && event.getType() == SendEvent.SEND_EVENT_TYPE_TIME_UNLIMINITED )
											Thread.sleep(event.getDelayTimeInMillisec());
									} catch ( Exception e ) {
										numberOfErrors++;
										System.out.println("Sender can't send.");
										e.printStackTrace();
									}
									if ( numberOfErrors > 3 )
										needStop = true;
								} while( !needStop && event.getType() == SendEvent.SEND_EVENT_TYPE_TIME_UNLIMINITED );
							}
						});
					} catch ( Exception e ) {
						System.out.println("There are some errors in Thread Pool.");
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	public void stopServer() {
		needStop = true;
		threadPool.purge();
		threadPool.shutdown();
		/*
		List<Runnable> unreleasedTasks = threadPool.shutdownNow();
		for ( Runnable oneTask : unreleasedTasks ) {
			try {
				// TODO : How to kill an unreleased runnable thread. I don't know. 
				System.out.println("Unreleased task:" + oneTask);
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
		*/
		System.out.println("Stop Server...." + threadPool.toString() + ":" + receiver + ":" + sender );
	}

	
}
