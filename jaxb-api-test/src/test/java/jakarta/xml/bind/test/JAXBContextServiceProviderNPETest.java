/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.xml.bind.*;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.fail;

/**
 * regression test for
 * JDK-8145104: NPE is thrown when JAXBContextFactory implementation is specified in system property
 */
public class JAXBContextServiceProviderNPETest {

    public static class Factory implements JAXBContextFactory {

        @Override
        public JAXBContext createContext(Class<?>[] classesToBeBound, Map<String, ?> properties) throws JAXBException {
            return new MyContext();
        }

        @Override
        public JAXBContext createContext(String contextPath, ClassLoader classLoader, Map<String, ?> properties)
                throws JAXBException {
            return new MyContext();
        }
    }

    static class MyContext extends JAXBContext {
        @Override
        public Unmarshaller createUnmarshaller() throws JAXBException {
            return null;
        }

        @Override
        public Marshaller createMarshaller() throws JAXBException {
            return null;
        }

    }

    @Test
    public void testContextPath() {
        try {
            JAXBContext ctx = JAXBContext.newInstance("whatever", ClassLoader.getSystemClassLoader());
            assertNotNull("Expected non-null instance to be returned from the test Factory", ctx);
            assertEquals("Expected MyContext instance to be returned from the test Factory", MyContext.class, ctx.getClass());
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Not expected to fail!");
        }
    }

    @Test
    public void testClasses() {
        try {
            JAXBContext ctx = JAXBContext.newInstance(new Class[0]);
            assertNotNull("Expected non-null instance to be returned from the test Factory", ctx);
            assertEquals("Expected MyContext instance to be returned from the test Factory", MyContext.class, ctx.getClass());
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Not expected to fail!");
        }
    }

    @Before
    public void setup() {
        System.setProperty("jakarta.xml.bind.JAXBContextFactory", "jakarta.xml.bind.test.JAXBContextServiceProviderNPETest$Factory");
    }

    public static void main(String[] args) throws JAXBException {
        JAXBContextServiceProviderNPETest tst = new JAXBContextServiceProviderNPETest();
        tst.setup();
        tst.testContextPath();
        tst.testClasses();
    }

}

