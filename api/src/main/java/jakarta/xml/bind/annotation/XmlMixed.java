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

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import org.w3c.dom.Element;
import jakarta.xml.bind.JAXBElement;

/**
 * <p>
 * Annotate a JavaBean multi-valued property to support mixed content.
 *
 * <p>
 * The usage is subject to the following constraints:
 * <ul>
 *   <li> can be used with &#64;XmlElementRef, &#64;XmlElementRefs or &#64;XmlAnyElement</li>
 * </ul>
 * <p>
 * The following can be inserted into &#64;XmlMixed annotated multi-valued property
 * <ul>
 * <li>XML text information items are added as values of java.lang.String.</li>
 * <li>Children element information items are added as instances of
 * {@link JAXBElement} or instances with a class that is annotated with
 * &#64;XmlRootElement.</li>
 * <li>Unknown content that is not be bound to a Jakarta XML Binding mapped class is inserted
 * as {@link Element}. (Assumes property annotated with &#64;XmlAnyElement)</li>
 * </ul>
 *
 * Below is an example of binding and creation of mixed content.
 * {@snippet lang="XML" :
 *  <!-- schema fragment having  mixed content -->
 *  <xs:complexType name="letterBody" mixed="true">
 *    <xs:sequence>
 *      <xs:element name="name" type="xs:string"/>
 *      <xs:element name="quantity" type="xs:positiveInteger"/>
 *      <xs:element name="productName" type="xs:string"/>
 *      <!-- etc. -->
 *    </xs:sequence>
 *  </xs:complexType>
 *  <xs:element name="letterBody" type="letterBody"/>
 * }
 * {@snippet :
 *  // Schema-derived Java code: 
 *  // (Only annotations relevant to mixed content are shown below, 
 *  //  others are omitted.)
 *  import java.math.BigInteger;
 *  public class ObjectFactory {
 *      // element instance factories
 *      JAXBElement<LetterBody> createLetterBody(LetterBody value);
 *      JAXBElement<String>     createLetterBodyName(String value);
 *      JAXBElement<BigInteger> createLetterBodyQuantity(BigInteger value);
 *      JAXBElement<String>     createLetterBodyProductName(String value);
 *      // type instance factory
 *      LetterBody createLetterBody();
 *  }
 * }
 * {@snippet :
 *  public class LetterBody {
 *      // Mixed content can contain instances of Element classes
 *      // Name, Quantity and ProductName. Text data is represented as
 *      // java.util.String for text.
 *      @XmlMixed
 *      @XmlElementRefs({
 *         @XmlElementRef(name="productName", type=JAXBElement.class)
 *         @XmlElementRef(name="quantity", type=JAXBElement.class)
 *         @XmlElementRef(name="name", type=JAXBElement.class)
 *      })
 *      List getContent() {...}
 *  }
 * }
 * The following is an XML instance document with mixed content
 * {@snippet lang="XML" :
 *  <letterBody>
 *    Dear Mr.<name>Robert Smith</name>
 *    Your order of <quantity>1</quantity> <productName>Baby
 *    Monitor</productName> shipped from our warehouse. ....
 *  </letterBody>
 * }
 * that can be constructed using following Jakarta XML Binding API calls.
 * {@snippet :
 *  LetterBody lb = ObjectFactory.createLetterBody();
 *  JAXBElement<LetterBody> lbe = ObjectFactory.createLetterBody(lb);
 *  List gcl = lb.getContent();  //add mixed content to general content property.
 *  gcl.add("Dear Mr.");  // add text information item as a String.
 *
 *  // add child element information item
 *  gcl.add(ObjectFactory.createLetterBodyName("Robert Smith"));
 *  gcl.add("Your order of "); // add text information item as a String
 *
 *  // add children element information items
 *  gcl.add(ObjectFactory.createLetterBodyQuantity(new BigInteger("1")));
 *  gcl.add(ObjectFactory.createLetterBodyProductName("Baby Monitor"));
 *  gcl.add("shipped from our warehouse");  // add text information item
 * }
 *
 * <p>See "Package Specification" in jakarta.xml.bind.package javadoc for
 * additional common information.</p>
 * @author Kohsuke Kawaguchi
 * @since 1.6, JAXB 2.0
 */
@Retention(RUNTIME)
@Target({FIELD,METHOD})
public @interface XmlMixed {
}
