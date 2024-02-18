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

import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

/**
 * <p>
 * Maps a JavaBean property to a XML element derived from property's type.
 * <p>
 * <b>Usage</b>
 * <p>
 * {@code @XmlElementRef} annotation can be used with a
 * JavaBean property or from within {@link XmlElementRefs}
 * <p>
 * This annotation dynamically associates an XML element name with the JavaBean
 * property. When a JavaBean property is annotated with {@link
 * XmlElement}, the XML element name is statically derived from the
 * JavaBean property name. However, when this annotation is used, the
 * XML element name is derived from the instance of the type of the
 * JavaBean property at runtime.
 *
 * <h2> XML Schema substitution group support </h2>
 * XML Schema allows a XML document author to use XML element names
 * that were not statically specified in the content model of a
 * schema using substitution groups. Schema derived code provides
 * support for substitution groups using an <i>element property</i>,
 * (section 5.5.5, "Element Property" of Jakarta XML Binding specification). An
 * element property method signature is of the form:
 * {@snippet :
 *  public void setTerm(JAXBElement<? extends Operator>);
 *  public JAXBElement<? extends Operator> getTerm();
 * }
 * <p>
 * An element factory method annotated with  {@link XmlElementDecl} is
 * used to create a {@code JAXBElement} instance, containing an XML
 * element name. The presence of {@code @XmlElementRef} annotation on an
 * element property indicates that the element name from {@code JAXBElement}
 * instance be used instead of deriving an XML element name from the
 * JavaBean property name.
 *
 * <p>
 * The usage is subject to the following constraints:
 * <ul>
 *   <li> If the collection item type (for collection property) or
 *        property type (for single valued property) is
 *        {@link jakarta.xml.bind.JAXBElement}, then
 *        {@code @XmlElementRef.name()} and {@code @XmlElementRef.namespace()} must
 *        point an element factory method  with an @XmlElementDecl
 *        annotation in a class annotated  with @XmlRegistry (usually
 *        ObjectFactory class generated by  the schema compiler) :
 *        <ul>
 *            <li> @XmlElementDecl.name() must equal @XmlElementRef.name()  </li>
 *            <li> @XmlElementDecl.namespace() must equal @XmlElementRef.namespace(). </li>
 *        </ul>
 *   </li>
 *   <li> If the collection item type (for collection property) or
 *        property type  (for single valued property) is not
 *        {@link jakarta.xml.bind.JAXBElement}, then the type referenced by the
 *        property or field must be annotated  with {@link XmlRootElement}. </li>
 *   <li> This annotation can be used with the following annotations:
 *        {@link XmlElementWrapper}, {@link XmlJavaTypeAdapter}.
 *   </ul>
 *
 * <p>See "Package Specification" in jakarta.xml.bind.package javadoc for
 * additional common information.</p>
 *
 * <p><b>Example 1: Ant Task Example</b></p>
 * The following Java class hierarchy models an Ant build
 * script.  An Ant task corresponds to a class in the class
 * hierarchy. The XML element name of an Ant task is indicated by the
 * XmlRootElement annotation on its corresponding class.
 * {@snippet :
 *  @XmlRootElement(name="target")
 *  class Target {
 *      // The presence of @XmlElementRef indicates that the XML
 *      // element name will be derived from the @XmlRootElement
 *      // annotation on the type (for e.g. "jar" for JarTask).
 *      @XmlElementRef
 *      List<Task> tasks;
 *  }
 *
 *  abstract class Task {
 *  }
 *
 *  @XmlRootElement(name="jar")
 *  class JarTask extends Task {
 *      ...
 *  }
 *
 *  @XmlRootElement(name="javac")
 *  class JavacTask extends Task {
 *      ...
 *  }
 * }
 * {@snippet lang="XML" :
 *  <!-- XML Schema fragment -->
 *  <xs:element name="target" type="Target">
 *    <xs:complexType name="Target">
 *      <xs:sequence>
 *        <xs:choice maxOccurs="unbounded">
 *          <xs:element ref="jar"/>
 *          <xs:element ref="javac"/>
 *        </xs:choice>
 *      </xs:sequence>
 *    </xs:complexType>
 *  </xs:element>
 * }
 * <p>
 * Thus the following code fragment:
 * {@snippet :
 *  Target target = new Target();
 *  target.tasks.add(new JarTask());
 *  target.tasks.add(new JavacTask());
 *  marshal(target);
 * }
 * will produce the following XML output:
 * {@snippet lang="XML" :
 *  <target>
 *    <jar>
 *      ....
 *    </jar>
 *    <javac>
 *      ....
 *    </javac>
 *  </target>
 * }
 * <p>
 * It is not an error to have a class that extends {@code Task}
 * that doesn't have {@link XmlRootElement}. But they can't show up in an
 * XML instance (because they don't have XML element names).
 *
 * <p><b>Example 2: XML Schema Susbstitution group support</b>
 * <p> The following example shows the annotations for XML Schema
 * substitution groups.  The annotations and the ObjectFactory are
 * derived from the schema.
 *
 * {@snippet :
 *  @XmlElement
 *  class Math {
 *      //  The value of type() is // @link substring="type()" target="#type()"
 *      //  JAXBElement.class , which indicates the XML
 *      //  element name ObjectFactory - in general a class marked
 *      //  with @XmlRegistry. (See ObjectFactory below)
 *      //
 *      //  The name() is "operator", a pointer to a // @link substring="name()" target="#name()"
 *      // factory method annotated with a
 *      //  XmlElementDecl with the name "operator". Since //@link substring="XmlElementDecl" target="XmlElementDecl"
 *      //  "operator" is the head of a substitution group that
 *      //  contains elements "add" and "sub" elements, "operator"
 *      //  element can be substituted in an instance document by
 *      //  elements "add" or "sub". At runtime, JAXBElement
 *      //  instance contains the element name that has been
 *      //  substituted in the XML document.
 *      //
 *      @XmlElementRef(type=JAXBElement.class,name="operator")
 *      JAXBElement<? extends Operator> term;
 *  }
 *
 *  @XmlRegistry
 *  class ObjectFactory {
 *      @XmlElementDecl(name="operator")
 *      JAXBElement<Operator> createOperator(Operator o) {...}
 *      @XmlElementDecl(name="add",substitutionHeadName="operator")
 *      JAXBElement<Operator> createAdd(Operator o) {...}
 *      @XmlElementDecl(name="sub",substitutionHeadName="operator")
 *      JAXBElement<Operator> createSub(Operator o) {...}
 *  }
 *
 *  class Operator {
 *      ...
 *  }
 * }
 * <p>
 * Thus, the following code fragment
 * {@snippet :
 *  Math m = new Math();
 *  m.term = new ObjectFactory().createAdd(new Operator());
 *  marshal(m);
 * }
 * will produce the following XML output:
 * {@snippet lang="XML" :
 *  <math>
 *    <add>...</add>
 *  </math>
 * }
 *
 *
 * @author <ul><li>Kohsuke Kawaguchi, Sun Microsystems,Inc. </li><li>Sekhar Vajjhala, Sun Microsystems, Inc.</li></ul>
 * @see XmlElementRefs
 * @since 1.6, JAXB 2.0
 */
@Retention(RUNTIME)
@Target({FIELD,METHOD})
public @interface XmlElementRef {
    /**
     * The Java type being referenced.
     * <p>
     * If the value is DEFAULT.class, the type is inferred from the
     * the type of the JavaBean property.
     */
    Class<?> type() default DEFAULT.class;

    /**
     * This parameter and {@link #name()} are used to determine the
     * XML element for the JavaBean property.
     *
     * <p> If {@code type()} is {@code JAXBElement.class} , then
     * {@code namespace()} and {@code name()}
     * point to a factory method with {@link XmlElementDecl}. The XML
     * element name is the element name from the factory method's
     * {@link XmlElementDecl} annotation or if an element from its
     * substitution group (of which it is a head element) has been
     * substituted in the XML document, then the element name is from the
     * {@link XmlElementDecl} on the substituted element.
     *
     * <p> If {@link #type()} is not {@code JAXBElement.class}, then
     * the XML element name is the XML element name statically
     * associated with the type using the annotation {@link
     * XmlRootElement} on the type. If the type is not annotated with
     * an {@link XmlElementDecl}, then it is an error.
     *
     * <p> If {@code type()} is not {@code JAXBElement.class}, then
     * this value must be "".
     *
     */
    String namespace() default "";
    /**
     *
     * @see #namespace()
     */
    String name() default "##default";

    /**
     * Used in {@link XmlElementRef#type()} to
     * signal that the type be inferred from the signature
     * of the property.
     */
    final class DEFAULT {
        private DEFAULT() {}
    }

    /**
     * Customize the element declaration to be required.
     * <p>
     * If required() is true, then Javabean property is mapped to
     * an XML schema element declaration with minOccurs="1".
     * maxOccurs is "1" for a single valued property and "unbounded"
     * for a multivalued property.
     *
     * <p>
     * If required() is false, then the Javabean property is mapped
     * to XML Schema element declaration with minOccurs="0".
     * maxOccurs is "1" for a single valued property and "unbounded"
     * for a multivalued property.
     *
     * <p>
     * For compatibility with Jakarta XML Binding, this property defaults to {@code true},
     * despite the fact that {@link XmlElement#required()} defaults to false.
     *
     * @since 1.7, JAXB 2.2
     */
    boolean required() default true;
}
