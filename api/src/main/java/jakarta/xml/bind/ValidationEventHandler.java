/*
 * Copyright (c) 2003, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind;

/**
 * A basic event handler interface for validation errors.
 *
 * <p>
 * If an application needs to implement customized event handling, it must
 * implement this interface and then register it with either the 
 * {@link Unmarshaller#setEventHandler(ValidationEventHandler) Unmarshaller}, or
 * the {@link Marshaller#setEventHandler(ValidationEventHandler) Marshaller}.  
 * The Jakarta XML Binding Provider will then report validation errors and warnings encountered
 * during the unmarshal, marshal, and validate operations to these event 
 * handlers.
 *
 * <p>
 * If the {@code handleEvent} method throws an unchecked runtime exception,
 * the Jakarta XML Binding Provider must treat that as if the method returned false, effectively
 * terminating whatever operation was in progress at the time (unmarshal, 
 * validate, or marshal).
 * 
 * <p>
 * Modifying the Java content tree within your event handler is undefined
 * by the specification and may result in unexpected behaviour.
 *
 * <p>
 * Failing to return false from the {@code handleEvent} method after
 * encountering a fatal error is undefined by the specification and may result 
 * in unexpected behavior.
 *
 * <p>
 * <b>Default Event Handler</b>
 * <blockquote>
 *    If the client application does not set an event handler on their
 *    {@code Unmarshaller}, or {@code Marshaller} prior to
 *    calling the validate, unmarshal, or marshal methods, then a default event
 *    handler will receive notification of any errors or warnings encountered.
 *    The default event handler will cause the current operation to halt after
 *    encountering the first error or fatal error (but will attempt to continue
 *    after receiving warnings).
 * </blockquote>
 *
 * @author <ul><li>Ryan Shoemaker, Sun Microsystems, Inc.</li>
 *             <li>Kohsuke Kawaguchi, Sun Microsystems, Inc.</li>
 *             <li>Joe Fialli, Sun Microsystems, Inc.</li></ul>
 * @see Unmarshaller
 * @see Marshaller
 * @see ValidationEvent
 * @see jakarta.xml.bind.util.ValidationEventCollector
 * @since 1.6, JAXB 1.0
 */
public interface ValidationEventHandler {
    /**
     * Receive notification of a validation warning or error.  
     * 
     * The ValidationEvent will have a 
     * {@link ValidationEventLocator ValidationEventLocator} embedded in it that 
     * indicates where the error or warning occurred.
     *
     * <p>
     * If an unchecked runtime exception is thrown from this method, the Jakarta XML Binding
     * provider will treat it as if the method returned false and interrupt
     * the current unmarshal, validate, or marshal operation.
     * 
     * @param event the encapsulated validation event information.  It is a 
     * provider error if this parameter is null.
     * @return true if the Jakarta XML Binding Provider should attempt to continue the current
     *         unmarshal, validate, or marshal operation after handling this 
     *         warning/error, false if the provider should terminate the current 
     *         operation with the appropriate {@code UnmarshalException},
     *         {@code ValidationException}, or {@code MarshalException}.
     * @throws IllegalArgumentException if the event object is null.
     */
    boolean handleEvent(ValidationEvent event);
    
}

