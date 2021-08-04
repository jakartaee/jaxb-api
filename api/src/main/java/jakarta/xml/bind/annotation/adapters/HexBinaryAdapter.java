/*
 * Copyright (c) 2004, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind.annotation.adapters;

import jakarta.xml.bind.DatatypeConverter;

/**
 * {@link XmlAdapter} for {@code xs:hexBinary}.
 *
 * <p>
 * This {@link XmlAdapter} binds {@code byte[]} to the hexBinary representation in XML.
 *
 * @author Kohsuke Kawaguchi
 * @since 1.6, JAXB 2.0
 */
public final class HexBinaryAdapter extends XmlAdapter<String,byte[]> {

    public HexBinaryAdapter() {}

    @Override
    public byte[] unmarshal(String s) {
        if(s==null)     return null;
        return DatatypeConverter.parseHexBinary(s);
    }

    @Override
    public String marshal(byte[] bytes) {
        if(bytes==null)     return null;
        return DatatypeConverter.printHexBinary(bytes);
    }
}
