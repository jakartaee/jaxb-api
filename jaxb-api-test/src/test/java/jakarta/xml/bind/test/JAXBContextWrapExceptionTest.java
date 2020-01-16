/*
 * Copyright (c) 2015, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind.test;

import org.junit.Before;
import org.junit.Test;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertNull;

/**
 * regression test for
 * JDK-8145112: newInstance(String, ClassLoader): java.lang.JAXBException should not be wrapped as expected
 * according to spec
 */
public class JAXBContextWrapExceptionTest {

    public static class Factory {

        public static JAXBContext createContext(Class[] classesToBeBound, Map<String, ?> properties) throws JAXBException {
            throw new JAXBException("test");
        }

        public static JAXBContext createContext(String contextPath, ClassLoader classLoader, Map<String, ?> properties)
                throws JAXBException {
            throw new JAXBException("test");
        }
    }

    @Test
    public void testContextPath() {
        try {
            JAXBContext.newInstance("whatever", ClassLoader.getSystemClassLoader());
        } catch (Throwable t) {
            assertEquals("test", t.getMessage());
            assertNull("Root cause must be null", t.getCause());
        }
    }

    @Test
    public void testClasses() {
        try {
            JAXBContext.newInstance(new Class[0]);
            assertTrue("This should fail", false);
        } catch (Throwable t) {
            assertEquals("test", t.getMessage());
            assertNull("Root cause must be null", t.getCause());
        }
    }

    @Before
    public void setup() {
        System.setProperty("jakarta.xml.bind.JAXBContextFactory", "jakarta.xml.bind.test.JAXBContextWrapExceptionTest$Factory");
    }

    public static void main(String[] args) throws JAXBException {
        new JAXBContextWrapExceptionTest().testContextPath();
        new JAXBContextWrapExceptionTest().testClasses();
    }

}

