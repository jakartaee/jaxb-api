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
//     Kohsuke Kawaguchi
//     Sekhar Vajjhala

package jakarta.xml.bind.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used to map a property to a list simple type.
 *
 * <p><b>Usage</b> </p>
 * <p>
 * The {@code @XmlList} annotation can be used with the following program elements:
 * <ul>
 *   <li> JavaBean property </li>
 *   <li> field </li>
 * </ul>
 *
 * <p>
 * When a collection property is annotated just with @XmlElement,
 * each item in the collection will be wrapped by an element.
 * For example,
 * <p>
 * {@snippet :
 *  @XmlRootElement
 *  class Foo {
 *      @XmlElement
 *      List<String> data;
 *  }
 *}
 * <p>
 * would produce XML like this:
 * <p>
 * {@snippet lang = "XML":
 *  <foo>
 *    <data>abc</data>
 *    <data>def</data>
 *  </foo>
 *}
 * <p>
 * XmlList annotation, on the other hand, allows multiple values to be
 * represented as whitespace-separated tokens in a single element. For example,
 * <p>
 * {@snippet :
 *  @XmlRootElement
 *  class Foo {
 *      @XmlElement
 *      @XmlList
 *      List<String> data;
 *  }
 *}
 * <p>
 * the above code will produce XML like this:
 * <p>
 * {@snippet lang = "XML":
 *  <foo>
 *    <data>abc def</data>
 *  </foo>
 *}
 *
 * <p>This annotation can be used with the following annotations:
 *        {@linkplain XmlElement},
 *        {@linkplain XmlAttribute},
 *        {@linkplain XmlValue},
 *        {@linkplain XmlIDREF}.
 *  <ul>
 *    <li> The use of {@code @XmlList} with {@linkplain XmlValue} while
 *         allowed, is redundant since  {@linkplain XmlList} maps a
 *         collection type to a simple schema type that derives by
 *         list just as {@linkplain XmlValue} would. </li>
 *
 *    <li> The use of {@code @XmlList} with {@linkplain XmlAttribute} while
 *         allowed, is redundant since  {@linkplain XmlList} maps a
 *         collection type to a simple schema type that derives by
 *         list just as {@linkplain XmlAttribute} would. </li>
 *  </ul>
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD, PARAMETER})
public @interface XmlList {
}
