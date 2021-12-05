[![Android CI](https://github.com/bailuk/foc/actions/workflows/android.yml/badge.svg)](https://github.com/bailuk/foc/actions/workflows/android.yml) [![Release](https://jitpack.io/v/com.github.bailuk/foc.svg)](https://jitpack.io/#com.github.bailuk/foc)

# foc - file or content
A java library providing a platform independent abstraction for file like objects. 
It supports the following types: 
- Unix and Windows filesystems trough [Java File](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html) 
- Android Storage Access Framework - [SAF](https://developer.android.com/guide/topics/providers/document-provider)
- Android [Assets](https://developer.android.com/reference/android/content/res/AssetManager)


## Build
```bash
export ANDROID_SDK_ROOT=/home/user/Android/Sdk
./gradelw build
./gradlew publishToMavenLocal
```

## Integrate into project with [Gradle](https://gradle.org)
### Add jitpack.io repository
```gradle
repositories {
    // ...
    maven { url 'https://jitpack.io' }
}
```

### Add dependency
```gradle
dependencies {
    def focVersion = '1.2'

    // all modules
    implementation "com.github.bailuk:foc:$focVersion"

    // foc module
    // implementation "com.github.bailuk.foc:foc:$focVersion"

    // foc-android module
    // implementation "com.github.bailuk.foc:foc-android:$focVersion"
}
```

See [jitpack.io](http://jitpack.io) for more.


## Example
```java
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;

import ch.bailu.foc.Foc;
import ch.bailu.foc_android.FocAssetFactory;
import ch.bailu.foc_android.FocAndroidFactory;

public class ExampleActivity extends Activity {
    private int myRequestCode = 42;

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        if (requestCode == myRequestCode && resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                Foc saf = new FocAndroidFactory(this).toFoc(uri.toString());
                printFiles(saf);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Foc asset = new FocAssetFactory(this).toFoc("some/path/to/asset/file_or_directory");
        printFiles(asset);

        Foc file = new FocAndroidFactory(this).toFoc("/unix/path/to/file_or_directory");
        printFiles(file);
    }

    private void printFiles(Foc foc) {
        System.out.println(foc.getName());

        if (foc.isFile() && foc.canRead()) {
            InputStream reader = null;
            try {
                reader = foc.openR();
                printToConsole(reader);
            } catch (IOException e) {
            } finally {
                Foc.close(reader);
            }
        } else if (foc.isDir()) {
            foc.foreach(child -> printFiles(child));
        }
    }

    private void printToConsole(InputStream reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        int c;
        while ((c = reader.read()) != -1) {
            builder.append((char) c);
        }
        System.out.println(builder.toString());
    }
}
```