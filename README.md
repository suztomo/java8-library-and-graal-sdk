# Setup

Install JDK 8 and JDK 11. I used Open JDK 11 and zulu JDK 8.

```
~/java8-library-and-graal-sdk $ sdk list java |grep installed
...
               |     | 11.0.2       | open    | installed  | 11.0.2-open         
               | >>> | 8.0.332      | zulu    | installed  | 8.0.332-zulu   
```


# Steps

## 1. Compile the project using JDK 11

```
$ sdk use java 11.0.2-open     

Using java version 11.0.2-open in this shell.
$ java -version
openjdk version "11.0.2" 2019-01-15
OpenJDK Runtime Environment 18.9 (build 11.0.2+9)
OpenJDK 64-Bit Server VM 18.9 (build 11.0.2+9, mixed mode)
```

```
$ mvn clean install -DskipTests
[INFO] Scanning for projects...
[INFO] 
...
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.754 s
[INFO] Finished at: 2022-05-04T10:14:53-04:00
[INFO] ------------------------------------------------------------------------
```

## 1. Switch to JDK 8 and run tests



```
~/java8-library-and-graal-sdk $ mvn test
...
[INFO] --- maven-compiler-plugin:3.1:compile (default-compile) @ java8-library-and-graal-sdk ---
[INFO] Nothing to compile - all classes are up to date
...
[INFO] --- maven-compiler-plugin:3.1:testCompile (default-testCompile) @ java8-library-and-graal-sdk ---
[INFO] Nothing to compile - all classes are up to date
[INFO] 
[INFO] --- maven-surefire-plugin:2.12.4:test (default-test) @ java8-library-and-graal-sdk ---
[INFO] Surefire report directory: /Users/suztomo/java8-library-and-graal-sdk/target/surefire-reports

-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running org.example.AppTest
Hello App.main
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.033 sec

Results :

Tests run: 1, Failures: 0, Errors: 0, Skipped: 0

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.235 s
[INFO] Finished at: 2022-05-04T10:16:11-04:00
[INFO] ------------------------------------------------------------------------
```

Notice that `FooFeature.java` touches `ImageSingletons`.
This class from `org.graalvm.sdk:graal-sdk:22.1.0` is known to be incompatible with
Java 8 runtime. Because the tests do not touch `FooFeature`, it does not
fail the build.

# What if some classes used by tests are not compatible with Java 8

In `App.java`, uncomment the reference to `ImageSingletons`. Do the steps
above and it fails at the test phase:

```
$ sdk use java 8.0.332-zulu 

Using java version 8.0.332-zulu in this shell.
$ mvn test
...
[INFO] --- maven-compiler-plugin:3.1:compile (default-compile) @ java8-library-and-graal-sdk ---
[INFO] Nothing to compile - all classes are up to date
...
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running org.example.AppTest
Hello App.main
Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 0.047 sec <<< FAILURE!
shouldAnswerWithTrue(org.example.AppTest)  Time elapsed: 0.01 sec  <<< ERROR!
java.lang.UnsupportedClassVersionError: org/graalvm/nativeimage/ImageSingletons has
been compiled by a more recent version of the Java Runtime (class file version 55.0),
this version of the Java Runtime only recognizes class file versions up to 52.0
	at java.lang.ClassLoader.defineClass1(Native Method)
	at java.lang.ClassLoader.defineClass(ClassLoader.java:757)
	at java.security.SecureClassLoader.defineClass(SecureClassLoader.java:142)
	at java.net.URLClassLoader.defineClass(URLClassLoader.java:473)
	at java.net.URLClassLoader.access$100(URLClassLoader.java:74)
	at java.net.URLClassLoader$1.run(URLClassLoader.java:369)
	at java.net.URLClassLoader$1.run(URLClassLoader.java:363)
	at java.security.AccessController.doPrivileged(Native Method)
	at java.net.URLClassLoader.findClass(URLClassLoader.java:362)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:419)
	at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:352)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:352)
	at org.example.App.main(App.java:14)
	at org.example.AppTest.shouldAnswerWithTrue(AppTest.java:18)
...

Results :

Tests in error: 
  shouldAnswerWithTrue(org.example.AppTest): org/graalvm/nativeimage/ImageSingletons has been compiled by a more recent version of the Java Runtime (class file version 55.0), this version of the Java Runtime only recognizes class file versions up to 52.0

Tests run: 1, Failures: 0, Errors: 1, Skipped: 0

[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
...
```

This check (running tests on JDK 8) ensures that the library users continue to
use Java 8.
