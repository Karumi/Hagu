<p align="center"><img src ="./pictures/hagu.png" /></p>

[![Build Status](https://travis-ci.com/Karumi/Hagu.svg?branch=master)](https://travis-ci.com/Karumi/Hagu)

Gradle plugin to enable build configuration secrets for Kotlin, Kotlin-Native / Multiplatform.

### Adding to your project

Add our Gradle Plugin to your build.gradle file:

```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    
    dependencies {       
        classpath 'com.karumi.hagu:hagu:0.1.2'
    }
}

apply plugin: 'com.karumi.hagu'

kotlin.sourceSets["main"].kotlin.srcDirs("$buildDir/generated/kotlin/config")
``` 

*If you want to use it in Kotlin Multiplatform you must change `main` for `commonMain` or the source directory name you are using it.*  

### Add configuration file

Hagu by default uses `gradle.properties` file to add the key-value configuration you want like this:

```groovy
api_key = "some_api_key"
number_key = 11
```

### Use your configuration file

First you must build your project, `./gradlew build`, it generates the HaguConfig kotlin object with the property constants you added in `gradle.properties`.

```kotlin
import com.karumi.hagu.generated.HaguConfig

println(HaguConfig.API_KEY)  //output: some_api_key
```

### Profiles 

You can build your project with different build configuration profiles having different property files, for example:

```
debug.properties
release.properties
ci.properties
```

To select one of those profiles, you need to add a `profile` parameter with the name of the profile file, for example, `debug`.

```
./gradlew build -Pprofile=debug
```

#### Default profile

If you want to select a default profile instead of `gradle.properties` you can add the following Hagu configuration:

```
hagu {
    defaultProfile = "debug"
}
```

This configuration will use `debug.properties` by default, you can always override this behavior if you add the `profile` parameter. 

License
-------

    Copyright 2019 Karumi

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
