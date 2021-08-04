/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind.annotation;

import jakarta.xml.bind.ValidationEventHandler;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

/**
 * Converts an element (and its descendants)
 * from/to DOM (or similar) representation.
 *
 * <p>
 * Implementations of this interface will be used in conjunction with
 * {@link XmlAnyElement} annotation to map an element of XML into a representation
 * of infoset such as W3C DOM.
 *
 * <p>
 * Implementations hide how a portion of XML is converted into/from such
 * DOM-like representation, allowing Jakarta XML Binding providers to work with arbitrary
 * such library.
 *
 * <P>
 * This interface is intended to be implemented by library writers
 * and consumed by Jakarta XML Binding providers. None of those methods are intended to
 * be called from applications.
 *
 * @author Kohsuke Kawaguchi
 * @since 1.6, JAXB 2.0
 */
public interface DomHandler<ElementT,ResultT extends Result> {
    /**
     * When a Jakarta XML Binding provider needs to unmarshal a part of a document into an
     * infoset representation, it first calls this method to create a
     * {@link Result} object.
     *
     * <p>
     * A Jakarta XML Binding provider will then send a portion of the XML
     * into the given result. Such a portion always form a subtree
     * of the whole XML document rooted at an element.
     *
     * @param errorHandler
     *      if any error happens between the invocation of this method
     *      and the invocation of {@link #getElement(Result)}, they
     *      must be reported to this handler.
     *
     *      The caller must provide a non-null error handler.
     *
     *      The {@link Result} object created from this method
     *      may hold a reference to this error handler.
     *
     * @return
     *      null if the operation fails. The error must have been reported
     *      to the error handler.
     */
    ResultT createUnmarshaller( ValidationEventHandler errorHandler );

    /**
     * Once the portion is sent to the {@link Result}. This method is called
     * by a Jakarta XML Binding provider to obtain the unmarshalled element representation.
     *
     * <p>
     * Multiple invocations of this method may return different objects.
     * This method can be invoked only when the whole sub-tree are fed
     * to the {@link Result} object.
     *
     * @param rt
     *      The {@link Result} object created by {@link #createUnmarshaller(ValidationEventHandler)}.
     *
     * @return
     *      null if the operation fails. The error must have been reported
     *      to the error handler.
     */
    ElementT getElement(ResultT rt);

    /**
     * This method is called when a Jakarta XML Binding provider needs to marshal an element
     * to XML.
     *
     * <p>
     * If non-null, the returned {@link Source} must contain a whole document
     * rooted at one element, which will then be weaved into a bigger document
     * that the Jakarta XML Binding provider is marshalling.
     *
     * @param errorHandler
     *      Receives any errors happened during the process of converting
     *      an element into a {@link Source}.
     *
     *      The caller must provide a non-null error handler.
     *
     * @return
     *      null if there was an error. The error should have been reported
     *      to the handler.
     */
    Source marshal( ElementT n, ValidationEventHandler errorHandler );
}
