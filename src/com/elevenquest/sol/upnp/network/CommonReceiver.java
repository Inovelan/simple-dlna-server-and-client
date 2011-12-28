package com.elevenquest.sol.upnp.network;

import java.util.Vector;


public abstract class CommonReceiver {

	Vector<CommonReceiveHandler> handlerList = new Vector<CommonReceiveHandler>();

	public void addReceiveHandler(CommonReceiveHandler handler) {
		this.handlerList.add(handler);
	}
	
	public void clearHandler() {
		this.handlerList.clear();
	}
	
	/**
	 * ������ network layer�� �ִ� �ڷḦ �޴� ��Ȱ�� �ϴ� �޽��.
	 * ServerSocket�� ���, accept�� ȣ���ϴ� ��Ȱ.
	 * ����Ÿ ��Ʈ���� ���� ����, process �Լ��� ȣ���� �־�� �Ѵ�.
	 * 
	 * ���� : �ش� �޽��� Blocking methods�� �����Ѵ�. (Sync ���)
	 *       ����, Blocking�� ���� �ʴ� ���, �ش� Listener�� ���������� �ݺ��Ǿ�
	 *       ���� ���ϰ� �뷮���� �߻��ȴ�.
	 * 
	 * @throws Exception
	 */
	abstract protected Object listen() throws Exception;
	
	public void beforeReceive() {
		// TODO : API for hooking
	}
	
	public void afterReceive() {
		// TODO : API for hooking
	}
	
	public void receiveData() throws Exception {
		beforeReceive();
		Object rtnValue = listen();
		process(rtnValue);
		afterReceive();
	}
	
	public void clear() {
		clearHandler();
	}
	
	/**
	 * listen�� �ڷḦ ������ ó���ϴ� CommonHandler�� ȣ���Ѵ�.
	 * ���������� ��ϵ�(addReceiveHandler) Handler�� ������ ȣ���� �� �ִ�. 
	 * 
	 * @param packet
	 */
	public void process(Object packet) {
		for ( CommonReceiveHandler handler : this.handlerList ) {
			handler.process(packet);
		}
	}

}
