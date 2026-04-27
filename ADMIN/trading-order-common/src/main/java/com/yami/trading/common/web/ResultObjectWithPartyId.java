package com.yami.trading.common.web;

import java.io.Serializable;


public class ResultObjectWithPartyId extends ResultObject {
	private String partyId;

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}
}
