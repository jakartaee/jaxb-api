/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/**
 * Jakarta XML Binding API.
 *
 * <p>
 * References in this document to JAXB refer to the Jakarta XML Binding unless otherwise noted.
 */
module jakarta.xml.bind {
    requires transitive jakarta.activation;
    requires transitive java.xml;
    requires java.logging;

    exports jakarta.xml.bind;
    exports jakarta.xml.bind.annotation;
    exports jakarta.xml.bind.annotation.adapters;
    exports jakarta.xml.bind.attachment;
    exports jakarta.xml.bind.helpers;
    exports jakarta.xml.bind.util;

    uses jakarta.xml.bind.JAXBContextFactory;
}
