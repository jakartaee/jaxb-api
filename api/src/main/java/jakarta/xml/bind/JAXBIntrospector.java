/*
 * Copyright (c) 2004, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind;

import  javax.xml.namespace.QName;

/**
 * Provide access to Jakarta XML Binding xml binding data for a Jakarta XML Binding object.
 *
 * <p>
 * Initially, the intent of this class is to just conceptualize how
 * a Jakarta XML Binding application developer can access xml binding information,
 * independent if binding model is java to schema or schema to java.
 * Since accessing the XML element name related to a Jakarta XML Binding element is
 * a highly requested feature, demonstrate access to this
 * binding information.
 *
 * The factory method to get a <code>JAXBIntrospector</code> instance is
 * {@link JAXBContext#createJAXBIntrospector()}.
 *
 * @see JAXBContext#createJAXBIntrospector()
 * @since 1.6, JAXB 2.0
 */
public abstract class JAXBIntrospector {

    /**
     * Do-nothing constructor for the derived classes.
     */
    protected JAXBIntrospector() {}

    /**
     * <p>Return true if <code>object</code> represents a Jakarta XML Binding element.</p>
     * <p>Parameter <code>object</code> is a Jakarta XML Binding element for following cases:
     * <ol>
     *   <li>It is an instance of <code>jakarta.xml.bind.JAXBElement</code>.</li>
     *   <li>The class of <code>object</code> is annotated with
     *       <code>&#64;XmlRootElement</code>.
     *   </li>
     * </ol>
     *
     * @see #getElementName(Object)
     */
    public abstract boolean isElement(Object object);

    /**
     * <p>Get xml element qname for <code>jaxbElement</code>.</p>
     *
     * @param jaxbElement is an object that {@link #isElement(Object)} returned true.
     *
     * @return xml element qname associated with jaxbElement;
     *         null if <code>jaxbElement</code> is not a JAXBElement.
     */
    public abstract QName getElementName(Object jaxbElement);

    /**
     * <p>Get the element value of a Jakarta XML Binding element.</p>
     *
     * <p>Convenience method to abstract whether working with either
     *    a jakarta.xml.bind.JAXBElement instance or an instance of
     *    {@code @XmlRootElement} annotated Java class.</p>
     *
     * @param jaxbElement  object that #isElement(Object) returns true.
     *
     * @return The element value of the <code>jaxbElement</code>.
     */
    public static Object getValue(Object jaxbElement) {
	if (jaxbElement instanceof JAXBElement) {
	    return ((JAXBElement)jaxbElement).getValue();
	} else {
	    // assume that class of this instance is
	    // annotated with @XmlRootElement.
	    return jaxbElement;
	}
    }
}
