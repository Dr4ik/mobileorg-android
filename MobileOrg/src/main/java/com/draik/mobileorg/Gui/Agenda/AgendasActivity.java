package com.draik.mobileorg.Gui.Agenda;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.draik.mobileorg.R;

public class AgendasActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.agendas);
		
		getSupportActionBar().setTitle("Agenda");
	}
}
