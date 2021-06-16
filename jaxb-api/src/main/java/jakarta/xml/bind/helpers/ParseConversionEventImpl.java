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

import jakarta.xml.bind.ParseConversionEvent;
import jakarta.xml.bind.ValidationEventLocator;

/**
 * Default implementation of the ParseConversionEvent interface.
 * 
 * <p>
 * Jakarta XML Binding providers are allowed to use whatever class that implements
 * the ValidationEvent interface. This class is just provided for a
 * convenience.
 *
 * @author <ul><li>Ryan Shoemaker, Sun Microsystems, Inc.</li></ul> 
 * @see jakarta.xml.bind.ParseConversionEvent
 * @see jakarta.xml.bind.ValidationEventHandler
 * @see jakarta.xml.bind.ValidationEvent
 * @see jakarta.xml.bind.ValidationEventLocator
 * @since 1.6, JAXB 1.0
 */
public class ParseConversionEventImpl
    extends ValidationEventImpl
    implements ParseConversionEvent {

    /**
     * Create a new ParseConversionEventImpl.
     * 
     * @param _severity The severity value for this event.  Must be one of
     * ValidationEvent.WARNING, ValidationEvent.ERROR, or 
     * ValidationEvent.FATAL_ERROR
     * @param _message The text message for this event - may be null.
     * @param _locator The locator object for this event - may be null.
     * @throws IllegalArgumentException if an illegal severity field is supplied
     */
    public ParseConversionEventImpl( int _severity, String _message,
                                      ValidationEventLocator _locator) {
            
        super(_severity, _message, _locator);
    }

    /**
     * Create a new ParseConversionEventImpl.
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
    public ParseConversionEventImpl( int _severity, String _message,
                                      ValidationEventLocator _locator,
                                      Throwable _linkedException) {
            
        super(_severity, _message, _locator, _linkedException);
    }

}
