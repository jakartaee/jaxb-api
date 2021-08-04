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



/**
 * This exception indicates that an error was encountered while getting or
 * setting a property.
 * 
 * @author <ul><li>Ryan Shoemaker, Sun Microsystems, Inc.</li><li>Kohsuke Kawaguchi, Sun Microsystems, Inc.</li><li>Joe Fialli, Sun Microsystems, Inc.</li></ul>
 * @see JAXBContext
 * @see Unmarshaller
 * @since 1.6, JAXB 1.0
 */
public class PropertyException extends JAXBException {
    
    /** 
     * Construct a PropertyException with the specified detail message.  The 
     * errorCode and linkedException will default to null.
     *
     * @param message a description of the exception
     */
    public PropertyException(String message) {
    	super(message);
    }
    
    /** 
     * Construct a PropertyException with the specified detail message and 
     * vendor specific errorCode.  The linkedException will default to null.
     *
     * @param message a description of the exception
     * @param errorCode a string specifying the vendor specific error code
     */
    public PropertyException(String message, String errorCode) {
    	super(message, errorCode);
    }
    
    /** 
     * Construct a PropertyException with a linkedException.  The detail 
     * message and vendor specific errorCode will default to null.
     *
     * @param exception the linked exception
     */
    public PropertyException(Throwable exception) {
    	super(exception);
    }
    
    /** 
     * Construct a PropertyException with the specified detail message and 
     * linkedException.  The errorCode will default to null.
     *
     * @param message a description of the exception
     * @param exception the linked exception
     */
    public PropertyException(String message, Throwable exception) {
    	super(message, exception);
    }
    
    /** 
     * Construct a PropertyException with the specified detail message, vendor 
     * specific errorCode, and linkedException.
     *
     * @param message a description of the exception
     * @param errorCode a string specifying the vendor specific error code
     * @param exception the linked exception
     */
    public PropertyException(
    	String message,
    	String errorCode,
    	Throwable exception) {
    	super(message, errorCode, exception);
    }
    
    /**
     * Construct a PropertyException whose message field is set based on the 
     * name of the property and value.toString(). 
     * 
     * @param name the name of the property related to this exception
     * @param value the value of the property related to this exception
     */
    public PropertyException(String name, Object value) {
    	super( Messages.format( Messages.NAME_VALUE, 
                                        name, 
                                        value.toString() ) );
    }
    
    
}
