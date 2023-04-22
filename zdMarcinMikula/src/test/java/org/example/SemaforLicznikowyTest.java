package org.example;

import junit.framework.TestCase;

public class SemaforLicznikowyTest extends TestCase {

    public void testSemaforLicznikowyStart() {
        SemaforLicznikowy semaforLicznikowy = new SemaforLicznikowy(0);
        assertEquals(0, semaforLicznikowy.semaforLicznikowyStart());
    }
}