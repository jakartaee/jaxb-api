/*
 * Copyright (c) 2003, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind;

/**
 * Encapsulate the location of a ValidationEvent.
 *
 * <p>
 * The {@code ValidationEventLocator} indicates where the {@code ValidationEvent}
 * occurred.  Different fields will be set depending on the type of
 * validation that was being performed when the error or warning was detected.
 * For example, on-demand validation would produce locators that contained 
 * references to objects in the Java content tree while unmarshal-time 
 * validation would produce locators containing information appropriate to the 
 * source of the XML data (file, url, Node, etc.).
 *
 * @author <ul><li>Ryan Shoemaker, Sun Microsystems, Inc.</li>
 *             <li>Kohsuke Kawaguchi, Sun Microsystems, Inc.</li>
 *             <li>Joe Fialli, Sun Microsystems, Inc.</li></ul>
 * @see ValidationEvent
 * @since 1.6, JAXB 1.0
 */
public interface ValidationEventLocator {

    /**
     * Return the name of the XML source as a URL if available
     *
     * @return the name of the XML source as a URL or null if unavailable
     */
    java.net.URL getURL();
    
    /**
     * Return the byte offset if available
     *
     * @return the byte offset into the input source or -1 if unavailable
     */
    int getOffset();
    
    /**
     * Return the line number if available
     *
     * @return the line number or -1 if unavailable 
     */
    int getLineNumber();
    
    /**
     * Return the column number if available
     *
     * @return the column number or -1 if unavailable
     */
    int getColumnNumber();
    
    /**
     * Return a reference to the object in the Java content tree if available
     *
     * @return a reference to the object in the Java content tree or null if
     *         unavailable
     */
    java.lang.Object getObject();
    
    /**
     * Return a reference to the DOM Node if available
     *
     * @return a reference to the DOM Node or null if unavailable 
     */
    org.w3c.dom.Node getNode();
    
}
