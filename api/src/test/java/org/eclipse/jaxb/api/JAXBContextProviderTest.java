/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.eclipse.jaxb.api;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBContextFactory;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JAXBContextProviderTest {

    private static final String CONTEXT_PATH = "org.eclipse.jaxb.api";
    private static final String MISSING_FACTORY = "org.eclipse.jaxb.api.MissingContextFactory";
    private static final Map<String, ?> PROPERTIES = Map.of(
            JAXBContext.JAXB_CONTEXT_FACTORY, Factory.class.getName(), "test.property", "value");

    @Test
    public void testProviderForClasses() throws JAXBException {
        Class<?>[] classes = {String.class};
        TestContext context = Assertions.assertInstanceOf(TestContext.class,
                JAXBContext.newInstance(classes, PROPERTIES));

        Assertions.assertArrayEquals(classes, context.classes);
        Assertions.assertNull(context.contextPath);
        Assertions.assertEquals(Map.of("test.property", "value"), context.properties);
    }

    @Test
    public void testProviderForContextPath() throws JAXBException {
        ClassLoader loader = getClass().getClassLoader();
        TestContext context = Assertions.assertInstanceOf(TestContext.class,
                JAXBContext.newInstance(CONTEXT_PATH, loader, PROPERTIES));

        Assertions.assertEquals(CONTEXT_PATH, context.contextPath);
        Assertions.assertSame(loader, context.loader);
        Assertions.assertNull(context.classes);
        Assertions.assertEquals(Map.of("test.property", "value"), context.properties);
    }

    @Test
    public void testMissingProviderForClasses() {
        JAXBException exception = Assertions.assertThrows(JAXBException.class,
                () -> JAXBContext.newInstance(new Class<?>[]{String.class},
                        Map.of(JAXBContext.JAXB_CONTEXT_FACTORY, MISSING_FACTORY)));
        Assertions.assertInstanceOf(ClassNotFoundException.class, exception.getLinkedException());
    }

    @Test
    public void testMissingProviderForContextPath() {
        JAXBException exception = Assertions.assertThrows(JAXBException.class,
                () -> JAXBContext.newInstance(CONTEXT_PATH, getClass().getClassLoader(),
                        Map.of(JAXBContext.JAXB_CONTEXT_FACTORY, MISSING_FACTORY)));
        Assertions.assertInstanceOf(ClassNotFoundException.class, exception.getLinkedException());
    }

    public static class Factory implements JAXBContextFactory {

        @Override
        public JAXBContext createContext(Class<?>[] classesToBeBound, Map<String, ?> properties) {
            return new TestContext(classesToBeBound, null, null, properties);
        }

        @Override
        public JAXBContext createContext(String contextPath, ClassLoader classLoader, Map<String, ?> properties) {
            return new TestContext(null, contextPath, classLoader, properties);
        }
    }

    private static class TestContext extends JAXBContext {

        private final Class<?>[] classes;
        private final String contextPath;
        private final ClassLoader loader;
        private final Map<String, ?> properties;

        TestContext(Class<?>[] classes, String contextPath, ClassLoader loader, Map<String, ?> properties) {
            this.classes = classes;
            this.contextPath = contextPath;
            this.loader = loader;
            this.properties = properties;
        }

        @Override
        public Unmarshaller createUnmarshaller() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Marshaller createMarshaller() {
            throw new UnsupportedOperationException();
        }
    }
}
