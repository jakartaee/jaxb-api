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
 * Enables mapping a class to a  XML Schema complex type with a
 * simpleContent or an XML Schema simple type. 
 * </p>
 *
 * <p>
 * <b> Usage: </b>
 * <p>
 * The {@code @XmlValue} annotation can be used with the following program
 * elements: 
 * <ul> 
 *   <li> a JavaBean property.</li>
 *   <li> non-static, non transient field.</li>
 * </ul>
 * 
 * <p>See "Package Specification" in jakarta.xml.bind.package javadoc for
 * additional common information.</p>
 *
 * The usage is subject to the following usage constraints:
 * <ul>
 *   <li>At most one field or property can be annotated with the
 *       {@code @XmlValue} annotation. </li>
 *
 *   <li>{@code @XmlValue} can be used with the following
 *   annotations: {@link XmlList}. However this is redundant since
 *   {@link XmlList} maps a type to a simple schema type that derives by
 *   list just as {@link XmlValue} would. </li>
 *
 *   <li>If the type of the field or property is a collection type,
 *       then the collection item type must map to a simple schema
 *       type.  </li>
 * 
 *   <li>If the type of the field or property is not a collection
 *       type, then the type must map to an XML Schema simple type. </li>
 *
 * </ul>
 * <p>
 * If the annotated JavaBean property is the sole class member being
 * mapped to XML Schema construct, then the class is mapped to a
 * simple type. 
 * <p>
 * If there are additional JavaBean properties (other than the
 * JavaBean property annotated with {@code @XmlValue} annotation)
 * that are mapped to XML attributes, then the class is mapped to a
 * complex type with simpleContent.
 * </p>
 *
 * <p> <b> Example 1: </b> Map a class to XML Schema simpleType</p>
 *
 * {@snippet :
 *  // Example 1: Code fragment
 *  public class USPrice {
 *      @XmlValue
 *      public java.math.BigDecimal price;
 *  }
 * }
 * {@snippet lang="XML" :
 *  <!-- Example 1: XML Schema fragment -->
 *  <xs:simpleType name="USPrice">
 *    <xs:restriction base="xs:decimal"/>
 *  </xs:simpleType>
 * }
 * 
 * <p><b> Example 2: </b> Map a class to XML Schema complexType with
 *        with simpleContent.</p>
 *
 * {@snippet :
 *  // Example 2: Code fragment
 *  public class InternationalPrice {
 *      @XmlValue
 *      public java.math.BigDecimal price;
 * 
 *      @XmlAttribute
 *      public String currency;
 *  }
 * }
 * {@snippet lang="XML" :
 *  <!-- Example 2: XML Schema fragment -->
 *  <xs:complexType name="InternationalPrice">
 *    <xs:simpleContent>
 *      <xs:extension base="xs:decimal">
 *        <xs:attribute name="currency" type="xs:string"/>
 *      </xs:extension>
 *    </xs:simpleContent>
 *  </xs:complexType>
 * }
 *
 * @author Sekhar Vajjhala, Sun Microsystems, Inc.
 * @see XmlType
 * @since 1.6, JAXB 2.0
 */
@Retention(RUNTIME) @Target({FIELD, METHOD})
public @interface XmlValue {}
