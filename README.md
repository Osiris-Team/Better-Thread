# Better-Thread [![](https://jitpack.io/v/Osiris-Team/Better-Thread.svg)](https://jitpack.io/#Osiris-Team/Better-Thread)
Provides colored, highly customizable, thread visualisation 
for any terminal, via [jansi](http://fusesource.github.io/jansi/)
and [jline](https://github.com/jline/jline3).
Add it to your project with [Maven/Gradle/Sbt/Leinigen](https://jitpack.io/#Osiris-Team/Better-Thread/).

```java
BThreadManager manager = new BThreadManager();
manager.startPrinter();
manager.start(thread -> {
    for (int i = 1; i <= 100; i++) {
        thread.setStatus("Climbing stairs... Step: "+ i);
        thread.step();
    }
}, new BuilderBThreadModules().date().spinner().status().build());
```

<div><img src="https://cdn.discordapp.com/attachments/709182873348997172/811714169614106634/tasks.gif" /></div>


### Features
 - Works on multiple platforms (Windows, Unix, MacOs etc...)
 - Customizable messages via modules.
 - Easy to use and fast to write.

### Usage
- [Example how to create a Task, with parent class.](https://github.com/Osiris-Team/Better-Thread/blob/main/src/test/java/UsageExample.java)
- [Example how to create a Task, without parent class.](https://github.com/Osiris-Team/Better-Thread/blob/main/src/test/java/UsageExample.java)

### Links
- Support and chat over at [Discord](https://discord.com/invite/GGNmtCC)
- Support the development by [donating](https://www.paypal.com/donate?hosted_button_id=JNXQCWF2TF9W4)
- Thanks a lot to [@gnodet](https://github.com/gnodet) for his help with jansi and jline
 
