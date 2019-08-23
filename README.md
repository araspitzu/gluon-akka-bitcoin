
gluon-akka-bitcoin
===========

A JavaFX Application that shows how to embed an SQLite database on your project with akka.

You must use oracle JDK8 to run this application, if you're using openjdk you will need to specify the 
location of the javafx artifacts on the classpath.

It uses the proper SQLite dependency and driver for each platform, and uses Gluon Charm Down to 
access the private storage location on them

Akka 2.4 is used and a `InfoActor` is created, this actor replies with a serialized (printed) version
of its configuration through which is possible to read various system properties and versioning.

Bitcoin-lib 0.13 is used but currently fails to load the libsecp256k1 native library.

The app provides a few operations available from the navbar buttons, starting from right:
- `minus sign button`: cleans the output
- `clipper button`: print info about the SQLITE DB creation and population
- `plus-folder button`: insert a few persons in the db and populates view
- `strange-movie button`: ask the `InfoActor` about the actor system config
- `lock button`: asks the `InfoActor` to generate a new public key (not working)

Instructions
------------
To execute the sample, do as follows:

* Desktop
> Just run it from your IDE or from command line: `./gradlew run`
* Android
> Connect your Android device and run `./gradlew androidInstall`
* iOS
> Connect your iOS device and run `./gradlew launchIOSDevice`

### Gotchas
If the build fails with `GC overhead limit exceeded` during task `dex` you can run:
`./gradlew clean && ./gradlew androidInstall` 