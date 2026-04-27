package com.yami.trading.admin.controller.loanOrder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LoanLock {
	private static final Set<String> filter = Collections.synchronizedSet(new HashSet<String>());

	public static boolean add(String partyId) {
		if (!filter.add(partyId)) {
			return false;
		} else {
			return true;
		}
	}

	public static void remove(String partyId) {
		filter.remove(partyId);
	}
}
