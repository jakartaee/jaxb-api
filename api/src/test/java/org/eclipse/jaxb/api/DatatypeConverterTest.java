/*
 * Copyright (c) 2025, 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 2023, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.eclipse.jaxb.api;

import jakarta.xml.bind.DatatypeConverter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DatatypeConverterTest {

    @Test
    public void testParseBoolean() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("11"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("1A"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("non"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("fals"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("falses"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("false s"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("falst"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("tru"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("trux"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("truu"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("truxx"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("truth"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("truelle"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("truec"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("true c"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("oui"));


        Assertions.assertFalse(DatatypeConverter.parseBoolean("0"));
        Assertions.assertFalse(DatatypeConverter.parseBoolean(" 0"));
        Assertions.assertFalse(DatatypeConverter.parseBoolean(" 0 "));
        Assertions.assertFalse(DatatypeConverter.parseBoolean("0 "));
        Assertions.assertTrue(DatatypeConverter.parseBoolean("1"));
        Assertions.assertTrue(DatatypeConverter.parseBoolean(" 1"));
        Assertions.assertTrue(DatatypeConverter.parseBoolean(" 1 "));
        Assertions.assertTrue(DatatypeConverter.parseBoolean("1 "));
        Assertions.assertFalse(DatatypeConverter.parseBoolean("false"));
        Assertions.assertFalse(DatatypeConverter.parseBoolean(" false"));
        Assertions.assertFalse(DatatypeConverter.parseBoolean("false "));
        Assertions.assertFalse(DatatypeConverter.parseBoolean(" false "));
        Assertions.assertTrue(DatatypeConverter.parseBoolean("true"));
        Assertions.assertTrue(DatatypeConverter.parseBoolean(" true"));
        Assertions.assertTrue(DatatypeConverter.parseBoolean("true "));
        Assertions.assertTrue(DatatypeConverter.parseBoolean(" true "));
    }

    @Test
    public void testPrint() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printInteger(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printDateTime(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printHexBinary(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printTime(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printDate(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printDecimal(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printBase64Binary(null));

        //Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printShort(null));
        //Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printFloat(null));
        //Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printBoolean(null));
        //Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printByte(null));
        //Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printUnsignedInt(null));
        //Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printString(null));
        //Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printInt(null));
        //Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printLong(null));
        //Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printDouble(null));
        //Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printQName(null));
        //Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printUnsignedShort(null));
        //Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printAnySimpleType(null));

    }

    @Test
    public void testBase64() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBase64Binary("Qxx=="));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBase64Binary("SGVsbG8sIJdvcmxkIQQxx=="));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBase64Binary("dGhpcyBpcyB\nhbiBleGFtcGxl=="));

        Assertions.assertEquals("", new String(DatatypeConverter.parseBase64Binary("")));
        Assertions.assertEquals("f", new String(DatatypeConverter.parseBase64Binary("Zg==")));
        Assertions.assertEquals("fo", new String(DatatypeConverter.parseBase64Binary("Zm8=")));
        Assertions.assertEquals("foo", new String(DatatypeConverter.parseBase64Binary("Zm9v")));
        Assertions.assertEquals("foob", new String(DatatypeConverter.parseBase64Binary("Zm9vYg==")));
        Assertions.assertEquals("fooba", new String(DatatypeConverter.parseBase64Binary("Zm9vYmE=")));
        Assertions.assertEquals("foobar", new String(DatatypeConverter.parseBase64Binary("Zm9vYmFy")));
        Assertions.assertEquals("this is an example", new String(DatatypeConverter.parseBase64Binary("dGhpcyBpcyB hbiBleGFtcGxl")));
        Assertions.assertEquals("this is an example", new String(DatatypeConverter.parseBase64Binary("dGhpcyBpcyB\nhbiBleGFtcGxl")));
        Assertions.assertEquals("this is an example", new String(DatatypeConverter.parseBase64Binary("dGhpcyBpcyB\thbiBleGFtcGxl")));

        Assertions.assertNotEquals("Hello, world!", new String(DatatypeConverter.parseBase64Binary("SGVsbG8sIJdvcmxkIQ==")));

        Assertions.assertEquals("Hello, world!", new String(DatatypeConverter.parseBase64Binary("SGVsbG8sIHdvcmxkIQ==")));
    }
}
