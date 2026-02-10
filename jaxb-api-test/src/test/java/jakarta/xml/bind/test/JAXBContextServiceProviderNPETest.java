/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind.test;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBContextFactory;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


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
            Assertions.assertNotNull(ctx,"Expected non-null instance to be returned from the test Factory");
            Assertions.assertEquals(MyContext.class, ctx.getClass(), "Expected MyContext instance to be returned from the test Factory");
        } catch (Throwable t) {
            t.printStackTrace();
            Assertions.fail("Not expected to fail!");
        }
    }

    @Test
    public void testClasses() {
        try {
            JAXBContext ctx = JAXBContext.newInstance(new Class<?>[0]);
            Assertions.assertNotNull(ctx, "Expected non-null instance to be returned from the test Factory");
            Assertions.assertEquals(MyContext.class, ctx.getClass(), "Expected MyContext instance to be returned from the test Factory");
        } catch (Throwable t) {
            t.printStackTrace();
            Assertions.fail("Not expected to fail!");
        }
    }

    @BeforeEach
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

