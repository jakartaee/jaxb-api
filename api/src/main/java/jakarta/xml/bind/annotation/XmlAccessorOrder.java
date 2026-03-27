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

package jakarta.xml.bind.annotation;

import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p> Controls the ordering of fields and properties in a class. </p>
 *
 * <h2>Usage </h2>
 *
 * <p> {@code @XmlAccessorOrder} annotation can be used with the following
 * program elements:</p>
 *
 * <ul>
 *   <li> package</li>
 *   <li> a top level class </li>
 * </ul>
 *
 * <p> See "Package Specification" in {@code jakarta.xml.bind} package javadoc for
 * additional common information.</p>
 *
 * <p>The effective {@linkplain XmlAccessOrder} on a class is determined
 * as follows:
 *
 * <ul>
 *   <li> If there is a {@code @XmlAccessorOrder} on a class, then
 *        it is used. </li>
 *   <li> Otherwise, if a {@code @XmlAccessorOrder} exists on one of
 *        its super classes, then it is inherited (by the virtue of
 *        {@linkplain Inherited})
 *   <li> Otherwise, the {@code @XmlAccessorOrder} on the package
 *        of the class is used, if it's there.
 *   <li> Otherwise {@linkplain XmlAccessOrder#UNDEFINED}.
 * </ul>
 *
 * <p>This annotation can be used with the following annotations:
 *    {@linkplain XmlType}, {@linkplain XmlRootElement}, {@linkplain XmlAccessorType},
 *    {@linkplain XmlSchema}, {@linkplain XmlSchemaType}, {@linkplain XmlSchemaTypes},
 *    {@linkplain XmlJavaTypeAdapter}. It can also be used with the
 *    following annotations at the package level: {@linkplain XmlJavaTypeAdapter}.
 *
 * @author Sekhar Vajjhala, Sun Microsystems, Inc.
 * @see XmlAccessOrder
 * @since 1.6, JAXB 2.0
 */

@Inherited
@Retention(RUNTIME)
@Target({PACKAGE, TYPE})
public @interface XmlAccessorOrder {
    XmlAccessOrder value() default XmlAccessOrder.UNDEFINED;
}
