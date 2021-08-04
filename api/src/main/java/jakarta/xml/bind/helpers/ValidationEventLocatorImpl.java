/*
 * Copyright (c) 2003, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind.helpers;

import java.net.URL;
import java.net.MalformedURLException;
import java.text.MessageFormat;

import jakarta.xml.bind.ValidationEventLocator;
import org.w3c.dom.Node;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

/**
 * Default implementation of the ValidationEventLocator interface.
 *
 * <p>
 * Jakarta XML Binding providers are allowed to use whatever class that implements
 * the ValidationEventLocator interface. This class is just provided for a
 * convenience.
 *
 * @author <ul><li>Kohsuke Kawaguchi, Sun Microsystems, Inc.</li></ul>
 * @see jakarta.xml.bind.ValidationEventHandler
 * @see jakarta.xml.bind.ValidationEvent
 * @see jakarta.xml.bind.ValidationEventLocator
 * @since 1.6, JAXB 1.0
 */
public class ValidationEventLocatorImpl implements ValidationEventLocator
{
    /**
     * Creates an object with all fields unavailable.
     */
    public ValidationEventLocatorImpl() {
    }

    /**
     * Constructs an object from an org.xml.sax.Locator.
     *
     * The object's ColumnNumber, LineNumber, and URL become available from the
     * values returned by the locator's getColumnNumber(), getLineNumber(), and
     * getSystemId() methods respectively. Node, Object, and Offset are not
     * available.
     *
     * @param loc the SAX Locator object that will be used to populate this
     * event locator.
     * @throws IllegalArgumentException if the Locator is null
     */
    public ValidationEventLocatorImpl( Locator loc ) {
        if( loc == null ) {
            throw new IllegalArgumentException(
                Messages.format( Messages.MUST_NOT_BE_NULL, "loc" ) );
        }

        this.url = toURL(loc.getSystemId());
        this.columnNumber = loc.getColumnNumber();
        this.lineNumber = loc.getLineNumber();
    }

    /**
     * Constructs an object from the location information of a SAXParseException.
     *
     * The object's ColumnNumber, LineNumber, and URL become available from the
     * values returned by the locator's getColumnNumber(), getLineNumber(), and
     * getSystemId() methods respectively. Node, Object, and Offset are not
     * available.
     *
     * @param e the SAXParseException object that will be used to populate this
     * event locator.
     * @throws IllegalArgumentException if the SAXParseException is null
     */
    public ValidationEventLocatorImpl( SAXParseException e ) {
        if( e == null ) {
            throw new IllegalArgumentException(
                Messages.format( Messages.MUST_NOT_BE_NULL, "e" ) );
        }

        this.url = toURL(e.getSystemId());
        this.columnNumber = e.getColumnNumber();
        this.lineNumber = e.getLineNumber();
    }

    /**
     * Constructs an object that points to a DOM Node.
     *
     * The object's Node becomes available.  ColumnNumber, LineNumber, Object,
     * Offset, and URL are not available.
     *
     * @param _node the DOM Node object that will be used to populate this
     * event locator.
     * @throws IllegalArgumentException if the Node is null
     */
    public ValidationEventLocatorImpl(Node _node) {
        if( _node == null ) {
            throw new IllegalArgumentException(
                Messages.format( Messages.MUST_NOT_BE_NULL, "_node" ) );
        }

        this.node = _node;
    }

    /**
     * Constructs an object that points to a Jakarta XML Binding content object.
     *
     * The object's Object becomes available. ColumnNumber, LineNumber, Node,
     * Offset, and URL are not available.
     *
     * @param _object the Object that will be used to populate this
     * event locator.
     * @throws IllegalArgumentException if the Object is null
     */
    public ValidationEventLocatorImpl(Object _object) {
        if( _object == null ) {
            throw new IllegalArgumentException(
                Messages.format( Messages.MUST_NOT_BE_NULL, "_object" ) );
        }

        this.object = _object;
    }

    /** Converts a system ID to an URL object. */
    private static URL toURL( String systemId ) {
        try {
            return new URL(systemId);
        } catch( MalformedURLException e ) {
            // TODO: how should we handle system id here?
            return null;    // for now
        }
    }

    private URL url = null;
    private int offset = -1;
    private int lineNumber = -1;
    private int columnNumber = -1;
    private Object object = null;
    private Node node = null;


    /**
     * @see jakarta.xml.bind.ValidationEventLocator#getURL()
     */
    @Override
    public URL getURL() {
        return url;
    }

    /**
     * Set the URL field on this event locator.  Null values are allowed.
     *
     * @param _url the url
     */
    public void setURL( URL _url ) {
        this.url = _url;
    }

    /**
     * @see jakarta.xml.bind.ValidationEventLocator#getOffset()
     */
    @Override
    public int getOffset() {
        return offset;
    }

    /**
     * Set the offset field on this event locator.
     *
     * @param _offset the offset
     */
    public void setOffset( int _offset ) {
        this.offset = _offset;
    }

    /**
     * @see jakarta.xml.bind.ValidationEventLocator#getLineNumber()
     */
    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Set the lineNumber field on this event locator.
     *
     * @param _lineNumber the line number
     */
    public void setLineNumber( int _lineNumber ) {
        this.lineNumber = _lineNumber;
    }

    /**
     * @see jakarta.xml.bind.ValidationEventLocator#getColumnNumber()
     */
    @Override
    public int getColumnNumber() {
        return columnNumber;
    }

    /**
     * Set the columnNumber field on this event locator.
     *
     * @param _columnNumber the column number
     */
    public void setColumnNumber( int _columnNumber ) {
        this.columnNumber = _columnNumber;
    }

    /**
     * @see jakarta.xml.bind.ValidationEventLocator#getObject()
     */
    @Override
    public Object getObject() {
        return object;
    }

    /**
     * Set the Object field on this event locator.  Null values are allowed.
     *
     * @param _object the java content object
     */
    public void setObject( Object _object ) {
        this.object = _object;
    }

    /**
     * @see jakarta.xml.bind.ValidationEventLocator#getNode()
     */
    @Override
    public Node getNode() {
        return node;
    }

    /**
     * Set the Node field on this event locator.  Null values are allowed.
     *
     * @param _node the Node
     */
    public void setNode( Node _node ) {
        this.node = _node;
    }

    /**
     * Returns a string representation of this object in a format
     * helpful to debugging.
     *
     * @see Object#equals(Object)
     */
    @Override
    public String toString() {
        return MessageFormat.format("[node={0},object={1},url={2},line={3},col={4},offset={5}]",
            getNode(),
            getObject(),
            getURL(),
            String.valueOf(getLineNumber()),
            String.valueOf(getColumnNumber()),
            String.valueOf(getOffset()));
    }
}
