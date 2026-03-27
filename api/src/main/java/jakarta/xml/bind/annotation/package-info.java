/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 2004, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/**
 * Defines annotations for customizing Java program elements to XML Schema mapping.
 * <p>
 * References in this document to JAXB refer to the Jakarta XML Binding unless otherwise noted.
 *
 * <h2>Package Specification</h2>
 * <p>The following table shows the Jakarta XML Binding mapping annotations
 * that can be associated with each program element. </p>
 * <table class="striped">
 *   <caption>Annotations for customizing Java program elements to XML Schema mapping</caption>
 *   <thead>
 *     <tr>
 *       <th scope="col">Program Element</th>
 *       <th scope="col">Jakarta XML Binding annotation</th>
 *     </tr>
 *   </thead>
 *   <tbody style="text-align:left">
 *     <tr>
 *       <th scope="row" style="vertical-align:top">Package</th>
 *       <td>
 *         <ul style="list-style-type:none">
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlAccessorOrder}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlAccessorType}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlSchema}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlSchemaType}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlSchemaTypes}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapters}</li>
 *         </ul>
 *       </td>
 *     </tr>
 *     <tr>
 *       <th scope="row" style="vertical-align:top">Class</th>
 *       <td>
 *         <ul style="list-style-type:none">
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlAccessorOrder}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlAccessorType}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlInlineBinaryData}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlRootElement}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlType}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter}</li>
 *         </ul>
 *       </td>
 *     </tr>
 *     <tr>
 *       <th scope="row" style="vertical-align:top">Enum type</th>
 *       <td>
 *         <ul style="list-style-type:none">
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlEnum}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlEnumValue} (enum constant only)</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlRootElement}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlType}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter}</li>
 *         </ul>
 *       </td>
 *     </tr>
 *     <tr>
 *       <th scope="row" style="vertical-align:top">JavaBean Property/field</th>
 *       <td>
 *         <ul style="list-style-type:none">
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlElement}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlElements}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlElementRef}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlElementRefs}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlElementWrapper}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlAnyElement}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlAttribute}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlAnyAttribute}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlTransient}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlValue}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlID}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlIDREF}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlList}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlMixed}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlMimeType}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlAttachmentRef}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlInlineBinaryData}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlElementDecl} (only on method)</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter}</li>
 *         </ul>
 *       </td>
 *     </tr>
 *     <tr>
 *       <th scope="row" style="vertical-align:top">Parameter</th>
 *       <td>
 *         <ul style="list-style-type:none">
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlList}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlAttachmentRef}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.XmlMimeType}</li>
 *             <li>{@linkplain jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter}</li>
 *         </ul>
 *       </td>
 *     </tr>
 * </tbody>
 * </table>
 * <h3>Terminology</h3>
 * <p>
 * <b>JavaBean property and field:</b> For the purposes of
 * mapping, there is no semantic difference between a field and
 * a JavaBean property. Thus, an annotation that can be applied
 * to a JavaBean property can always be applied to a
 * field. Hence, in the Javadoc documentation, for brevity, the
 * term JavaBean property or property is used to mean either JavaBean
 * property or a field. Where required, both are explicitly
 * mentioned.
 * <p>
 * <b>top level class:</b> For the purpose of mapping, there is
 * no semantic difference between a top level class and a
 * static nested class. Thus, an annotation that can be applied
 * to a top level class, can always be applied to a nested
 * static class. Hence, in the Javadoc documentation, for
 * brevity, the term "top level class" or just class is used to
 * mean either a top level class or a nested static
 * class.
 * <p>
 * <b>mapping annotation:</b>A Jakarta XML Binding defined program
 * annotation based on the JSR 175 programming annotation
 * facility.
 * <h3>Common Usage Constraints</h3>
 * <p>The following usage constraints are defined here since
 * they apply to more than annotation:
 * <ul>
 * <li> For a property, a given annotation can be applied to
 * either read or write property but not both. </li>
 * <li> A property name must be different from any other
 * property name in any of the super classes of the
 * class being mapped. </li>
 * <li> A mapped field name or the decapitalized name of a
 * mapped property must be unique within a class. </li>
 * </ul>
 * <h3>Notations</h3>
 * <b>Namespace prefixes</b>
 * <p>The following namespace prefixes are used in the XML Schema
 * fragments in this package.
 * <table class="striped">
 *   <caption>XML Schema fragments namespace prefixes</caption>
 *   <thead>
 *     <tr>
 *       <th scope="col">Prefix</th>
 *       <th scope="col">Namespace</th>
 *       <th scope="col">Notes</th>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <th scope="row">xs</th>
 *       <td>http://www.w3.org/2001/XMLSchema</td>
 *       <td>Namespace of XML Schema namespace</td>
 *     </tr>
 *     <tr>
 *       <th scope="row">ref</th>
 *       <td>http://ws-i.org/profiles/basic/1.1/xsd</td>
 *       <td>Namespace for swaref schema component</td>
 *     </tr>
 *     <tr>
 *       <th scope="row">xsi</th>
 *       <td>http://www.w3.org/2001/XMLSchema-instance</td>
 *       <td>XML Schema namespace for instances</td>
 *     </tr>
 *   </tbody>
 * </table>
 *
 * @since 1.6, JAXB 2.0
 */
package jakarta.xml.bind.annotation;
