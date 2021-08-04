/*
 * Copyright (c) 2006, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind;

import jakarta.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Class that defines convenience methods for common, simple use of Jakarta XML Binding.
 *
 * <p>
 * Methods defined in this class are convenience methods that combine several basic operations
 * in the {@link JAXBContext}, {@link Unmarshaller}, and {@link Marshaller}.
 *
 * They are designed
 * to be the prefered methods for developers new to Jakarta XML Binding. They have
 * the following characterstics:
 *
 * <ol>
 *  <li>Generally speaking, the performance is not necessarily optimal.
 *      It is expected that people who need to write performance
 *      critical code will use the rest of the Jakarta XML Binding API directly.
 *  <li>Errors that happen during the processing is wrapped into
 *      {@link DataBindingException} (which will have {@link JAXBException}
 *      as its {@link Throwable#getCause() cause}. It is expected that
 *      people who prefer the checked exception would use
 *      the rest of the Jakarta XML Binding API directly.
 * </ol>
 *
 * <p>
 * In addition, the {@code unmarshal} methods have the following characteristic:
 *
 * <ol>
 *  <li>Schema validation is not performed on the input XML.
 *      The processing will try to continue even if there
 *      are errors in the XML, as much as possible. Only as
 *      the last resort, this method fails with {@link DataBindingException}.
 * </ol>
 *
 * <p>
 * Similarly, the {@code marshal} methods have the following characteristic:
 * <ol>
 *  <li>The processing will try to continue even if the Java object tree
 *      does not meet the validity requirement. Only as
 *      the last resort, this method fails with {@link DataBindingException}.
 * </ol>
 *
 *
 * <p>
 * All the methods on this class require non-null arguments to all parameters.
 * The {@code unmarshal} methods either fail with an exception or return
 * a non-null value.
 *
 * @author Kohsuke Kawaguchi
 * @since 1.6, JAXB 2.1
 */
public final class JAXB {
    /**
     * No instantiation is allowed.
     */
    private JAXB() {}

    /**
     * To improve the performance, we'll cache the last {@link JAXBContext} used.
     */
    private static final class Cache {
        final Class<?> type;
        final JAXBContext context;

        public Cache(Class<?> type) throws JAXBException {
            this.type = type;
            this.context = JAXBContext.newInstance(type);
        }
    }

    /**
     * Cache. We don't want to prevent the {@link Cache#type} from GC-ed,
     * hence {@link WeakReference}.
     */
    private static volatile WeakReference<Cache> cache;

    /**
     * Obtains the {@link JAXBContext} from the given type,
     * by using the cache if possible.
     *
     * <p>
     * We don't use locks to control access to {@link #cache}, but this code
     * should be thread-safe thanks to the immutable {@link Cache} and {@code volatile}.
     */
    private static <T> JAXBContext getContext(Class<T> type) throws JAXBException {
        WeakReference<Cache> c = cache;
        if(c!=null) {
            Cache d = c.get();
            if(d!=null && d.type==type)
                return d.context;
        }

        // overwrite the cache
        Cache d = new Cache(type);
        cache = new WeakReference<>(d);

        return d.context;
    }

    /**
     * Reads in a Java object tree from the given XML input.
     *
     * @param xml
     *      Reads the entire file as XML.
     */
    public static <T> T unmarshal( File xml, Class<T> type ) {
        try {
            JAXBElement<T> item = getContext(type).createUnmarshaller().unmarshal(new StreamSource(xml), type);
            return item.getValue();
        } catch (JAXBException e) {
            throw new DataBindingException(e);
        }
    }

    /**
     * Reads in a Java object tree from the given XML input.
     *
     * @param xml
     *      The resource pointed by the URL is read in its entirety.
     */
    public static <T> T unmarshal( URL xml, Class<T> type ) {
        try {
            JAXBElement<T> item = getContext(type).createUnmarshaller().unmarshal(toSource(xml), type);
            return item.getValue();
        } catch (JAXBException | IOException e) {
            throw new DataBindingException(e);
        }
    }

    /**
     * Reads in a Java object tree from the given XML input.
     *
     * @param xml
     *      The URI is {@link URI#toURL() turned into URL} and then
     *      follows the handling of {@code URL}.
     */
    public static <T> T unmarshal( URI xml, Class<T> type ) {
        try {
            JAXBElement<T> item = getContext(type).createUnmarshaller().unmarshal(toSource(xml), type);
            return item.getValue();
        } catch (JAXBException | IOException e) {
            throw new DataBindingException(e);
        }
    }

    /**
     * Reads in a Java object tree from the given XML input.
     *
     * @param xml
     *      The string is first interpreted as an absolute {@code URI}.
     *      If it's not {@link URI#isAbsolute() a valid absolute URI},
     *      then it's interpreted as a {@code File}
     */
    public static <T> T unmarshal( String xml, Class<T> type ) {
        try {
            JAXBElement<T> item = getContext(type).createUnmarshaller().unmarshal(toSource(xml), type);
            return item.getValue();
        } catch (JAXBException | IOException e) {
            throw new DataBindingException(e);
        }
    }

    /**
     * Reads in a Java object tree from the given XML input.
     *
     * @param xml
     *      The entire stream is read as an XML infoset.
     *      Upon a successful completion, the stream will be closed by this method.
     */
    public static <T> T unmarshal( InputStream xml, Class<T> type ) {
        try {
            JAXBElement<T> item = getContext(type).createUnmarshaller().unmarshal(toSource(xml), type);
            return item.getValue();
        } catch (JAXBException | IOException e) {
            throw new DataBindingException(e);
        }
    }

    /**
     * Reads in a Java object tree from the given XML input.
     *
     * @param xml
     *      The character stream is read as an XML infoset.
     *      The encoding declaration in the XML will be ignored.
     *      Upon a successful completion, the stream will be closed by this method.
     */
    public static <T> T unmarshal( Reader xml, Class<T> type ) {
        try {
            JAXBElement<T> item = getContext(type).createUnmarshaller().unmarshal(toSource(xml), type);
            return item.getValue();
        } catch (JAXBException | IOException e) {
            throw new DataBindingException(e);
        }
    }

    /**
     * Reads in a Java object tree from the given XML input.
     *
     * @param xml
     *      The XML infoset that the {@link Source} represents is read.
     */
    public static <T> T unmarshal( Source xml, Class<T> type ) {
        try {
            JAXBElement<T> item = getContext(type).createUnmarshaller().unmarshal(toSource(xml), type);
            return item.getValue();
        } catch (JAXBException | IOException e) {
            throw new DataBindingException(e);
        }
    }



    /**
     * Creates {@link Source} from various XML representation.
     * See {@link #unmarshal} for the conversion rules.
     */
    private static Source toSource(Object xml) throws IOException {
        if(xml==null)
            throw new IllegalArgumentException("no XML is given");

        if (xml instanceof String) {
            try {
                xml=new URI((String)xml);
            } catch (URISyntaxException e) {
                xml=new File((String)xml);
            }
        }
        if (xml instanceof File) {
            File file = (File) xml;
            return new StreamSource(file);
        }
        if (xml instanceof URI) {
            URI uri = (URI) xml;
            xml=uri.toURL();
        }
        if (xml instanceof URL) {
            URL url = (URL) xml;
            return new StreamSource(url.toExternalForm());
        }
        if (xml instanceof InputStream) {
            InputStream in = (InputStream) xml;
            return new StreamSource(in);
        }
        if (xml instanceof Reader) {
            Reader r = (Reader) xml;
            return new StreamSource(r);
        }
        if (xml instanceof Source) {
            return (Source) xml;
        }
        throw new IllegalArgumentException("I don't understand how to handle "+xml.getClass());
    }

    /**
     * Writes a Java object tree to XML and store it to the specified location.
     *
     * @param jaxbObject
     *      The Java object to be marshalled into XML. If this object is
     *      a {@link JAXBElement}, it will provide the root tag name and
     *      the body. If this object has {@link XmlRootElement}
     *      on its class definition, that will be used as the root tag name
     *      and the given object will provide the body. Otherwise,
     *      the root tag name is {@link java.beans.Introspector#decapitalize(String) infered} from
     *      {@link Class#getSimpleName() the short class name}.
     *      This parameter must not be null.
     *
     * @param xml
     *      XML will be written to this file. If it already exists,
     *      it will be overwritten.
     *
     * @throws DataBindingException
     *      If the operation fails, such as due to I/O error, unbindable classes.
     */
    public static void marshal( Object jaxbObject, File xml ) {
        _marshal(jaxbObject,xml);
    }

    /**
     * Writes a Java object tree to XML and store it to the specified location.
     *
     * @param jaxbObject
     *      The Java object to be marshalled into XML. If this object is
     *      a {@link JAXBElement}, it will provide the root tag name and
     *      the body. If this object has {@link XmlRootElement}
     *      on its class definition, that will be used as the root tag name
     *      and the given object will provide the body. Otherwise,
     *      the root tag name is {@link java.beans.Introspector#decapitalize(String) infered} from
     *      {@link Class#getSimpleName() the short class name}.
     *      This parameter must not be null.
     *
     * @param xml
     *      The XML will be {@link URLConnection#getOutputStream() sent} to the
     *      resource pointed by this URL. Note that not all {@code URL}s support
     *      such operation, and exact semantics depends on the {@code URL}
     *      implementations. In case of {@link HttpURLConnection HTTP URLs},
     *      this will perform HTTP POST.
     *
     * @throws DataBindingException
     *      If the operation fails, such as due to I/O error, unbindable classes.
     */
    public static void marshal( Object jaxbObject, URL xml ) {
        _marshal(jaxbObject,xml);
    }

    /**
     * Writes a Java object tree to XML and store it to the specified location.
     *
     * @param jaxbObject
     *      The Java object to be marshalled into XML. If this object is
     *      a {@link JAXBElement}, it will provide the root tag name and
     *      the body. If this object has {@link XmlRootElement}
     *      on its class definition, that will be used as the root tag name
     *      and the given object will provide the body. Otherwise,
     *      the root tag name is {@link java.beans.Introspector#decapitalize(String) infered} from
     *      {@link Class#getSimpleName() the short class name}.
     *      This parameter must not be null.
     *
     * @param xml
     *      The URI is {@link URI#toURL() turned into URL} and then
     *      follows the handling of {@code URL}. See above.
     *
     * @throws DataBindingException
     *      If the operation fails, such as due to I/O error, unbindable classes.
     */
    public static void marshal( Object jaxbObject, URI xml ) {
        _marshal(jaxbObject,xml);
    }

    /**
     * Writes a Java object tree to XML and store it to the specified location.
     *
     * @param jaxbObject
     *      The Java object to be marshalled into XML. If this object is
     *      a {@link JAXBElement}, it will provide the root tag name and
     *      the body. If this object has {@link XmlRootElement}
     *      on its class definition, that will be used as the root tag name
     *      and the given object will provide the body. Otherwise,
     *      the root tag name is {@link java.beans.Introspector#decapitalize(String) infered} from
     *      {@link Class#getSimpleName() the short class name}.
     *      This parameter must not be null.
     *
     * @param xml
     *      The string is first interpreted as an absolute {@code URI}.
     *      If it's not {@link URI#isAbsolute() a valid absolute URI},
     *      then it's interpreted as a {@code File}
     *
     * @throws DataBindingException
     *      If the operation fails, such as due to I/O error, unbindable classes.
     */
    public static void marshal( Object jaxbObject, String xml ) {
        _marshal(jaxbObject,xml);
    }

    /**
     * Writes a Java object tree to XML and store it to the specified location.
     *
     * @param jaxbObject
     *      The Java object to be marshalled into XML. If this object is
     *      a {@link JAXBElement}, it will provide the root tag name and
     *      the body. If this object has {@link XmlRootElement}
     *      on its class definition, that will be used as the root tag name
     *      and the given object will provide the body. Otherwise,
     *      the root tag name is {@link java.beans.Introspector#decapitalize(String) infered} from
     *      {@link Class#getSimpleName() the short class name}.
     *      This parameter must not be null.
     *
     * @param xml
     *      The XML will be sent to the given {@link OutputStream}.
     *      Upon a successful completion, the stream will be closed by this method.
     *
     * @throws DataBindingException
     *      If the operation fails, such as due to I/O error, unbindable classes.
     */
    public static void marshal( Object jaxbObject, OutputStream xml ) {
        _marshal(jaxbObject,xml);
    }

    /**
     * Writes a Java object tree to XML and store it to the specified location.
     *
     * @param jaxbObject
     *      The Java object to be marshalled into XML. If this object is
     *      a {@link JAXBElement}, it will provide the root tag name and
     *      the body. If this object has {@link XmlRootElement}
     *      on its class definition, that will be used as the root tag name
     *      and the given object will provide the body. Otherwise,
     *      the root tag name is {@link java.beans.Introspector#decapitalize(String) infered} from
     *      {@link Class#getSimpleName() the short class name}.
     *      This parameter must not be null.
     *
     * @param xml
     *      The XML will be sent as a character stream to the given {@link Writer}.
     *      Upon a successful completion, the stream will be closed by this method.
     *
     * @throws DataBindingException
     *      If the operation fails, such as due to I/O error, unbindable classes.
     */
    public static void marshal( Object jaxbObject, Writer xml ) {
        _marshal(jaxbObject,xml);
    }

    /**
     * Writes a Java object tree to XML and store it to the specified location.
     *
     * @param jaxbObject
     *      The Java object to be marshalled into XML. If this object is
     *      a {@link JAXBElement}, it will provide the root tag name and
     *      the body. If this object has {@link XmlRootElement}
     *      on its class definition, that will be used as the root tag name
     *      and the given object will provide the body. Otherwise,
     *      the root tag name is {@link java.beans.Introspector#decapitalize(String) infered} from
     *      {@link Class#getSimpleName() the short class name}.
     *      This parameter must not be null.
     *
     * @param xml
     *      The XML will be sent to the {@link Result} object.
     *
     * @throws DataBindingException
     *      If the operation fails, such as due to I/O error, unbindable classes.
     */
    public static void marshal( Object jaxbObject, Result xml ) {
        _marshal(jaxbObject,xml);
    }

    /**
     * Writes a Java object tree to XML and store it to the specified location.
     *
     * <p>
     * This method is a convenience method that combines several basic operations
     * in the {@link JAXBContext} and {@link Marshaller}. This method is designed
     * to be the prefered method for developers new to Jakarta XML Binding. This method
     * has the following characterstics:
     *
     * <ol>
     *  <li>Generally speaking, the performance is not necessarily optimal.
     *      It is expected that those people who need to write performance
     *      critical code will use the rest of the Jakarta XML Binding API directly.
     *  <li>Errors that happen during the processing is wrapped into
     *      {@link DataBindingException} (which will have {@link JAXBException}
     *      as its {@link Throwable#getCause() cause}. It is expected that
     *      those people who prefer the checked exception would use
     *      the rest of the Jakarta XML Binding API directly.
     * </ol>
     *
     * @param jaxbObject
     *      The Java object to be marshalled into XML. If this object is
     *      a {@link JAXBElement}, it will provide the root tag name and
     *      the body. If this object has {@link XmlRootElement}
     *      on its class definition, that will be used as the root tag name
     *      and the given object will provide the body. Otherwise,
     *      the root tag name is {@link java.beans.Introspector#decapitalize(String) infered} from
     *      {@link Class#getSimpleName() the short class name}.
     *      This parameter must not be null.
     *
     * @param xml
     *      Represents the receiver of XML. Objects of the following types are allowed.
     *
     *      <table><tr>
     *          <th>Type</th>
     *          <th>Operation</th>
     *      </tr><tr>
     *          <td>{@link File}</td>
     *          <td>XML will be written to this file. If it already exists,
     *              it will be overwritten.</td>
     *      </tr><tr>
     *          <td>{@link URL}</td>
     *          <td>The XML will be {@link URLConnection#getOutputStream() sent} to the
     *              resource pointed by this URL. Note that not all {@code URL}s support
     *              such operation, and exact semantics depends on the {@code URL}
     *              implementations. In case of {@link HttpURLConnection HTTP URLs},
     *              this will perform HTTP POST.</td>
     *      </tr><tr>
     *          <td>{@link URI}</td>
     *          <td>The URI is {@link URI#toURL() turned into URL} and then
     *              follows the handling of {@code URL}. See above.</td>
     *      </tr><tr>
     *          <td>{@link String}</td>
     *          <td>The string is first interpreted as an absolute {@code URI}.
     *              If it's not {@link URI#isAbsolute() a valid absolute URI},
     *              then it's interpreted as a {@code File}</td>
     *      </tr><tr>
     *          <td>{@link OutputStream}</td>
     *          <td>The XML will be sent to the given {@link OutputStream}.
     *              Upon a successful completion, the stream will be closed by this method.</td>
     *      </tr><tr>
     *          <td>{@link Writer}</td>
     *          <td>The XML will be sent as a character stream to the given {@link Writer}.
     *              Upon a successful completion, the stream will be closed by this method.</td>
     *      </tr><tr>
     *          <td>{@link Result}</td>
     *          <td>The XML will be sent to the {@link Result} object.</td>
     *      </tr></table>
     *
     * @throws DataBindingException
     *      If the operation fails, such as due to I/O error, unbindable classes.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void _marshal( Object jaxbObject, Object xml ) {
        try {
            JAXBContext context;

            if(jaxbObject instanceof JAXBElement) {
                context = getContext(((JAXBElement<?>)jaxbObject).getDeclaredType());
            } else {
                Class<?> clazz = jaxbObject.getClass();
                XmlRootElement r = clazz.getAnnotation(XmlRootElement.class);
                context = getContext(clazz);
                if(r==null) {
                    // we need to infer the name
                    jaxbObject = new JAXBElement(new QName(inferName(clazz)),clazz,jaxbObject);
                }
            }

            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
            m.marshal(jaxbObject, toResult(xml));
        } catch (JAXBException | IOException e) {
            throw new DataBindingException(e);
        }
    }

    private static String inferName(Class<?> clazz) {
        // XXX - behaviour of this method must be same as of Introspector.decapitalize
        // which is not used to avoid dependency on java.desktop
        String simpleName = clazz.getSimpleName();
        if (simpleName == null || simpleName.isEmpty()) {
            return simpleName;
        }
        if (simpleName.length() > 1 && Character.isUpperCase(simpleName.charAt(1))
                && Character.isUpperCase(simpleName.charAt(0))) {
            return simpleName;
        }
        char chars[] = simpleName.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    /**
     * Creates {@link Result} from various XML representation.
     * See {@link #_marshal(Object,Object)} for the conversion rules.
     */
    private static Result toResult(Object xml) throws IOException {
        if(xml==null)
            throw new IllegalArgumentException("no XML is given");

        if (xml instanceof String) {
            try {
                xml=new URI((String)xml);
            } catch (URISyntaxException e) {
                xml=new File((String)xml);
            }
        }
        if (xml instanceof File) {
            File file = (File) xml;
            return new StreamResult(file);
        }
        if (xml instanceof URI) {
            URI uri = (URI) xml;
            xml=uri.toURL();
        }
        if (xml instanceof URL) {
            URL url = (URL) xml;
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            con.setDoInput(false);
            con.connect();
            return new StreamResult(con.getOutputStream());
        }
        if (xml instanceof OutputStream) {
            OutputStream os = (OutputStream) xml;
            return new StreamResult(os);
        }
        if (xml instanceof Writer) {
            Writer w = (Writer)xml;
            return new StreamResult(w);
        }
        if (xml instanceof Result) {
            return (Result) xml;
        }
        throw new IllegalArgumentException("I don't understand how to handle "+xml.getClass());
    }

}
