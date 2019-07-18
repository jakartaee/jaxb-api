/*
 * Copyright (c) 2005, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
/**
 * References in this document to JAXB refer to the Jakarta XML Binding unless otherwise noted.
 */
module java.xml.bind {
    requires transitive jakarta.activation;
    requires transitive java.xml;
    requires java.logging;
    requires java.desktop;

    exports javax.xml.bind;
    exports javax.xml.bind.annotation;
    exports javax.xml.bind.annotation.adapters;
    exports javax.xml.bind.attachment;
    exports javax.xml.bind.helpers;
    exports javax.xml.bind.util;

    uses javax.xml.bind.JAXBContextFactory;
}
