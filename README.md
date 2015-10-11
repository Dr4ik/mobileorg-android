
##Cmon a new fork why?
The original project seems dead for me so I've decide to make a personal fork for myself.
This link to an original project [http://github.com/matburt/mobileorg-android/](http://github.com/matburt/mobileorg-android)

##New features:
1. Possibility to set the time of the reminders by using the `:REMINDER_TIME:` property </br>
  *Example:*</br>
  Test.org file on the PC:
  ``` org
  * TODO Test entry
    :PROPERTIES:
    :REMINDER_TIME: 10
    :END:

    Some text here
  ```
Please notice that you can use values such as "5", "10", "15", "30" and so on. 
It looks like this is a limitation of the calendar app itself but I'm not sure (I need to investigate this).

Also a bit of code refactoring was performed. (I'm not a Java expert so it can result in some bad behavior :))
