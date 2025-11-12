/*
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
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class DatatypeConverterTest {

    @Test
    public void testParseBoolean() {
        assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean(null));
        assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean(""));
        assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("11"));
        assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("1A"));
        assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("non"));
        assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("fals"));
        assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("falses"));
        assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("false s"));
        assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("falst"));
        assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("tru"));
        assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("trux"));
        assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("truu"));
        assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("truxx"));
        assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("truth"));
        assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("truelle"));
        assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("truec"));
        assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("true c"));
        assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("oui"));

        assertFalse(DatatypeConverter.parseBoolean("0"));
        assertFalse(DatatypeConverter.parseBoolean(" 0"));
        assertFalse(DatatypeConverter.parseBoolean(" 0 "));
        assertFalse(DatatypeConverter.parseBoolean("0 "));
        assertTrue(DatatypeConverter.parseBoolean("1"));
        assertTrue(DatatypeConverter.parseBoolean(" 1"));
        assertTrue(DatatypeConverter.parseBoolean(" 1 "));
        assertTrue(DatatypeConverter.parseBoolean("1 "));
        assertFalse(DatatypeConverter.parseBoolean("false"));
        assertFalse(DatatypeConverter.parseBoolean(" false"));
        assertFalse(DatatypeConverter.parseBoolean("false "));
        assertFalse(DatatypeConverter.parseBoolean(" false "));
        assertTrue(DatatypeConverter.parseBoolean("true"));
        assertTrue(DatatypeConverter.parseBoolean(" true"));
        assertTrue(DatatypeConverter.parseBoolean("true "));
        assertTrue(DatatypeConverter.parseBoolean(" true "));
    }

    @Test
    public void testParseInteger() {
        // happily parses with and without leading sign
        assertEquals(BigInteger.valueOf(12345), DatatypeConverter.parseInteger("+12345"));
        assertEquals(BigInteger.valueOf(12345), DatatypeConverter.parseInteger("12345"));
        assertEquals(BigInteger.valueOf(-12345), DatatypeConverter.parseInteger("-12345"));

        // rejects decimal points
        assertThrows(NumberFormatException.class, () -> DatatypeConverter.parseInteger(".12345"));
    }

    @Test
    public void testParseLong() {
        // happily parses with and without leading sign
        assertEquals(12345, DatatypeConverter.parseLong("+12345"));
        assertEquals(12345, DatatypeConverter.parseLong("12345"));
        assertEquals(-12345, DatatypeConverter.parseLong("-12345"));

        // rejects decimal points
        assertThrows(NumberFormatException.class, () -> DatatypeConverter.parseLong(".12345"));
    }

    @Test 
    public void testPrint() { 
        assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printInteger(null));
        assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printDateTime(null));
        assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printHexBinary(null));
        assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printTime(null));
        assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printDate(null));
        assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printDecimal(null));
        assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printBase64Binary(null));
        
        //assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printShort(null));
        //assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printFloat(null));
        //assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printBoolean(null));
        //assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printByte(null));
        //assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printUnsignedInt(null));
        //assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printString(null));
        //assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printInt(null));
        //assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printLong(null));
        //assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printDouble(null));
        //assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printQName(null));
        //assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printUnsignedShort(null));
        //assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printAnySimpleType(null));
        
    } 

    @Test
    public void testBase64() {
        assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBase64Binary("Qxx=="));

        assertEquals("", new String(DatatypeConverter.parseBase64Binary("")));
        assertEquals("f", new String(DatatypeConverter.parseBase64Binary("Zg==")));
        assertEquals("fo", new String(DatatypeConverter.parseBase64Binary("Zm8=")));
        assertEquals("foo", new String(DatatypeConverter.parseBase64Binary("Zm9v")));
        assertEquals("foob", new String(DatatypeConverter.parseBase64Binary("Zm9vYg==")));
        assertEquals("fooba", new String(DatatypeConverter.parseBase64Binary("Zm9vYmE=")));
        assertEquals("foobar", new String(DatatypeConverter.parseBase64Binary("Zm9vYmFy")));

        assertNotEquals("Hello, world!", new String(DatatypeConverter.parseBase64Binary("SGVsbG8sIJdvcmxkIQ==")));

        assertEquals("Hello, world!", new String(DatatypeConverter.parseBase64Binary("SGVsbG8sIHdvcmxkIQ==")));
    }
}
