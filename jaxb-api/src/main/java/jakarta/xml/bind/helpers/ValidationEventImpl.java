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

import java.text.MessageFormat;

import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.ValidationEventLocator;

/**
 * Default implementation of the ValidationEvent interface.
 *
 * <p>
 * Jakarta XML Binding providers are allowed to use whatever class that implements
 * the ValidationEvent interface. This class is just provided for a
 * convenience.
 *
 * @author <ul><li>Kohsuke Kawaguchi, Sun Microsystems, Inc.</li></ul>
 * @see jakarta.xml.bind.ValidationEventHandler
 * @see jakarta.xml.bind.ValidationEvent
 * @see jakarta.xml.bind.ValidationEventLocator
 * @since 1.6, JAXB 1.0
 */
public class ValidationEventImpl implements ValidationEvent
{

    /**
     * Create a new ValidationEventImpl.
     *
     * @param _severity The severity value for this event.  Must be one of
     * ValidationEvent.WARNING, ValidationEvent.ERROR, or
     * ValidationEvent.FATAL_ERROR
     * @param _message The text message for this event - may be null.
     * @param _locator The locator object for this event - may be null.
     * @throws IllegalArgumentException if an illegal severity field is supplied
     */
    public ValidationEventImpl( int _severity, String _message,
                                 ValidationEventLocator _locator ) {

        this(_severity,_message,_locator,null);
    }

    /**
     * Create a new ValidationEventImpl.
     *
     * @param _severity The severity value for this event.  Must be one of
     * ValidationEvent.WARNING, ValidationEvent.ERROR, or
     * ValidationEvent.FATAL_ERROR
     * @param _message The text message for this event - may be null.
     * @param _locator The locator object for this event - may be null.
     * @param _linkedException An optional linked exception that may provide
     * additional information about the event - may be null.
     * @throws IllegalArgumentException if an illegal severity field is supplied
     */
    public ValidationEventImpl( int _severity, String _message,
                                 ValidationEventLocator _locator,
                                 Throwable _linkedException ) {

        setSeverity( _severity );
        this.message = _message;
        this.locator = _locator;
        this.linkedException = _linkedException;
    }

    private int severity;
    private String message;
    private Throwable linkedException;
    private ValidationEventLocator locator;

    @Override
    public int getSeverity() {
        return severity;
    }


    /**
     * Set the severity field of this event.
     *
     * @param _severity Must be one of ValidationEvent.WARNING,
     * ValidationEvent.ERROR, or ValidationEvent.FATAL_ERROR.
     * @throws IllegalArgumentException if an illegal severity field is supplied
     */
    public void setSeverity( int _severity ) {

        if( _severity != ValidationEvent.WARNING &&
            _severity != ValidationEvent.ERROR &&
            _severity != ValidationEvent.FATAL_ERROR ) {
                throw new IllegalArgumentException(
                    Messages.format( Messages.ILLEGAL_SEVERITY ) );
        }

        this.severity = _severity;
    }

    @Override
    public String getMessage() {
        return message;
    }
    /**
     * Set the message field of this event.
     *
     * @param _message String message - may be null.
     */
    public void setMessage( String _message ) {
        this.message = _message;
    }

    @Override
    public Throwable getLinkedException() {
        return linkedException;
    }
    /**
     * Set the linked exception field of this event.
     *
     * @param _linkedException Optional linked exception - may be null.
     */
    public void setLinkedException( Throwable _linkedException ) {
        this.linkedException = _linkedException;
    }

    @Override
    public ValidationEventLocator getLocator() {
        return locator;
    }
    /**
     * Set the locator object for this event.
     *
     * @param _locator The locator - may be null.
     */
    public void setLocator( ValidationEventLocator _locator ) {
        this.locator = _locator;
    }

    /**
     * Returns a string representation of this object in a format
     * helpful to debugging.
     *
     * @see Object#equals(Object)
     */
    @Override
    public String toString() {
        String s;
        switch(getSeverity()) {
        case WARNING:   s="WARNING";break;
        case ERROR: s="ERROR";break;
        case FATAL_ERROR: s="FATAL_ERROR";break;
        default: s=String.valueOf(getSeverity());break;
        }
        return MessageFormat.format("[severity={0},message={1},locator={2}]",
            new Object[]{
                s,
                getMessage(),
                getLocator()
            });
    }
}
