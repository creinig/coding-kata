/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package de.creinig.kata;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class LibraryTest {
    @Test
    public void someLibraryMethodReturnsTrue() {
        Library classUnderTest = new Library();
        assertTrue("someLibraryMethod should return 'true'", classUnderTest.someLibraryMethod());
    }
}
