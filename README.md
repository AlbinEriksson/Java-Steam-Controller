# Java Steam Controller [![](https://jitpack.io/v/AlbinEriksson/Java-Steam-Controller.svg)](https://jitpack.io/#AlbinEriksson/Java-Steam-Controller)
A Java 1.8 library which can interpret inputs from a Steam Controller.
## Only the Steam Controller?
I know, most controllers rely on XInput or DInput. And I know, there are libraries for this.  
However, the Steam Controller is different. The only way to receive inputs is to read raw USB data. As far as I know, common input libraries do not support this. There's no easy way to determine where each button and analog control is.  
Then there's also weird behavior with the analog stick and left trackpad used together.
## How to set up
You have to first setup either Maven or Gradle for your project.
### For Maven:
Add the jitpack.io repository to your pom.xml file:
```xml
<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>
```
Then, add the Java-Steam-Controller dependency:
```xml
<dependencies>
	<dependency>
		<groupId>com.github.AlbinEriksson</groupId>
		<artifactId>Java-Steam-Controller</artifactId>
		<version>1.1.0</version>
	</dependency>
</dependencies>
```
### For Gradle:
Add the jitpack.io repository to your root build.gradle file:
```gradle
allprojects {
	repositories {
		maven { url "https://jitpack.io" }
	}
}
```
Then, add the Java-Steam-Controller dependency:
```gradle
dependencies {
	compile 'com.github.AlbinEriksson:Java-Steam-Controller:1.1.0'
}
```
### Development versions
The version number you see above is the latest release version, but you can also replace it with the short commit hash, if you want development versions.
## Contributing
Well, I have one controller and one computer running on one operating system (Windows 10 x64). If any errors occur (which they probably will), hit up an issue or pull request so we can prevent it from happening.
## How to use
First, you can find a Steam Controller:
```java
List<SteamController> controllers = SteamController.getConnectedControllers();
```
Then, you must implement a subscriber to process inputs from the controller.
```java
public class TestSteamControllerSubscriber implements SteamControllerSubscriber
{
    @Override
    public void update(SteamController state, SteamController last)
    {
        if(state.isAHeld() && !last.isAHeld())
            System.out.println("A button pressed!");
    }
}
```
And finally, to listen for inputs:
```java
SteamControllerListener listener = new SteamControllerListener();
listener.open();
TestSteamControllerSubscriber subscriber = new TestSteamControllerSubscriber();
listener.addSubscriber(subscriber);
```
