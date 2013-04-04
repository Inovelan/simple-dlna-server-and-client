package com.elevenquest.sol.upnp.discovery;

import com.elevenquest.sol.upnp.model.UPnPDeviceManager;
import com.elevenquest.sol.upnp.network.HttpRequest;
import com.elevenquest.sol.upnp.network.HttpResponse;
import com.elevenquest.sol.upnp.network.IHttpRequestHandler;

public class SSDPReceiveHandler implements IHttpRequestHandler {

	@Override
	public HttpResponse process(HttpRequest packet) {
		SSDPRequest message = new SSDPRequest(packet);
 		UPnPDeviceManager manager = UPnPDeviceManager.getDefaultDeviceManager();
		try {
			if ( SSDPMessage.ID_NT_SUBTYPE_SSDPALIVE.equalsIgnoreCase(
					message.getHeaderValue(SSDPMessage.ID_UPNP_DISCOVERY_NT_SUBTYPE)) ) {
				manager.addDevice(message.getDeviceBaseInfo());
			} else if (SSDPMessage.ID_NT_SUBTYPE_SSDPBYEBYE.equalsIgnoreCase(
					message.getHeaderValue(SSDPMessage.ID_UPNP_DISCOVERY_NT_SUBTYPE))) {
				manager.removeDevice(message.getDeviceBaseInfo().getUuid());
			} else if (SSDPMessage.ID_NT_SUBTYPE_SSDPUPDATE.equalsIgnoreCase(
					message.getHeaderValue(SSDPMessage.ID_UPNP_DISCOVERY_NT_SUBTYPE))) {
				manager.updateDevice(message.getDeviceBaseInfo().getUuid());
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return null;
	}

}
