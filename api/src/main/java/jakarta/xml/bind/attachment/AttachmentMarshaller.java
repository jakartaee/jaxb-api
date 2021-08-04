/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind.attachment;

import jakarta.activation.DataHandler;
import jakarta.xml.bind.Marshaller;

/**
 * <p>Enable Jakarta XML Binding marshalling to optimize storage of binary data.
 *
 * <p>This API enables an efficient cooperative creation of optimized
 * binary data formats between a Jakarta XML Binding marshalling process and a MIME-based package
 * processor. A Jakarta XML Binding implementation marshals the root body of a MIME-based package,
 * delegating the creation of referenceable MIME parts to
 * the MIME-based package processor that implements this abstraction.
 *
 * <p>XOP processing is enabled when {@link #isXOPPackage()} is true.
 *    See {@link #addMtomAttachment(DataHandler, String, String)} for details.
 *
 *
 * <p>WS-I Attachment Profile 1.0 is supported by
 * {@link #addSwaRefAttachment(DataHandler)} being called by the
 * marshaller for each Jakarta XML Binding property related to
 * {http://ws-i.org/profiles/basic/1.1/xsd}swaRef.
 *
 *
 * @author Marc Hadley
 * @author Kohsuke Kawaguchi
 * @author Joseph Fialli
 * @since 1.6, JAXB 2.0
 *
 * @see Marshaller#setAttachmentMarshaller(AttachmentMarshaller)
 *
 * @see <a href="http://www.w3.org/TR/2005/REC-xop10-20050125/">XML-binary Optimized Packaging</a>
 * @see <a href="http://www.ws-i.org/Profiles/AttachmentsProfile-1.0-2004-08-24.html">WS-I Attachments Profile Version 1.0.</a>
 */
public abstract class AttachmentMarshaller {

    /**
     * Do-nothing constructor for the derived classes.
     */
    protected AttachmentMarshaller() {}

    /**
     * <p>Consider MIME content {@code data} for optimized binary storage as an attachment.
     *
     * <p>
     * This method is called by Jakarta XML Binding marshal process when {@link #isXOPPackage()} is
     * {@code true}, for each element whose datatype is "base64Binary", as described in
     * Step 3 in
     * <a href="http://www.w3.org/TR/2005/REC-xop10-20050125/#creating_xop_packages">Creating XOP Packages</a>.
     *
     * <p>
     * The method implementor determines whether {@code data} shall be attached separately
     * or inlined as base64Binary data. If the implementation chooses to optimize the storage
     * of the binary data as a MIME part, it is responsible for attaching {@code data} to the
     * MIME-based package, and then assigning an unique content-id, cid, that identifies
     * the MIME part within the MIME message. This method returns the cid,
     * which enables the Jakarta XML Binding marshaller to marshal a XOP element that refers to that cid in place
     * of marshalling the binary data. When the method returns null, the Jakarta XML Binding marshaller
     * inlines {@code data} as base64binary data.
     *
     * <p>
     * The caller of this method is required to meet the following constraint.
     * If the element infoset item containing {@code data} has the attribute
     * {@code xmime:contentType} or if the Jakarta XML Binding property/field representing
     * {@code data} is annotated with a known MIME type,
     * {@code data.getContentType()} should be set to that MIME type.
     *
     * <p>
     * The {@code elementNamespace} and {@code elementLocalName}
     * parameters provide the
     * context that contains the binary data. This information could
     * be used by the MIME-based package processor to determine if the
     * binary data should be inlined or optimized as an attachment.
     *
     * @param data
     *       represents the data to be attached. Must be non-null.
     * @param elementNamespace
     *      the namespace URI of the element that encloses the base64Binary data.
     *      Can be empty but never null.
     * @param elementLocalName
     *      The local name of the element. Always a non-null valid string.
     *
     * @return
     *     a valid content-id URI (see <a href="http://www.w3.org/TR/xop10/#RFC2387">RFC 2387</a>) that identifies the attachment containing {@code data}.
     *     Otherwise, null if the attachment was not added and should instead be inlined in the message.
     *
     * @see <a href="http://www.w3.org/TR/2005/REC-xop10-20050125/">XML-binary Optimized Packaging</a>
     * @see <a href="http://www.w3.org/TR/xml-media-types/">Describing Media Content of Binary Data in XML</a>
     */
    public abstract String addMtomAttachment(DataHandler data, String elementNamespace, String elementLocalName);

    /**
     * <p>Consider binary {@code data} for optimized binary storage as an attachment.
     *
     * <p>Since content type is not known, the attachment's MIME content type must be set to "application/octet-stream".
     *
     * <p>
     * The {@code elementNamespace} and {@code elementLocalName}
     * parameters provide the
     * context that contains the binary data. This information could
     * be used by the MIME-based package processor to determine if the
     * binary data should be inlined or optimized as an attachment.
     *
     * @param data
     *      represents the data to be attached. Must be non-null. The actual data region is
     *      specified by {@code (data,offset,length)} tuple.
     *
     * @param offset
     *       The offset within the array of the first byte to be read;
     *       must be non-negative and no larger than array.length
     *
     * @param length
     *       The number of bytes to be read from the given array;
     *       must be non-negative and no larger than array.length
     *
     * @param mimeType
     *      If the data has an associated MIME type known to Jakarta XML Binding, that is passed
     *      as this parameter. If none is known, "application/octet-stream".
     *      This parameter may never be null.
     *
     * @param elementNamespace
     *      the namespace URI of the element that encloses the base64Binary data.
     *      Can be empty but never null.
     *
     * @param elementLocalName
     *      The local name of the element. Always a non-null valid string.
     *
     * @return content-id URI, cid, to the attachment containing
     *         {@code data} or null if data should be inlined.
     *
     * @see #addMtomAttachment(DataHandler, String, String)
     */
    public abstract String addMtomAttachment(byte[] data, int offset, int length, String mimeType, String elementNamespace, String elementLocalName);

    /**
     * <p>Read-only property that returns true if Jakarta XML Binding marshaller should enable XOP creation.
     *
     * <p>This value must not change during the marshalling process. When this
     * value is true, the {@code addMtomAttachment(...)} method
     * is invoked when the appropriate binary datatypes are encountered by
     * the marshal process.
     *
     * <p>Marshaller.marshal() must throw IllegalStateException if this value is {@code true}
     * and the XML content to be marshalled violates Step 1 in
     * <a href="http://www.w3.org/TR/2005/REC-xop10-20050125/#creating_xop_packages">Creating XOP Pacakges</a>
     * http://www.w3.org/TR/2005/REC-xop10-20050125/#creating_xop_packages.
     * <i>"Ensure the Original XML Infoset contains no element information item with a
     * [namespace name] of "http://www.w3.org/2004/08/xop/include" and a [local name] of Include"</i>
     *
     * <p>When this method returns true and during the marshal process
     * at least one call to {@code addMtomAttachment(...)} returns
     * a content-id, the MIME-based package processor must label the
     * root part with the application/xop+xml media type as described in
     * Step 5 of
     * <a href="http://www.w3.org/TR/2005/REC-xop10-20050125/#creating_xop_packages">Creating XOP Pacakges</a>.
     *
     * @return true when MIME context is a XOP Package.
     */
    public boolean isXOPPackage() { return false; }

   /**
    * <p>Add MIME {@code data} as an attachment and return attachment's content-id, cid.
    *
    * <p>
    * This method is called by Jakarta XML Binding marshal process for each element/attribute typed as
    * {http://ws-i.org/profiles/basic/1.1/xsd}swaRef. The MIME-based package processor
    * implementing this method is responsible for attaching the specified data to a
    * MIME attachment, and generating a content-id, cid, that uniquely identifies the attachment
    * within the MIME-based package.
    *
    * <p>Caller inserts the returned content-id, cid, into the XML content being marshalled.
    *
    * @param data
    *       represents the data to be attached. Must be non-null.
    * @return
    *       must be a valid URI used as cid. Must satisfy Conformance Requirement R2928 from
    *       <a href="http://www.ws-i.org/Profiles/AttachmentsProfile-1.0-2004-08-24.html#Referencing_Attachments_from_the_SOAP_Envelope">WS-I Attachments Profile Version 1.0.</a>
    */
    public abstract String addSwaRefAttachment(DataHandler data);
}
