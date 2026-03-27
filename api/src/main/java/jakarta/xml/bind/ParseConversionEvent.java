/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 2004, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

// Contributor(s):
//     Ryan Shoemaker
//     Kohsuke Kawaguchi
//     Joe Fialli

package jakarta.xml.bind;

/**
 * This event indicates that a problem was encountered while converting a string from the XML data into a value of the
 * target Java data type.
 *
 * @see ValidationEvent
 * @see ValidationEventHandler
 * @see Unmarshaller
 */
public interface ParseConversionEvent extends ValidationEvent {

}
