/*
 * Copyright (c) 2005, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.PACKAGE;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * A container for multiple {@link XmlSchemaType} annotations.
 *
 * <p> Multiple annotations of the same type are not allowed on a program
 * element. This annotation therefore serves as a container annotation
 * for multiple {@link XmlSchemaType} annotations as follows:
 *
 * {@snippet :
 *  @XmlSchemaTypes({ @XmlSchemaType(...), @XmlSchemaType(...) })
 * }
 * <p>The {@code @XmlSchemaTypes} annotation can be used to
 * define {@link XmlSchemaType} for different types at the
 * package level.
 *
 * <p>See "Package Specification" in jakarta.xml.bind.package javadoc for
 * additional common information.</p>
 *
 * @author <ul><li>Sekhar Vajjhala, Sun Microsystems, Inc.</li></ul>
 * @see XmlSchemaType
 * @since 1.6, JAXB 2.0
 */
@Retention(RUNTIME) @Target({PACKAGE})
public @interface XmlSchemaTypes {
    /**
     * Collection of @{@link XmlSchemaType} annotations
     */
    XmlSchemaType[] value();
}
