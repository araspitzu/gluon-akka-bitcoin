
gluon-mobile-akka
===========

A JavaFX Application that shows how to embed an akka in gluon-mobile.

You must use oracle JDK8 to run this application, if you're using openjdk you will need to specify the 
location of the javafx artifacts on the classpath.

Akka 2.5 is used and a few `PingerActor`s are created, this actor simply replies Ping/Pong to a random
pinger in the list (currently there are 3 pingers)

To start the ping/pong between the actors click the play button on the top left corner of the navbar.

Instructions
------------
To execute the sample, do as follows:

* Desktop
> Just run it from your IDE or from command line: `./gradlew run`
* Android
> Connect your Android device and run `./gradlew androidInstall`
* iOS
> Connect your iOS device and run `./gradlew launchIOSDevice`