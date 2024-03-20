/*
 * Copyright (c) 2015, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind.test;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * regression test for
 * JDK-8145112: newInstance(String, ClassLoader): java.lang.JAXBException should not be wrapped as expected
 * according to spec
 */
public class JAXBContextWrapExceptionTest {

    public static class Factory {

        public static JAXBContext createContext(Class<?>[] classesToBeBound, Map<String, ?> properties) throws JAXBException {
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
            Assertions.assertEquals(t.getMessage(), "test");
            Assertions.assertNull(t.getCause(), "Root cause must be null");
        }
    }

    @Test
    public void testClasses() {
        try {
            JAXBContext.newInstance(new Class<?>[0]);
            Assertions.assertTrue(false, "This should fail");
        } catch (Throwable t) {
            Assertions.assertEquals(t.getMessage(), "test");
            Assertions.assertNull(t.getCause(), "Root cause must be null");
        }
    }

    @BeforeEach
    public void setup() {
        System.setProperty("jakarta.xml.bind.JAXBContextFactory", "jakarta.xml.bind.test.JAXBContextWrapExceptionTest$Factory");
    }

    public static void main(String[] args) throws JAXBException {
        new JAXBContextWrapExceptionTest().testContextPath();
        new JAXBContextWrapExceptionTest().testClasses();
    }

}

