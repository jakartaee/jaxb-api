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

import jakarta.xml.bind.DatatypeConverter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DatatypeConverterRegistrationTest {

    @Test
    public void testRegisterConverter() throws Exception {
        // Isolate the static converter from tests that initialize its default value.
        URL api = DatatypeConverter.class.getProtectionDomain().getCodeSource().getLocation();
        try (URLClassLoader loader = new URLClassLoader(new URL[]{api}, null)) {
            Class<?> converterClass = loader.loadClass("jakarta.xml.bind.DatatypeConverter");
            Assertions.assertSame(loader, converterClass.getClassLoader());
            Class<?> converterInterface = loader.loadClass("jakarta.xml.bind.DatatypeConverterInterface");
            Method setter = converterClass.getMethod("setDatatypeConverter", converterInterface);
            Method parseInt = converterClass.getMethod("parseInt", String.class);

            InvocationTargetException nullArgument = Assertions.assertThrows(InvocationTargetException.class,
                    () -> setter.invoke(null, new Object[]{null}));
            Assertions.assertInstanceOf(IllegalArgumentException.class, nullArgument.getCause());

            Object first = Proxy.newProxyInstance(loader, new Class<?>[]{converterInterface}, (proxy, method, args) -> {
                Assertions.assertEquals("parseInt", method.getName());
                Assertions.assertEquals("41", args[0]);
                return 123;
            });
            setter.invoke(null, first);
            Assertions.assertEquals(123, parseInt.invoke(null, "41"));

            Object second = Proxy.newProxyInstance(loader, new Class<?>[]{converterInterface}, (proxy, method, args) -> {
                throw new AssertionError("The second converter must not replace the first");
            });
            setter.invoke(null, second);
            Assertions.assertEquals(123, parseInt.invoke(null, "41"));

            InvocationTargetException nullAfterRegistration = Assertions.assertThrows(InvocationTargetException.class,
                    () -> setter.invoke(null, new Object[]{null}));
            Assertions.assertInstanceOf(IllegalArgumentException.class, nullAfterRegistration.getCause());
        }
    }
}
