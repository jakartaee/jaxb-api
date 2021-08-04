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
 * This exception indicates that an error has occurred while performing
 * a marshal operation that the provider is unable to recover from.
 * 
 * <p>
 * The {@code ValidationEventHandler} can cause this exception to be thrown
 * during the marshal operations.  See 
 * {@link ValidationEventHandler#handleEvent(ValidationEvent)
 * ValidationEventHandler.handleEvent(ValidationEvent)}.
 *
 * @author <ul><li>Ryan Shoemaker, Sun Microsystems, Inc.</li></ul>
 * @see JAXBException
 * @see Marshaller
 * @since 1.6, JAXB 1.0
 */
public class MarshalException extends JAXBException {

    /** 
     * Construct a MarshalException with the specified detail message.  The 
     * errorCode and linkedException will default to null.
     *
     * @param message a description of the exception
     */
    public MarshalException( String message ) {
        this( message, null, null );
    }

    /** 
     * Construct a MarshalException with the specified detail message and vendor 
     * specific errorCode.  The linkedException will default to null.
     *
     * @param message a description of the exception
     * @param errorCode a string specifying the vendor specific error code
     */
    public MarshalException( String message, String errorCode ) {
        this( message, errorCode, null );
    }

    /** 
     * Construct a MarshalException with a linkedException.  The detail message and
     * vendor specific errorCode will default to null.
     *
     * @param exception the linked exception
     */
    public MarshalException( Throwable exception ) {
        this( null, null, exception );
    }
    
    /** 
     * Construct a MarshalException with the specified detail message and 
     * linkedException.  The errorCode will default to null.
     *
     * @param message a description of the exception
     * @param exception the linked exception
     */
    public MarshalException( String message, Throwable exception ) {
        this( message, null, exception );
    }
    
    /** 
     * Construct a MarshalException with the specified detail message, vendor 
     * specific errorCode, and linkedException.
     *
     * @param message a description of the exception
     * @param errorCode a string specifying the vendor specific error code
     * @param exception the linked exception
     */
    public MarshalException( String message, String errorCode, Throwable exception ) {
        super( message, errorCode, exception );
    }

}


