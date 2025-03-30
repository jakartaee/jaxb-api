/*
 * Copyright (c) 2023, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ContextFinderTest {
    @Test
    public void testFind_String_String_ClassLoader_Map_AcceptsFactoryClassNameInProperties() throws JAXBException {
        Map<String, Object> properties = new HashMap<>();
        properties.put("jakarta.xml.bind.JAXBContextFactory", MyCustomFactory.class.getName());
        JAXBContext jaxbContext = ContextFinder.find(
                "jakarta.xml.bind.JAXBContextFactory", "contextPath", ContextFinder.class.getClassLoader(), properties);
        Assert.assertTrue(jaxbContext instanceof MyJaxbContext);
    }

    @Test
    public void testFind_String_String_ClassLoader_Map_AcceptsFactoryClassNameInSystemProperties() throws JAXBException {
        Map<String, Object> properties = new HashMap<>();
        var old = System.getProperty("jakarta.xml.bind.JAXBContextFactory");
        try {
            System.setProperty("jakarta.xml.bind.JAXBContextFactory", MyCustomFactory.class.getName());
            JAXBContext jaxbContext = ContextFinder.find(
                    "jakarta.xml.bind.JAXBContextFactory", "contextPath", ContextFinder.class.getClassLoader(), properties);
            Assert.assertTrue(jaxbContext instanceof MyJaxbContext);
        } finally {
            if (old != null) {
                System.setProperty("jakarta.xml.bind.JAXBContextFactory", old);
            } else {
                System.clearProperty("jakarta.xml.bind.JAXBContextFactory");
            }
        }
    }

    public static class MyCustomFactory implements JAXBContextFactory {
        @Override
        public JAXBContext createContext(Class<?>[] classesToBeBound, Map<String, ?> properties) throws JAXBException {
            if (properties.containsKey("jakarta.xml.bind.JAXBContextFactory")) {
                throw new JAXBException("property \"jakarta.xml.bind.JAXBContextFactory\" is not supported");
            }
            return new MyJaxbContext();
        }

        @Override
        public JAXBContext createContext(String contextPath, ClassLoader classLoader, Map<String, ?> properties) throws JAXBException {
            if (properties.containsKey("jakarta.xml.bind.JAXBContextFactory")) {
                throw new JAXBException("property \"jakarta.xml.bind.JAXBContextFactory\" is not supported");
            }
            return new MyJaxbContext();
        }
    }

    public static class MyJaxbContext extends JAXBContext {
        @Override
        public Marshaller createMarshaller() throws JAXBException {
            throw new UnsupportedOperationException("just for testing");
        }

        @Override
        public Unmarshaller createUnmarshaller() throws JAXBException {
            throw new UnsupportedOperationException("just for testing");
        }
    }
}