[![](https://jitpack.io/v/LBNL-UCB-STI/or-tools-wrapper.svg)](https://jitpack.io/#LBNL-UCB-STI/or-tools-wrapper)

# or-tools-wrapper

### Build

```
./gradlew build
```

### Update library
1. Download binary distributions from official page: https://developers.google.com/optimization/install/java
2. Get the copy of the new version of `ortools-java-XXXX.jar` to the `libs` folder of the project
3. Extract native libraries for every platform from JAR file per platfrom. For example, for Linux the JAR file is `ortools-linux-x86-64-8.2.8710.jar`. When you unzip it you see a folder `linux-x86-64` which contains `libjniortools.so` and `libortools.so`. Create a zip file with those files and copy it to `resources`