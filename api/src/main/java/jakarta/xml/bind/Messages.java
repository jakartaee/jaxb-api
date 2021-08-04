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
    static final String PROVIDER_NOT_FOUND = // 1 arg
        "ContextFinder.ProviderNotFound";

    static final String DEFAULT_PROVIDER_NOT_FOUND = // 0 args
        "ContextFinder.DefaultProviderNotFound";

    static final String COULD_NOT_INSTANTIATE = // 2 args
        "ContextFinder.CouldNotInstantiate";
        
    static final String CANT_FIND_PROPERTIES_FILE = // 1 arg
        "ContextFinder.CantFindPropertiesFile";
        
    static final String CANT_MIX_PROVIDERS = // 0 args
        "ContextFinder.CantMixProviders";
        
    static final String MISSING_PROPERTY = // 2 args
        "ContextFinder.MissingProperty";

    static final String NO_PACKAGE_IN_CONTEXTPATH = // 0 args
        "ContextFinder.NoPackageInContextPath";

    static final String NAME_VALUE = // 2 args
        "PropertyException.NameValue";
        
    static final String CONVERTER_MUST_NOT_BE_NULL = // 0 args
        "DatatypeConverter.ConverterMustNotBeNull";

    static final String ILLEGAL_CAST = // 2 args
        "JAXBContext.IllegalCast";

    static final String ERROR_LOAD_CLASS = // 2 args
            "ContextFinder.ErrorLoadClass";

    static final String JAXB_CLASSES_NOT_OPEN = // 1 arg
            "JAXBClasses.notOpen";
}
