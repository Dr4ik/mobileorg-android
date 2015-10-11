This is the fork of [http://wiki.github.com/matburt/mobileorg-android/](http://wiki.github.com/matburt/mobileorg-android)

The original project seems dead for me so I've decide to make a personal fork for myself.

New features:
1. Possibility to set reminders time by using the ":REMINDER_TIME:" property
Example:
Test.org file on the PC:
<code>
* TODO Test entry
  :PROPERTIES:
  :REMINDER_TIME: 10
  :END:

  Some text here
</code>
Please notice that you can use values such as "5", "10", "15", "30" and so on. 
It looks like this is a limitation of the calendar app itself but I'm not sure (I need to investigate this).
