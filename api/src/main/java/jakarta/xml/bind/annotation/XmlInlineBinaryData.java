/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 2005, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind.annotation;

import jakarta.activation.DataHandler;
import jakarta.xml.bind.attachment.AttachmentMarshaller;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.xml.transform.Source;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Disable consideration of XOP encoding for datatypes that are bound to base64-encoded binary data in XML.
 *
 * <p>
 * When XOP encoding is enabled as described in {@linkplain AttachmentMarshaller#isXOPPackage()}, this annotation
 * disables datatypes such as {@code java.awt.Image} or {@linkplain Source} or {@code byte[]} that are bound to
 * base64-encoded binary from being considered for XOP encoding. If a Jakarta XML Binding property is annotated with
 * this annotation or if the Jakarta XML Binding property's base type is annotated with this annotation, neither
 * {@linkplain AttachmentMarshaller#addMtomAttachment(DataHandler, String, String)} nor
 * {@linkplain AttachmentMarshaller#addMtomAttachment(byte[], int, int, String, String, String)} is ever called for the
 * property. The binary data will always be inlined.
 *
 * @author Joseph Fialli
 * @since 1.6, JAXB 2.0
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD, TYPE})
public @interface XmlInlineBinaryData {
}
