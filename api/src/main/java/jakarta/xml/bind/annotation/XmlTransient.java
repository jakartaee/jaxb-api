/*
 * Copyright (c) 2004, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind.annotation;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * <p>
 * Prevents the mapping of a JavaBean property/type to XML representation.
 * <p>
 * The {@code @XmlTransient} annotation is useful for resolving name
 * collisions between a JavaBean property name and a field name or
 * preventing the mapping of a field/property. A name collision can
 * occur when the decapitalized JavaBean property name and a field
 * name are the same. If the JavaBean property refers to the field,
 * then the name collision can be resolved by preventing the
 * mapping of either the field or the JavaBean property using the
 * {@code @XmlTransient} annotation.
 *
 * <p>
 * When placed on a class, it indicates that the class shouldn't be mapped
 * to XML by itself. Properties on such class will be mapped to XML along
 * with its derived classes, as if the class is inlined.
 *
 * <p><b>Usage</b></p>
 * <p> The {@code @XmlTransient} annotation can be used with the following
 *     program elements: 
 * <ul> 
 *   <li> a JavaBean property </li>
 *   <li> field </li>
 *   <li> class </li>
 * </ul>
 *
 * <p>{@code @XmlTransient} is mutually exclusive with all other
 * Jakarta XML Binding defined annotations. </p>
 * 
 * <p>See "Package Specification" in jakarta.xml.bind.package javadoc for
 * additional common information.</p>
 *
 * <p><b>Example:</b> Resolve name collision between JavaBean property and
 *     field name </p>
 * 
 * {@snippet :
 *  // Example: Code fragment
 *  public class USAddress {
 *
 *      // The field name "name" collides with the property name
 *      // obtained by bean decapitalization of getName() below
 *      @XmlTransient public String name;
 *
 *      String getName() {..};
 *      String setName() {..};
 *  }
 * }
 * {@snippet lang="XML" :
 *  <!-- Example: XML Schema fragment -->
 *  <xs:complexType name="USAddress">
 *    <xs:sequence>
 *      <xs:element name="name" type="xs:string"/>
 *    </xs:sequence>
 *  </xs:complexType>
 * }
 *
 * @author Sekhar Vajjhala, Sun Microsystems, Inc.
 * @since 1.6, JAXB 2.0
 */
@Retention(RUNTIME) @Target({FIELD, METHOD, TYPE})
public @interface XmlTransient {}
   
