package com.draik.mobileorg.OrgData;

import android.text.TextUtils;

import com.draik.mobileorg.Services.CalendarSyncService;
import com.draik.mobileorg.Services.CalendarWrapper;

public class CalendarEntry {
	public String title = "";
	public String description = "";
	public String location = "";
	public int reminderTime = 0;
	public long id = -1;
	public long dtStart = 0;
	public long dtEnd = 0;
	public int allDay = 0;
	public String busy = "";

	public CalendarEntry(){}

	public CalendarEntry(OrgNodeDate date, OrgNodePayload payload, String filename)
	{
		this.dtStart = date.beginTime;
		this.dtEnd = date.endTime;
		this.allDay = date.allDay;
		this.reminderTime = Integer.valueOf(payload.getReminderTime());
		this.busy = payload.getProperty(CalendarSyncService.ORG_PROP_BUSY);
		this.description = CalendarWrapper.CALENDAR_ORGANIZER + ":" + filename + "\n" + payload.getCleanedPayload();
		this.title = date.getTitle();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof OrgNodeDate) {
			OrgNodeDate entry = (OrgNodeDate) o;
			return this.dtStart == entry.beginTime
					&& this.dtEnd == entry.endTime
					&& entry.getTitle().startsWith(this.title);
		}

		return super.equals(o);
	}

	public  boolean equals(CalendarEntry other)
	{
		return title.equals(other.title)
				&& description.equals(other.description)
				&& dtStart == other.dtStart
				&& dtEnd == other.dtEnd
				&& allDay == other.allDay
				&& reminderTime == other.reminderTime
				&& location.equals(other.location);
	}

	public OrgNode convertToOrgNode() {
		OrgNode node = new OrgNode();
		node.name = this.title;

		boolean isAllDay = allDay > 0;
		String date = OrgNodeDate.getDate(this.dtStart, this.dtEnd, isAllDay);
		String formatedDate = OrgNodeTimeDate.formatDate(
				OrgNodeTimeDate.TYPE.Timestamp, date);

		String payload = formatedDate + "\n" + this.description;
		String props = "";

		if (!this.busy.isEmpty())
			props += "\n:" + CalendarSyncService.ORG_PROP_BUSY + ": " + String.valueOf(this.busy);

		if (this.reminderTime > 0)
			props += "\n:REMINDER_TIME: " + String.valueOf(this.reminderTime);

		if (!props.isEmpty())
		{
			payload += "\n:PROPERTIES:";
			payload += props;
			payload += "\n:END:";
		}

		if (!TextUtils.isEmpty(this.location))
			payload += "\n:LOCATION: " + this.location;

		node.setPayload(payload);
		return node;
	}
}
