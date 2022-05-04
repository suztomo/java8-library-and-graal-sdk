package org.example;

import org.graalvm.nativeimage.ImageSingletons;

public class FooFeature {
  public static void printMessage()
  {
    System.out.println( "Hello World! + " + ImageSingletons.class);
  }
}
