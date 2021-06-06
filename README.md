# foc
File or content - abstraction for file like objects


# Build
```bash
export ANDROID_SDK_ROOT=/home/user/Android/Sdk
./gradelw build
./gradlew publishToMavenLocal
```

# Integrate into project with [Gradle](https://gradle.org)
## Add jitpack.io repository
```gradle
repositories {
    // ...
    maven { url 'https://jitpack.io' }
}
```

## Add dependency
```gradle
dependencies {
    def focVersion = '1.1'

    // all modules
    implementation "com.github.bailuk:foc:$focVersion"

    // foc module
    // implementation "com.github.bailuk.foc:foc:$focVersion"

    // foc-android module
    // implementation "com.github.bailuk.foc:foc-android:$focVersion"
}
```

See [jitpack.io](http://jitpack.io) for more examples.