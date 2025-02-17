package com.draik.mobileorg.test.OrgData;

import android.content.Context;

import com.draik.mobileorg.OrgData.OrgDatabase;
import com.draik.mobileorg.OrgData.OrgNode;

public class OrgDatabaseStub extends OrgDatabase {

	int fastInsertNodeCalls = 0;
	int fastInsertNodePayloadCalls = 0;
	
	public OrgDatabaseStub(Context context) {
		super(context);
	}

	@Override
	public long fastInsertNode(OrgNode node) {
		fastInsertNodeCalls++;
		return super.fastInsertNode(node);
	}
	
	@Override
	public void fastInsertNodePayload(Long id, final String payload) {
		fastInsertNodePayloadCalls++;
		super.fastInsertNodePayload(id, payload);
	}
}
