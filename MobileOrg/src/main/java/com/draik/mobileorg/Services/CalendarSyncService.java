package com.draik.mobileorg.Services;

import java.util.ArrayList;
import java.util.HashSet;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.draik.mobileorg.OrgData.CalendarEntriesParser;
import com.draik.mobileorg.OrgData.CalendarEntry;
import com.draik.mobileorg.OrgData.OrgFile;
import com.draik.mobileorg.OrgData.OrgNode;
import com.draik.mobileorg.OrgData.OrgNodeDate;
import com.draik.mobileorg.OrgData.OrgNodePayload;
import com.draik.mobileorg.OrgData.OrgProviderUtils;
import com.draik.mobileorg.util.MultiMap;
import com.draik.mobileorg.util.OrgFileNotFoundException;
import com.draik.mobileorg.util.OrgNodeNotFoundException;
import com.draik.mobileorg.util.OrgUtils;

public class CalendarSyncService extends Service implements
		SharedPreferences.OnSharedPreferenceChangeListener {
	public final static String CLEARDB = "clearDB";
	public final static String PULL = "pull";
	public final static String PUSH = "push";
	public final static String FILELIST = "filelist";
	// The name of the org-property holding the availability status.
	public final static String ORG_PROP_BUSY = "BUSY";

	private Context context;
	private SharedPreferences sharedPreferences;
	private ContentResolver resolver;

	private CalendarWrapper calendarWrapper;

	private boolean showDone = false;
	private boolean showPast = true;
	private boolean showHabits = false;
	private boolean pullEnabled = false;
	private boolean pullDelete = false;

	private HashSet<String> activeTodos = new HashSet<String>();
	private HashSet<String> allTodos = new HashSet<String>();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.resolver = getContentResolver();
		this.context = getBaseContext();
		this.sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		this.sharedPreferences.registerOnSharedPreferenceChangeListener(this);
		this.calendarWrapper = new CalendarWrapper(context);
		refreshPreferences();
	}

	@Override
	public void onDestroy() {
		this.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null)
			return 0;
		
		refreshPreferences();
		
		final String[] fileList = intent.getStringArrayExtra(FILELIST);
		final boolean clearDB = intent.getBooleanExtra(CLEARDB, false);
		final boolean pull = intent.getBooleanExtra(PULL, false);
		final boolean push = intent.getBooleanExtra(PUSH, false);
		
		new Thread() {
			public void run() {
				if (clearDB) {
					if (fileList != null)
						calendarWrapper.deleteFileEntries(fileList);
					else
						calendarWrapper.deleteEntries();
				} 
				
				if (push) {
					if (fileList != null)
						syncFiles(fileList);
					else
						syncFiles();
					
					if (pullEnabled)
						assimilateCalendar();
				}
				
				if (pull) {
					assimilateCalendar();
				}
			}
		}.start();

		return 0;
	}

	private void syncFiles() {
		ArrayList<String> files = OrgProviderUtils.getFilenames(resolver);
		files.remove(OrgFile.AGENDA_FILE);
		for (String filename : files)
			syncFile(filename);
	}

	private void syncFiles(String[] files) {
		for (String filename : files) {
			if (filename.equals(OrgFile.AGENDA_FILE) == false) {
				syncFile(filename);
			}
		}
	}


	private int inserted = 0;
	private int deleted = 0;
	private int unchanged = 0;

	private void syncFile(String filename) {
		inserted = 0;
		deleted = 0;
		unchanged = 0;

		Cursor scheduledQuery;
		try {
			scheduledQuery = OrgProviderUtils.getFileSchedule(filename,
					this.showHabits, resolver);
		} catch (OrgFileNotFoundException e) {
			return;
		}

		MultiMap<CalendarEntry> entries = getCalendarEntries(filename);

		while (scheduledQuery.isAfterLast() == false) {
			try {
				OrgNode node = new OrgNode(scheduledQuery);
				syncNode(node, entries, filename);
			} catch (OrgNodeNotFoundException e) {}
			scheduledQuery.moveToNext();
		}
		scheduledQuery.close();

		removeCalendarEntries(entries);

		Log.d("MobileOrg", "Calendar (" + filename + ") Inserted: " + inserted
				+ " and deleted: " + deleted + " unchanged: " + unchanged);
	}

	private void syncNode(OrgNode node, MultiMap<CalendarEntry> entries,
			String filename) {
		for (OrgNodeDate date : node.getOrgNodePayload().getDates(
				node.getCleanedName())) {
			if (shouldInsertEntry(node.todo, date))
				tryToInsertNode(entries, date, filename, node);
		}
	}

	private void tryToInsertNode(MultiMap<CalendarEntry> entries,
			OrgNodeDate date, String filename, OrgNode node) {
		CalendarEntry insertedEntry = entries.findValue(date.beginTime, date);
		OrgNodePayload payload = node.getOrgNodePayload();

		CalendarEntry newEntry = new CalendarEntry(date, payload, filename);

		if (insertedEntry != null && insertedEntry.equals(newEntry)) {
			entries.remove(date.beginTime, insertedEntry);
			unchanged++;
			return;
		}

		calendarWrapper.insertEntry(newEntry);
		inserted++;
	}

	private boolean shouldInsertEntry(String todo, OrgNodeDate date) {
		boolean isTodoActive = true;
		if (TextUtils.isEmpty(todo) == false && allTodos.contains(todo))
			isTodoActive = this.activeTodos.contains(todo);
		
		if (this.showDone == false && isTodoActive == false)
			return false;
		

		if (this.showPast == false && date.isInPast())
			return false;
		
		return true;
	}
	
	private MultiMap<CalendarEntry> getCalendarEntries(String filename) {
		refreshPreferences();

		Cursor query = calendarWrapper.getCalendarCursor(filename);

		MultiMap<CalendarEntry> map = new MultiMap<CalendarEntry>();
		CalendarEntriesParser entriesParser = new CalendarEntriesParser(calendarWrapper.calendar.events,
				query);

		while (query.isAfterLast() == false) {
			CalendarEntry entry = entriesParser.getEntryFromCursor(query);
			map.put(entry.dtStart, entry);

			query.moveToNext();
		}

		return map;
	}

	private void removeCalendarEntries(MultiMap<CalendarEntry> entries) {
		for (Long entryKey : entries.keySet()) {
			for (CalendarEntry entry : entries.get(entryKey)) {
				calendarWrapper.deleteEntry(entry);
				deleted++;
			}
		}
	}
	
	
	private void assimilateCalendar() {
		Cursor eventsQuery = calendarWrapper.getUnassimilatedCalendarCursor();
		
		CalendarEntriesParser entriesParser = new CalendarEntriesParser(
				calendarWrapper.calendar.events, eventsQuery);
				
		while(!eventsQuery.isAfterLast()) {
			CalendarEntry entry = entriesParser.getEntryFromCursor(eventsQuery);
			Cursor cur = calendarWrapper.getUnassimilatedCalendarRemindersCursor(entry.id);

			while(!cur.isAfterLast()) {
				entry.reminderTime = cur.getInt(cur.getColumnIndexOrThrow(calendarWrapper.calendar.reminders.MINUTES));
				cur.moveToNext();
			}
            
            cur.close();
            
			OrgNode node = entry.convertToOrgNode();
			
			OrgFile captureFile = OrgProviderUtils
					.getOrCreateCaptureFile(getContentResolver());
			node.fileId = captureFile.id;
			node.parentId = captureFile.nodeId;
			node.level = 1;
						
			node.write(getContentResolver());
			
			if (this.pullDelete)
				calendarWrapper.deleteEntry(entry);
			
			eventsQuery.moveToNext();
		}
		
		eventsQuery.close();
		OrgUtils.announceSyncDone(this);
	}


	private void refreshPreferences() {
		this.pullEnabled = sharedPreferences.getBoolean("calendarPull", false);
		this.pullDelete = sharedPreferences.getBoolean("calendarPullDelete", false);
		this.showDone = sharedPreferences.getBoolean("calendarShowDone", true);
		this.showPast = sharedPreferences.getBoolean("calendarShowPast", true);
		this.showHabits = sharedPreferences.getBoolean("calendarHabits", true);
		this.activeTodos = new HashSet<String>(
				OrgProviderUtils.getActiveTodos(resolver));
		this.allTodos = new HashSet<String>(OrgProviderUtils.getTodos(resolver));
		this.calendarWrapper.refreshPreferences();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.startsWith("calendar")) {
			syncFiles();
		}
	}
}
