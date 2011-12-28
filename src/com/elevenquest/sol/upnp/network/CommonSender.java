package com.elevenquest.sol.upnp.network;

import java.util.Observer;


public abstract class CommonSender implements Observer {
	
	CommonSendHandler handler = null;
	
	public void setSenderHandler(CommonSendHandler handler) {
		this.handler = handler;
	}
	
	public void clearHandler() {
		handler = null;
	}
	
	public void sendData() throws Exception {
		Object rtnValue = handler.getSendObject();
		send(rtnValue);
	}
	
	/**
	 * ������ network layer�� �ִ� �ڷḦ �����ִ� ��Ȱ�� �ϴ� �޽��.
	 * 
	 * ���� : �ش� �޽��� non-Blocking method�� �����Ѵ�.
	 *       ����, Event�� �߻��Ǵ� ��쿡��, �ڷḦ ������ ��Ȱ�� �ϴ� ������ �����Ѵ�.
	 * 
	 * @throws Exception
	 */
	abstract protected void send(Object sendData) throws Exception;
	
	public void clear() {
		clearHandler();
	}

}
