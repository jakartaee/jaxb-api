/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind;

import org.w3c.dom.Node;

import javax.xml.validation.Schema;

/**
 * Enable synchronization between XML infoset nodes and Jakarta XML Binding objects
 * representing same XML document.
 *
 * <p>
 * An instance of this class maintains the association between XML nodes of
 * an infoset preserving view and a Jakarta XML Binding representation of an XML document.
 * Navigation between the two views is provided by the methods
 * {@link #getXMLNode(Object)} and {@link #getJAXBNode(Object)}.
 *
 * <p>
 * Modifications can be made to either the infoset preserving view or the
 * Jakarta XML Binding representation of the document while the other view remains
 * unmodified. The binder is able to synchronize the changes made in the
 * modified view back into the other view using the appropriate
 * Binder update methods, {@link #updateXML(Object, Object)} or
 * {@link #updateJAXB(Object)}.
 *
 * <p>
 * A typical usage scenario is the following:
 * <ul>
 *   <li>load XML document into an XML infoset representation</li>
 *   <li>{@link #unmarshal(Object)} XML infoset view to Jakarta XML Binding view.
 *       (Note to conserve resources, it is possible to only unmarshal a
 *       subtree of the XML infoset view to the Jakarta XML Binding view.)</li>
 *   <li>application access/updates Jakarta XML Binding view of XML document.</li>
 *   <li>{@link #updateXML(Object)} synchronizes modifications to Jakarta XML Binding view
 *       back into the XML infoset view. Update operation preserves as
 *       much of original XML infoset as possible (i.e. comments, PI, ...)</li>
 * </ul>
 *
 * <p>
 * A Binder instance is created using the factory method
 * {@link JAXBContext#createBinder()} or {@link JAXBContext#createBinder(Class)}.
 *
 * @param <XmlNode> the template parameter, <code>XmlNode</code>, is the
 * root interface/class for the XML infoset preserving representation.
 * A Binder implementation is required to minimally support
 * an <code>XmlNode</code> value of <code>org.w3c.dom.Node.class</code>.
 * A Binder implementation can support alternative XML infoset
 * preserving representations.
 *
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 *     Joseph Fialli
 *
 * @since 1.6, JAXB 2.0
 */
public abstract class Binder<XmlNode> {

    /**
     * Do-nothing constructor for the derived classes.
     */
    protected Binder() {}

    /**
     * Unmarshal XML infoset view to a Jakarta XML Binding object tree.
     *
     * <p>
     * This method is similar to {@link Unmarshaller#unmarshal(Node)}
     * with the addition of maintaining the association between XML nodes
     * and the produced Jakarta XML Binding objects, enabling future update operations,
     * {@link #updateXML(Object, Object)} or {@link #updateJAXB(Object)}.
     *
     * <p>
     * When {@link #getSchema()} is non-null, <code>xmlNode</code>
     * and its descendants is validated during this operation.
     *
     * <p>
     * This method throws {@link UnmarshalException} when the Binder's
     * {@link JAXBContext} does not have a mapping for the XML element name
     * or the type, specifiable via {@code @xsi:type}, of {@code xmlNode}
     * to a Jakarta XML Binding mapped class. The method {@link #unmarshal(Object, Class)}
     * enables an application to specify the Jakarta XML Binding mapped class that
     * the {@code xmlNode} should be mapped to.
     *
     * @param xmlNode
     *      the document/element to unmarshal XML data from.
     *
     * @return
     *      the newly created root object of the Jakarta XML Binding object tree.
     *
     * @throws JAXBException
     *      If any unexpected errors occur while unmarshalling
     * @throws UnmarshalException
     *     If the {@link ValidationEventHandler ValidationEventHandler}
     *     returns false from its {@code handleEvent} method or the
     *     {@code Binder} is unable to perform the XML to Java
     *     binding.
     * @throws IllegalArgumentException
     *      If the node parameter is null
     */
    public abstract Object unmarshal( XmlNode xmlNode ) throws JAXBException;

    /**
     * Unmarshal XML root element by provided {@code declaredType}
     * to a Jakarta XML Binding object tree.
     *
     * <p>
     * Implements <a href="Unmarshaller.html#unmarshalByDeclaredType">Unmarshal by Declared Type</a>
     *
     * <p>
     * This method is similar to {@link Unmarshaller#unmarshal(Node, Class)}
     * with the addition of maintaining the association between XML nodes
     * and the produced Jakarta XML Binding objects, enabling future update operations,
     * {@link #updateXML(Object, Object)} or {@link #updateJAXB(Object)}.
     *
     * <p>
     * When {@link #getSchema()} is non-null, <code>xmlNode</code>
     * and its descendants is validated during this operation.
     *
     * @param xmlNode
     *      the document/element to unmarshal XML data from.
     * @param declaredType
     *      appropriate Jakarta XML Binding mapped class to hold {@code node}'s XML data.
     * @param <T> the declared type
     *
     * @return
     * <a href="JAXBElement.html">JAXBElement</a> representation
     * of {@code node}
     *
     * @throws JAXBException
     *      If any unexpected errors occur while unmarshalling
     * @throws UnmarshalException
     *     If the {@link ValidationEventHandler ValidationEventHandler}
     *     returns false from its {@code handleEvent} method or the
     *     {@code Binder} is unable to perform the XML to Java
     *     binding.
     * @throws IllegalArgumentException
     *      If any of the input parameters are null
     * @since 1.6, JAXB 2.0
     */
    public abstract <T> JAXBElement<T>
	unmarshal( XmlNode xmlNode, Class<T> declaredType )
	throws JAXBException;

    /**
     * Marshal a Jakarta XML Binding object tree to a new XML document.
     *
     * <p>
     * This method is similar to {@link Marshaller#marshal(Object, Node)}
     * with the addition of maintaining the association between Jakarta XML Binding objects
     * and the produced XML nodes,
     * enabling future update operations such as
     * {@link #updateXML(Object, Object)} or {@link #updateJAXB(Object)}.
     *
     * <p>
     * When {@link #getSchema()} is non-null, the marshalled
     * xml content is validated during this operation.
     *
     * @param jaxbObject
     *      The content tree to be marshalled.
     * @param xmlNode
     *      The parameter must be a Node that accepts children.
     *
     * @throws JAXBException
     *      If any unexpected problem occurs during the marshalling.
     * @throws MarshalException
     *      If the {@link ValidationEventHandler ValidationEventHandler}
     *      returns false from its {@code handleEvent} method or the
     *      {@code Binder} is unable to marshal {@code jaxbObject} (or any
     *      object reachable from {@code jaxbObject}).
     *
     * @throws IllegalArgumentException
     *      If any of the method parameters are null
     */
    public abstract void marshal( Object jaxbObject, XmlNode xmlNode ) throws JAXBException;

    /**
     * Gets the XML element associated with the given Jakarta XML Binding object.
     *
     * <p>
     * Once a Jakarta XML Binding object tree is associated with an XML fragment,
     * this method enables navigation between the two trees.
     *
     * <p>
     * An association between an XML element and a Jakarta XML Binding object is
     * established by the bind methods and the update methods.
     * Note that this association is partial; not all XML elements
     * have associated Jakarta XML Binding objects, and not all Jakarta XML Binding objects have
     * associated XML elements.
     *
     * @param jaxbObject An instance that is reachable from a prior
     *                   call to a bind or update method that returned
     *                   a Jakarta XML Binding object tree.
     *
     * @return
     *      null if the specified Jakarta XML Binding object is not known to this
     *      {@link Binder}, or if it is not associated with an
     *      XML element.
     *
     * @throws IllegalArgumentException
     *      If the jaxbObject parameter is null
     */
    public abstract XmlNode getXMLNode( Object jaxbObject );

    /**
     * Gets the Jakarta XML Binding object associated with the given XML element.
     *
     * <p>
     * Once a Jakarta XML Binding object tree is associated with an XML fragment,
     * this method enables navigation between the two trees.
     *
     * <p>
     * An association between an XML element and a Jakarta XML Binding object is
     * established by the unmarshal, marshal and update methods.
     * Note that this association is partial; not all XML elements
     * have associated Jakarta XML Binding objects, and not all Jakarta XML Binding objects have
     * associated XML elements.
     *
     * @param xmlNode the XML element
     *
     * @return
     *      null if the specified XML node is not known to this
     *      {@link Binder}, or if it is not associated with a
     *      Jakarta XML Binding object.
     *
     * @throws IllegalArgumentException
     *      If the node parameter is null
     */
    public abstract Object getJAXBNode( XmlNode xmlNode );

    /**
     * Takes an Jakarta XML Binding object and updates
     * its associated XML node and its descendants.
     *
     * <p>
     * This is a convenience method of:
     * <pre>
     * updateXML( jaxbObject, getXMLNode(jaxbObject));
     * </pre>
     *
     * @param jaxbObject the XML Binding object
     *
     * @return the XML node associated with XML Binding object
     *
     * @throws JAXBException
     *      If any unexpected problem occurs updating corresponding XML content.
     * @throws IllegalArgumentException
     *      If the jaxbObject parameter is null
     */
    public abstract XmlNode updateXML( Object jaxbObject ) throws JAXBException;

    /**
     * Changes in Jakarta XML Binding object tree are updated in its associated XML parse tree.
     *
     * <p>
     * This operation can be thought of as an "in-place" marshalling.
     * The difference is that instead of creating a whole new XML tree,
     * this operation updates an existing tree while trying to preserve
     * the XML as much as possible.
     *
     * <p>
     * For example, unknown elements/attributes in XML that were not bound
     * to Jakarta XML Binding will be left untouched (whereas a marshalling operation
     * would create a new tree that doesn't contain any of those.)
     *
     * <p>
     * As a side-effect, this operation updates the association between
     * XML nodes and Jakarta XML Binding objects.
     *
     * @param jaxbObject root of potentially modified Jakarta XML Binding object tree
     * @param xmlNode    root of update target XML parse tree
     *
     * @return
     *      Returns the updated XML node. Typically, this is the same
     *      node you passed in as <i>xmlNode</i>, but it maybe
     *      a different object, for example when the tag name of the object
     *      has changed.
     *
     * @throws JAXBException
     *      If any unexpected problem occurs updating corresponding XML content.
     * @throws IllegalArgumentException
     *      If any of the input parameters are null
     */
    public abstract XmlNode updateXML( Object jaxbObject, XmlNode xmlNode ) throws JAXBException;

    /**
     * Takes an XML node and updates its associated Jakarta XML Binding object and its descendants.
     *
     * <p>
     * This operation can be thought of as an "in-place" unmarshalling.
     * The difference is that instead of creating a whole new Jakarta XML Binding tree,
     * this operation updates an existing tree, reusing as much Jakarta XML Binding objects
     * as possible.
     *
     * <p>
     * As a side-effect, this operation updates the association between
     * XML nodes and Jakarta XML Binding objects.
     *
     * @param xmlNode the XML node
     *
     * @return
     *      Returns the updated Jakarta XML Binding object. Typically, this is the same
     *      object that was returned from earlier
     *      {@link #marshal(Object,Object)} or
     *      {@link #updateJAXB(Object)} method invocation,
     *      but it maybe
     *      a different object, for example when the name of the XML
     *      element has changed.
     *
     * @throws JAXBException
     *      If any unexpected problem occurs updating corresponding Jakarta XML Binding mapped content.
     * @throws IllegalArgumentException
     *      If node parameter is null
     */
    public abstract Object updateJAXB( XmlNode xmlNode ) throws JAXBException;


    /**
     * Specifies whether marshal, unmarshal and update methods
     * performs validation on their XML content.
     *
     * @param schema set to null to disable validation.
     *
     * @see Unmarshaller#setSchema(Schema)
     */
    public abstract void setSchema( Schema schema );

    /**
     * Gets the last {@link Schema} object (including null) set by the
     * {@link #setSchema(Schema)} method.
     *
     * @return the Schema object for validation or null if not present
     */
    public abstract Schema getSchema();

    /**
     * Allow an application to register a {@code ValidationEventHandler}.
     * <p>
     * The {@code ValidationEventHandler} will be called by the Jakarta XML Binding Provider
     * if any validation errors are encountered during calls to any of the
     * Binder unmarshal, marshal and update methods.
     *
     * <p>
     * Calling this method with a null parameter will cause the Binder
     * to revert back to the default default event handler.
     *
     * @param handler the validation event handler
     * @throws JAXBException if an error was encountered while setting the
     *         event handler
     */
    public abstract void setEventHandler( ValidationEventHandler handler ) throws JAXBException;

    /**
     * Return the current event handler or the default event handler if one
     * hasn't been set.
     *
     * @return the current ValidationEventHandler or the default event handler
     *         if it hasn't been set
     * @throws JAXBException if an error was encountered while getting the
     *         current event handler
     */
    public abstract ValidationEventHandler getEventHandler() throws JAXBException;

    /**
     *
     * Set the particular property in the underlying implementation of
     * {@code Binder}.  This method can only be used to set one of
     * the standard Jakarta XML Binding defined unmarshal/marshal properties
     * or a provider specific property for binder, unmarshal or marshal.
     * Attempting to set an undefined property will result in
     * a PropertyException being thrown.  See
     * <a href="Unmarshaller.html#supportedProps">Supported Unmarshal Properties</a>
     * and
     * <a href="Marshaller.html#supportedProps">Supported Marshal Properties</a>.
     *
     * @param name the name of the property to be set. This value can either
     *              be specified using one of the constant fields or a user
     *              supplied string.
     * @param value the value of the property to be set
     *
     * @throws PropertyException when there is an error processing the given
     *                            property or value
     * @throws IllegalArgumentException
     *      If the name parameter is null
     */
    abstract public void setProperty( String name, Object value ) throws PropertyException;


    /**
     * Get the particular property in the underlying implementation of
     * {@code Binder}.  This method can only
     * be used to get one of
     * the standard Jakarta XML Binding defined unmarshal/marshal properties
     * or a provider specific property for binder, unmarshal or marshal.
     * Attempting to get an undefined property will result in
     * a PropertyException being thrown.  See
     * <a href="Unmarshaller.html#supportedProps">Supported Unmarshal Properties</a>
     * and
     * <a href="Marshaller.html#supportedProps">Supported Marshal Properties</a>.
     *
     * @param name the name of the property to retrieve
     * @return the value of the requested property
     *
     * @throws PropertyException
     *      when there is an error retrieving the given property or value
     *      property name
     * @throws IllegalArgumentException
     *      If the name parameter is null
     */
    abstract public Object getProperty( String name ) throws PropertyException;

}
