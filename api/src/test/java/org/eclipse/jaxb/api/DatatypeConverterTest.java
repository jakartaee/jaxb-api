/*
 * Copyright (c) 2023, 2023 Oracle and/or its affiliates. All rights reserved.
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

public class DatatypeConverterTest {

    @Test
    public void testParseBoolean() {
        Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean(null));
        Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean(""));
        Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("11"));
        Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("1A"));
        Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("non"));
        Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("fals"));
        Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("falses"));
        Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("false s"));
        Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("falst"));
        Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("tru"));
        Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("trux"));
        Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("truu"));
        Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("truxx"));
        Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("truth"));
        Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("truelle"));
        Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("truec"));
        Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("true c"));
        Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.parseBoolean("oui"));


        Assert.assertEquals(false, DatatypeConverter.parseBoolean("0"));
        Assert.assertEquals(false, DatatypeConverter.parseBoolean(" 0"));
        Assert.assertEquals(false, DatatypeConverter.parseBoolean(" 0 "));
        Assert.assertEquals(false, DatatypeConverter.parseBoolean("0 "));
        Assert.assertEquals(true, DatatypeConverter.parseBoolean("1"));
        Assert.assertEquals(true, DatatypeConverter.parseBoolean(" 1"));
        Assert.assertEquals(true, DatatypeConverter.parseBoolean(" 1 "));
        Assert.assertEquals(true, DatatypeConverter.parseBoolean("1 "));
        Assert.assertEquals(false, DatatypeConverter.parseBoolean("false"));
        Assert.assertEquals(false, DatatypeConverter.parseBoolean(" false"));
        Assert.assertEquals(false, DatatypeConverter.parseBoolean("false "));
        Assert.assertEquals(false, DatatypeConverter.parseBoolean(" false "));
        Assert.assertEquals(true, DatatypeConverter.parseBoolean("true"));
        Assert.assertEquals(true, DatatypeConverter.parseBoolean(" true"));
        Assert.assertEquals(true, DatatypeConverter.parseBoolean("true "));
        Assert.assertEquals(true, DatatypeConverter.parseBoolean(" true "));
    }

    @Test 
    public void testJDate() { 
        Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printInteger(null));
        Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printDateTime(null));
        Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printHexBinary(null));
        Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printTime(null));
        Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printDate(null));
        Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printDecimal(null));
        Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printBase64Binary(null));
        
        //Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printShort(null));
        //Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printFloat(null));
        //Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printBoolean(null));
        //Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printByte(null));
        //Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printUnsignedInt(null));
        //Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printString(null));
        //Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printInt(null));
        //Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printLong(null));
        //Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printDouble(null));
        //Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printQName(null));
        //Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printUnsignedShort(null));
        //Assert.assertThrows(IllegalArgumentException.class, () -> DatatypeConverter.printAnySimpleType(null));
        
    } 
}

