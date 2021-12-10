/*
 * Copyright (c) 2006, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind;

/**
 * Exception that represents a failure in a Jakarta XML Binding operation.
 *
 * <p>
 * This exception differs from {@link JAXBException} in that
 * this is an unchecked exception, while {@code JAXBException}
 * is a checked exception.
 *
 * @see JAXB
 * @since 1.6, JAXB 2.1
 */
public class DataBindingException extends RuntimeException {

    private static final long serialVersionUID = 4743686626270704879L;

    public DataBindingException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataBindingException(Throwable cause) {
        super(cause);
    }
}
