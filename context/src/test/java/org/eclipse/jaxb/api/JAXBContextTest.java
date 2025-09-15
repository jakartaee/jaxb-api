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

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.helpers.DefaultValidationEventHandler;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;

public class JAXBContextTest {

    public void testHexBinaryNull() throws Exception {
        JAXBContext context = JAXBContext.newInstance(Book.class);
        StringBuilder sb = new StringBuilder("");
        sb.append("<book><atr>");
        sb.append("\n\t");
        sb.append("6f1087c8105312e302e30820408a00005001588306312e3021000000a5049f1c611a4d02156501ff</atr></book>");
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setEventHandler(new DefaultValidationEventHandler());
        Book b = (Book) unmarshaller.unmarshal(new InputSource(new StringReader(sb.toString())));
        Assert.assertNull(b.atr);
    }

    @Test
    public void testHexBinaryNotNull() throws Exception {
        JAXBContext context = JAXBContext.newInstance(Book.class);
        StringBuilder sb = new StringBuilder("");
        sb.append("<book><atr>");
        sb.append("6f1087c8105312e302e30820408a00005001588306312e3021000000a5049f1c611a4d02156501ff</atr></book>");
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setEventHandler(new DefaultValidationEventHandler());
        Book b = (Book) unmarshaller.unmarshal(new InputSource(new StringReader(sb.toString())));
        Assert.assertNotNull(b.atr);
    }
}
