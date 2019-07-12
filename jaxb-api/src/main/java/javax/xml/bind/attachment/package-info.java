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
 * This package is implemented by a MIME-based package processor that
 * enables the interpretation and creation of optimized binary data
 * within an MIME-based package format.
 * <p>
 * <p>
 * Soap MTOM[1], XOP([2][3]) and WS-I AP[4] standardize approaches to
 * optimized transmission of binary datatypes as an attachment.
 * To optimally support these standards within a message passing
 * environment, this package enables an integrated solution between
 * a MIME-based package processor and Jakarta XML Binding unmarshall/marshal processes.
 * <p>
 * References in this document to JAXB refer to the Jakarta XML Binding unless otherwise noted.
 *
 * <h2>Package Specification</h2>
 * <p>
 * <ul>
 * <li><a href="https://projects.eclipse.org/projects/ee4j.jaxb">Jakarta XML Binding Specification project</a>
 * </ul>
 * <p>
 * <h2>Related Standards</h2>
 * <p>
 * <ul>
 * <li><a href="http://www.w3.org/TR/2004/WD-soap12-mtom-20040608/">[1]SOAP Message Transmission Optimization Mechanism</a> </li>
 * <li><a href="http://www.w3.org/TR/2005/REC-xop10-20050125/">[2]XML-binary Optimized Packaging</a></li>
 * <li><a href="http://www.ws-i.org/Profiles/AttachmentsProfile-1.0-2004-08-24.html">[3]WS-I Attachments Profile Version 1.0.</a></li>
 * <li><a href="http://www.w3.org/TR/xml-media-types/">[4]Describing Media Content of Binary Data in XML</a></li>
 * </ul>
 *
 * @see <a href="http://www.w3.org/TR/2004/WD-soap12-mtom-20040608/">[1]SOAP Message Transmission Optimization Mechanism</a>
 * @see <a href="http://www.w3.org/TR/2005/REC-xop10-20050125/">[2]XML-binary Optimized Packaging</a>
 * @see <a href="http://www.ws-i.org/Profiles/AttachmentsProfile-1.0-2004-08-24.html">[3]WS-I Attachments Profile Version 1.0.</a>
 * @see <a href="http://www.w3.org/TR/xml-media-types/">[4]Describing Media Content of Binary Data in XML</a>
 * @since JAXB 2.0
 */
package javax.xml.bind.attachment;
