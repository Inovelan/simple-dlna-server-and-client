package com.lgcns.sol.upnp.network;

import java.util.Vector;

public abstract class CommonReceiver {

	Vector<CommonHandler> handlerList = new Vector<CommonHandler>();

	public void addReceiveHandler(CommonHandler handler) {
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
	abstract public void listen() throws Exception;
	
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
		for ( CommonHandler handler : this.handlerList ) {
			handler.process(packet);
		}
	}

}
