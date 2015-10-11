package com.draik.mobileorg.test.Synchronizers;

import java.util.ArrayList;

import com.draik.mobileorg.OrgData.OrgNode;
import com.draik.mobileorg.OrgData.OrgNodeDate;

import android.test.AndroidTestCase;

public class CalendarSyncServiceTest extends AndroidTestCase {
	
	public void testOrgNodePayloadGetDates() {
		OrgNode node = new OrgNode();
		node.setPayload("<2012-09-13 Thu>");
		ArrayList<OrgNodeDate> dates = node.getOrgNodePayload().getDates(node.getCleanedName());
		
		assertEquals(1, dates.size());
	}
}
