package org.example;

import org.graalvm.nativeimage.ImageSingletons;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello App.main");
        System.out.println( "Touching Java8-incompatible class + " + ImageSingletons.class);
    }
}
