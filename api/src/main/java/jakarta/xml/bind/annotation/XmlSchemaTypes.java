/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 2005, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

// Contributor(s):
//     Sekhar Vajjhala

package jakarta.xml.bind.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>
 * A container for multiple {@linkplain XmlSchemaType} annotations.
 *
 * <p> Multiple annotations of the same type are not allowed on a program
 * element. This annotation therefore serves as a container annotation for multiple {@linkplain XmlSchemaType}
 * annotations as follows:
 * <p>
 * {@snippet :
 *  @XmlSchemaTypes({ @XmlSchemaType(...), @XmlSchemaType(...) })
 *}
 * <p>The {@code @XmlSchemaTypes} annotation can be used to
 * define {@linkplain XmlSchemaType} for different types at the package level.
 *
 * <p>See "Package Specification" in jakarta.xml.bind.package javadoc for
 * additional common information.</p>
 *
 * @see XmlSchemaType
 */
@Retention(RUNTIME)
@Target({PACKAGE})
public @interface XmlSchemaTypes {
    /**
     * Collection of @{@linkplain XmlSchemaType} annotations
     */
    XmlSchemaType[] value();
}
