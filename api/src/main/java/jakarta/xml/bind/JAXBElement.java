/*
 * Copyright (c) 2004, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind;

import javax.xml.namespace.QName;
import java.io.Serializable;

/**
 * <p>Jakarta XML Binding representation of an Xml Element.</p>
 *
 * <p>This class represents information about an Xml Element from both the element
 * declaration within a schema and the element instance value within an xml document
 * with the following properties
 * <ul>
 *   <li>element's xml tag <b>{@code name}</b></li>
 *   <li><b>{@code value}</b> represents the element instance's attribute(s) and content model</li>
 *   <li>element declaration's <b>{@code declaredType}</b> ({@code xs:element @type} attribute)</li>
 *   <li><b>{@code scope}</b> of element declaration</li>
 *   <li>boolean <b>{@code nil}</b> property. (element instance's <b>{@code xsi:nil}</b> attribute)</li>
 * </ul>
 *
 * <p>The {@code declaredType} and {@code scope} property are the
 * Jakarta XML Binding class binding for the xml type definition.
 * </p>
 *
 * <p><b>{@code Scope}</b> is either {@link GlobalScope} or the Java class representing the
 * complex type definition containing the schema element declaration.
 * </p>
 *
 * <p>There is a property constraint that if <b>{@code value}</b> is {@code null},
 * then {@code nil} must be {@code true}. The converse is not true to enable
 * representing a nil element with attribute(s). If {@code nil} is true, it is possible
 * that {@code value} is non-null so it can hold the value of the attributes
 * associated with a nil element.
 * </p>
 *
 * @author Kohsuke Kawaguchi, Joe Fialli
 * @since 1.6, JAXB 2.0
 */

public class JAXBElement<T> implements Serializable {

    /** xml element tag name */
    final protected QName name;

    /** Java datatype binding for xml element declaration's type. */
    final protected Class<T> declaredType;

    /** Scope of xml element declaration representing this xml element instance.
     *  Can be one of the following values:
     *  - {@link GlobalScope} for global xml element declaration.
     *  - local element declaration has a scope set to the Java class
     *     representation of complex type defintion containing
     *     xml element declaration.
     */
    final protected Class<?> scope;

    /** xml element value.
        Represents content model and attributes of an xml element instance. */
    protected T value;

    /** true iff the xml element instance has xsi:nil="true". */
    protected boolean nil = false;

    /**
     * Designates global scope for an xml element.
     */
    public static final class GlobalScope {
        private GlobalScope() {}
    }

    /**
     * <p>Construct an xml element instance.</p>
     *
     * @param name          Java binding of xml element tag name
     * @param declaredType  Java binding of xml element declaration's type
     * @param scope
     *      Java binding of scope of xml element declaration.
     *      Passing null is the same as passing {@code GlobalScope.class}
     * @param value
     *      Java instance representing xml element's value.
     * @see #getScope()
     * @see #isTypeSubstituted()
     */
    public JAXBElement(QName name,
		       Class<T> declaredType,
		       Class<?> scope,
		       T value) {
        if(declaredType==null || name==null)
            throw new IllegalArgumentException();
        this.declaredType = declaredType;
        if(scope==null)     scope = GlobalScope.class;
        this.scope = scope;
        this.name = name;
        setValue(value);
    }

    /**
     * Construct an xml element instance.
     *
     * This is just a convenience method for {@code new JAXBElement(name,declaredType,GlobalScope.class,value)}
     */
    public JAXBElement(QName name, Class<T> declaredType, T value ) {
        this(name,declaredType,GlobalScope.class,value);
    }

    /**
     * Returns the Java binding of the xml element declaration's type attribute.
     */
    public Class<T> getDeclaredType() {
        return declaredType;
    }

    /**
     * Returns the xml element tag name.
     */
    public QName getName() {
        return name;
    }

    /**
     * <p>Set the content model and attributes of this xml element.</p>
     *
     * <p>When this property is set to {@code null}, {@code isNil()} must by {@code true}.
     *    Details of constraint are described at {@link #isNil()}.</p>
     *
     * @see #isTypeSubstituted()
     */
    public void setValue(T t) {
        this.value = t;
    }

    /**
     * <p>Return the content model and attribute values for this element.</p>
     *
     * <p>See {@link #isNil()} for a description of a property constraint when
     * this value is {@code null}</p>
     */
    public T getValue() {
        return value;
    }

    /**
     * Returns scope of xml element declaration.
     *
     * @see #isGlobalScope()
     * @return {@code GlobalScope.class} if this element is of global scope.
     */
    public Class<?> getScope() {
        return scope;
    }

    /**
     * <p>Returns {@code true} iff this element instance content model
     * is nil.</p>
     *
     * <p>This property always returns {@code true} when {@link #getValue()} is null.
     * Note that the converse is not true, when this property is {@code true},
     * {@link #getValue()} can contain a non-null value for attribute(s). It is
     * valid for a nil xml element to have attribute(s).</p>
     */
    public boolean isNil() {
        return (value == null) || nil;
    }

    /**
     * <p>Set whether this element has nil content.</p>
     *
     * @see #isNil()
     */
    public void setNil(boolean value) {
        this.nil = value;
    }

    /* Convenience methods
     * (Not necessary but they do unambiguously conceptualize
     *  the rationale behind this class' fields.)
     */

    /**
     * Returns true iff this xml element declaration is global.
     */
    public boolean isGlobalScope() {
        return this.scope == GlobalScope.class;
    }

    /**
     * Returns true iff this xml element instance's value has a different
     * type than xml element declaration's declared type.
     */
    public boolean isTypeSubstituted() {
        if(value==null)     return false;
        return value.getClass() != declaredType;
    }

    private static final long serialVersionUID = 1L;
}
