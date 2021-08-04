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

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import jakarta.xml.bind.ValidationEventHandler;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

/**
 * {@link DomHandler} implementation for W3C DOM (<code>org.w3c.dom</code> package.)
 *
 * @author Kohsuke Kawaguchi
 * @since 1.6, JAXB 2.0
 */
public class W3CDomHandler implements DomHandler<Element,DOMResult> {

    private DocumentBuilder builder;

    /**
     * Default constructor.
     *
     * It is up to a Jakarta XML Binding provider to decide which DOM implementation
     * to use or how that is configured.
     */
    public W3CDomHandler() {
        this.builder = null;
    }

    /**
     * Constructor that allows applications to specify which DOM implementation
     * to be used.
     *
     * @param builder
     *      must not be null. Jakarta XML Binding uses this {@link DocumentBuilder} to create
     *      a new element.
     */
    public W3CDomHandler(DocumentBuilder builder) {
        if(builder==null)
            throw new IllegalArgumentException();
        this.builder = builder;
    }

    public DocumentBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(DocumentBuilder builder) {
        this.builder = builder;
    }

    @Override
    public DOMResult createUnmarshaller(ValidationEventHandler errorHandler) {
        if(builder==null)
            return new DOMResult();
        else
            return new DOMResult(builder.newDocument());
    }

    @Override
    public Element getElement(DOMResult r) {
        // JAXP spec is ambiguous about what really happens in this case,
        // so work defensively
        Node n = r.getNode();
        if( n instanceof Document ) {
            return ((Document)n).getDocumentElement();
        }
        if( n instanceof Element )
            return (Element)n;
        if( n instanceof DocumentFragment )
            return (Element)n.getChildNodes().item(0);

        // if the result object contains something strange,
        // it is not a user problem, but it is a Jakarta XML Binding provider's problem.
        // That's why we throw a runtime exception.
        throw new IllegalStateException(n.toString());
    }

    @Override
    public Source marshal(Element element, ValidationEventHandler errorHandler) {
        return new DOMSource(element);
    }
}
