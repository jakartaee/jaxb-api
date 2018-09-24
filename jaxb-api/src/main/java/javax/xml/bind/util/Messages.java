/*
 * Copyright (c) 2003, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package javax.xml.bind.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Formats error messages.
 */
class Messages
{
    static String format( String property ) {
        return format( property, null );
    }
    
    static String format( String property, Object arg1 ) {
        return format( property, new Object[]{arg1} );
    }
    
    static String format( String property, Object arg1, Object arg2 ) {
        return format( property, new Object[]{arg1,arg2} );
    }
    
    static String format( String property, Object arg1, Object arg2, Object arg3 ) {
        return format( property, new Object[]{arg1,arg2,arg3} );
    }
    
    // add more if necessary.
    
    /** Loads a string resource and formats it with specified arguments. */
    static String format( String property, Object[] args ) {
        String text = ResourceBundle.getBundle(Messages.class.getName()).getString(property);
        return MessageFormat.format(text,args);
    }
    
//
//
// Message resources
//
//
    static final String UNRECOGNIZED_SEVERITY = // 1 arg
        "ValidationEventCollector.UnrecognizedSeverity";

    static final String RESULT_NULL_CONTEXT = // 0 args
        "JAXBResult.NullContext";

    static final String RESULT_NULL_UNMARSHALLER = // 0 arg
        "JAXBResult.NullUnmarshaller";
        
    static final String SOURCE_NULL_CONTEXT = // 0 args
        "JAXBSource.NullContext";

    static final String SOURCE_NULL_CONTENT = // 0 arg
        "JAXBSource.NullContent";
        
    static final String SOURCE_NULL_MARSHALLER = // 0 arg
        "JAXBSource.NullMarshaller";
        
}
