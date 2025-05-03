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

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * <p>
 * Maps a JavaBean property to an XML attribute. 
 *
 * <p> <b>Usage</b> </p>
 * <p>
 * The {@code @XmlAttribute} annotation can be used with the
 * following program elements: 
 * <ul> 
 *   <li> JavaBean property </li>
 *   <li> field </li>
 * </ul>
 *
 * <p> A static final field is mapped to an XML fixed attribute.
 *
 * <p>See "Package Specification" in jakarta.xml.bind.package javadoc for
 * additional common information.</p>
 *
 * The usage is subject to the following constraints:
 * <ul>
 *   <li> If type of the field or the property is a collection
 *        type, then the collection item type must be mapped to schema
 *        simple type.
 * {@snippet :
 *  // Examples
 *  @XmlAttribute List<Integer> items; //legal
 *  @XmlAttribute List<Bar> foo; // illegal if Bar does not map to a schema simple type
 * }
 *   </li>
 *   <li> If the type of the field or the property is a non
 *         collection type, then the type of the property or field
 *         must map to a simple schema type.
 * {@snippet :
 *  // Examples
 *  @XmlAttribute int foo; // legal
 *  @XmlAttribute Foo foo; // illegal if Foo does not map to a schema simple type
 * }
 *   </li>
 *   <li> This annotation can be used with the following annotations:
 *            {@link XmlID}, 
 *            {@link XmlIDREF},
 *            {@link XmlList},
 *            {@link XmlSchemaType},
 *            {@link XmlValue},
 *            {@link XmlAttachmentRef},
 *            {@link XmlMimeType},
 *            {@link XmlInlineBinaryData},
 *            {@link jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter}.</li>
 * </ul>
 *
 * <p> <b>Example 1: </b>Map a JavaBean property to an XML attribute.</p>
 * {@snippet :
 *  //Example: Code fragment
 *  public class USPrice {
 *      @XmlAttribute
 *      public java.math.BigDecimal getPrice() {...} ;
 *      public void setPrice(java.math.BigDecimal ) {...};
 *  }
 * }
 * {@snippet lang="XML" :
 *  <!-- Example: XML Schema fragment -->
 *  <xs:complexType name="USPrice">
 *    <xs:sequence>
 *    </xs:sequence>
 *    <xs:attribute name="price" type="xs:decimal"/>
 *  </xs:complexType>
 * }
 *
 * <p> <b>Example 2: </b>Map a JavaBean property to an XML attribute with anonymous type.</p>
 * See Example 7 in @{@link XmlType}.
 *
 * <p> <b>Example 3: </b>Map a JavaBean collection property to an XML attribute.</p>
 * {@snippet :
 *  // Example: Code fragment
 *  class Foo {
 *      ...
 *      @XmlAttribute
 *      List<Integer> items;
 *  }
 * }
 * {@snippet lang="XML" :
 *  <!-- Example: XML Schema fragment -->
 *  <xs:complexType name="foo">
 *    ...
 *    <xs:attribute name="items">
 *      <xs:simpleType>
 *        <xs:list itemType="xs:int"/>
 *      </xs:simpleType>
      </xs:attribute>
 *  </xs:complexType>
 * }
 * @author Sekhar Vajjhala, Sun Microsystems, Inc.
 * @see XmlType
 * @since 1.6, JAXB 2.0
 */
@Retention(RUNTIME) @Target({FIELD, METHOD})
public @interface XmlAttribute {
    /**
     * Name of the XML Schema attribute. By default, the XML Schema
     * attribute name is derived from the JavaBean property name.
     *
     */
    String name() default "##default";
 
    /**
     * Specifies if the XML Schema attribute is optional or
     * required. If true, then the JavaBean property is mapped to
     * an XML Schema attribute that is required. Otherwise, it is mapped
     * to an XML Schema attribute that is optional.
     *
     */
     boolean required() default false;

    /**
     * Specifies the XML target namespace of the XML Schema
     * attribute.
     * 
     */
    String namespace() default "##default" ;
}
