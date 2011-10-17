package com.lgcns.sol.upnp.description;

public class ServiceDescription implements ICommonDescription {
	
	static final String SD_REPLACEABLE_PART_SERVICE_TYPE = "#SERVICE_TYPE#";
	static final String SD_REPLACEABLE_PART_SERVICE_ID = "#SERVICE_ID#";
	static final String SD_REPLACEABLE_PART_SCPD_URL = "#SCPD_URL#";
	static final String SD_REPLACEABLE_PART_CONTROL_URL = "#CONTROL_URL#";
	static final String SD_REPLACEABLE_PART_EVENTSUB_URL = "#EVENT_URL#";
	
	static final String DEVICE_DESCRIPTION_SERVICE_TEMPLATE =
		"<service>" +
		"<serviceType>" + SD_REPLACEABLE_PART_SERVICE_TYPE + "</serviceType>" +
		"<serviceId>" + SD_REPLACEABLE_PART_SERVICE_ID + "</serviceId>" +
		"<SCPDURL>" + SD_REPLACEABLE_PART_SCPD_URL + "</SCPDURL>" +
		"<controlURL>" + SD_REPLACEABLE_PART_CONTROL_URL + "</controlURL>" +
		"<eventSubURL>" + SD_REPLACEABLE_PART_EVENTSUB_URL + "</eventSubURL>" +
		"</service>";
	
	String serviceType;
	String serviceId;
	String scpdUrl;
	String controlUrl;
	String eventsubUrl;
	
	public String getDescription() {
		// TODO : Replace all replaceable parts.
		return DEVICE_DESCRIPTION_SERVICE_TEMPLATE;
	}
	
}
