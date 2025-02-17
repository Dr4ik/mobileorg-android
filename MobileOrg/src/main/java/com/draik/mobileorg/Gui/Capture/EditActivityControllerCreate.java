package com.draik.mobileorg.Gui.Capture;

import android.content.Intent;

import com.draik.mobileorg.OrgData.OrgNode;
import com.draik.mobileorg.util.OrgUtils;


public class EditActivityControllerCreate extends EditActivityController {
	
	public EditActivityControllerCreate(String defaultTodo) {
		this.node = new OrgNode();
		this.node.todo = defaultTodo;
		this.node.addAutomaticTimestamp();
	}
	
	public EditActivityControllerCreate(Intent intent, String defaultTodo) {
		this.node = OrgUtils.getCaptureIntentContents(intent);
		this.node.todo = defaultTodo;
		this.node.addAutomaticTimestamp();
	}
	
	@Override
	public boolean isNodeEditable() {
		return true;
	}
	
	public OrgNode getParentOrgNode() {
		return null;
	}

	@Override
	public void saveEdits(OrgNode newNode) {
		newNode.level = 1;
		newNode.write(resolver);		
	}

	@Override
	public String getActionMode() {
		return ACTIONMODE_CREATE;
	}

	@Override
	public boolean hasEdits(OrgNode newNode) {
		return !this.node.equals(newNode);
	}
}
