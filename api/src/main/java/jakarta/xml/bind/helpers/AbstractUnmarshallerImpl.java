/*
 * Copyright (c) 2003, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind.helpers;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.w3c.dom.Node;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.PropertyException;
import jakarta.xml.bind.UnmarshalException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.ValidationEventHandler;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.attachment.AttachmentUnmarshaller;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Reader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import java.net.URL;

/**
 * Partial default {@code Unmarshaller} implementation.
 *
 * <p>
 * This class provides a partial default implementation for the
 * {@link jakarta.xml.bind.Unmarshaller}interface.
 *
 * <p>
 * A Jakarta XML Binding Provider has to implement five methods (getUnmarshallerHandler,
 * unmarshal(Node), unmarshal(XMLReader,InputSource),
 * unmarshal(XMLStreamReader), and unmarshal(XMLEventReader).
 *
 * @author <ul>
 *         <li>Kohsuke Kawaguchi, Sun Microsystems, Inc.</li>
 *         </ul>
 * @see jakarta.xml.bind.Unmarshaller
 * @since 1.6, JAXB 1.0
 */
public abstract class AbstractUnmarshallerImpl implements Unmarshaller
{
    /** handler that will be used to process errors and warnings during unmarshal */
    private ValidationEventHandler eventHandler =
        new DefaultValidationEventHandler();

    /**
     * XMLReader that will be used to parse a document.
     */
    private XMLReader reader = null;

    /**
     * Do-nothing constructor for the derived classes.
     */
    protected AbstractUnmarshallerImpl() {}

    /**
     * Obtains a configured XMLReader.
     *
     * This method is used when the client-specified
     * {@link SAXSource} object doesn't have XMLReader.
     *
     * {@link Unmarshaller} is not re-entrant, so we will
     * only use one instance of XMLReader.
     */
    protected XMLReader getXMLReader() throws JAXBException {
        if(reader==null) {
            try {
                SAXParserFactory parserFactory;
                parserFactory = SAXParserFactory.newInstance();
                parserFactory.setNamespaceAware(true);
                // there is no point in asking a validation because
                // there is no guarantee that the document will come with
                // a proper schemaLocation.
                parserFactory.setValidating(false);
                reader = parserFactory.newSAXParser().getXMLReader();
            } catch( ParserConfigurationException | SAXException e ) {
                throw new JAXBException(e);
            }
        }
        return reader;
    }

    @Override
    public Object unmarshal( Source source ) throws JAXBException {
        if( source == null ) {
            throw new IllegalArgumentException(
                Messages.format( Messages.MUST_NOT_BE_NULL, "source" ) );
        }

        if(source instanceof SAXSource)
            return unmarshal( (SAXSource)source );
        if(source instanceof StreamSource)
            return unmarshal( streamSourceToInputSource((StreamSource)source));
        if(source instanceof DOMSource)
            return unmarshal( ((DOMSource)source).getNode() );

        // we don't handle other types of Source
        throw new IllegalArgumentException();
    }

    // use the client specified XMLReader contained in the SAXSource.
    private Object unmarshal( SAXSource source ) throws JAXBException {

        XMLReader r = source.getXMLReader();
        if( r == null )
            r = getXMLReader();

        return unmarshal( r, source.getInputSource() );
    }

    /**
     * Unmarshals an object by using the specified XMLReader and the InputSource.
     *
     * The callee should call the setErrorHandler method of the XMLReader
     * so that errors are passed to the client-specified ValidationEventHandler.
     */
    protected abstract Object unmarshal( XMLReader reader, InputSource source ) throws JAXBException;

    @Override
    public final Object unmarshal( InputSource source ) throws JAXBException {
        if( source == null ) {
            throw new IllegalArgumentException(
                Messages.format( Messages.MUST_NOT_BE_NULL, "source" ) );
        }

        return unmarshal( getXMLReader(), source );
    }


    private Object unmarshal( String url ) throws JAXBException {
        return unmarshal( new InputSource(url) );
    }

    @Override
    public final Object unmarshal( URL url ) throws JAXBException {
        if( url == null ) {
            throw new IllegalArgumentException(
                Messages.format( Messages.MUST_NOT_BE_NULL, "url" ) );
        }

        return unmarshal( url.toExternalForm() );
    }

    @Override
    public final Object unmarshal( File f ) throws JAXBException {
        if( f == null ) {
            throw new IllegalArgumentException(
                Messages.format( Messages.MUST_NOT_BE_NULL, "file" ) );
        }

        try {
            return unmarshal(new BufferedInputStream(new FileInputStream(f)));
        } catch( FileNotFoundException e ) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public final Object unmarshal( java.io.InputStream is )
        throws JAXBException {

        if( is == null ) {
            throw new IllegalArgumentException(
                Messages.format( Messages.MUST_NOT_BE_NULL, "is" ) );
        }

        InputSource isrc = new InputSource( is );
        return unmarshal( isrc );
    }

    @Override
    public final Object unmarshal( Reader reader ) throws JAXBException {
        if( reader == null ) {
            throw new IllegalArgumentException(
                Messages.format( Messages.MUST_NOT_BE_NULL, "reader" ) );
        }

        InputSource isrc = new InputSource( reader );
        return unmarshal( isrc );
    }


    private static InputSource streamSourceToInputSource( StreamSource ss ) {
        InputSource is = new InputSource();
        is.setSystemId( ss.getSystemId() );
        is.setByteStream( ss.getInputStream() );
        is.setCharacterStream( ss.getReader() );

        return is;
    }


    /**
     * Allow an application to register a validation event handler.
     * <p>
     * The validation event handler will be called by the Jakarta XML Binding Provider if any
     * validation errors are encountered during calls to any of the
     * {@code unmarshal} methods.  If the client application does not register
     * a validation event handler before invoking the unmarshal methods, then
     * all validation events will be silently ignored and may result in
     * unexpected behaviour.
     *
     * @param handler the validation event handler
     * @throws JAXBException if an error was encountered while setting the
     *        event handler
     */
    @Override
    public void setEventHandler(ValidationEventHandler handler)
        throws JAXBException {

        if( handler == null ) {
            eventHandler = new DefaultValidationEventHandler();
        } else {
            eventHandler = handler;
        }
    }


    /**
     * Return the current event handler or the default event handler if one
     * hasn't been set.
     *
     * @return the current ValidationEventHandler or the default event handler
     *        if it hasn't been set
     * @throws JAXBException if an error was encountered while getting the
     *        current event handler
     */
    @Override
    public ValidationEventHandler getEventHandler() throws JAXBException {
        return eventHandler;
    }


    /**
     * Creates an UnmarshalException from a SAXException.
     *
     * This is an utility method provided for the derived classes.
     *
     * <p>
     * When a provider-implemented ContentHandler wants to throw a
     * JAXBException, it needs to wrap the exception by a SAXException.
     * If the unmarshaller implementation blindly wrap SAXException
     * by JAXBException, such an exception will be a JAXBException
     * wrapped by a SAXException wrapped by another JAXBException.
     * This is silly.
     *
     * <p>
     * This method checks the nested exception of SAXException
     * and reduce those excessive wrapping.
     *
     * @return the resulting UnmarshalException
     */
    protected UnmarshalException createUnmarshalException( SAXException e ) {
        // check the nested exception to see if it's an UnmarshalException
        Exception nested = e.getException();
        if(nested instanceof UnmarshalException)
            return (UnmarshalException)nested;

        if(nested instanceof RuntimeException)
            // typically this is an unexpected exception,
            // just throw it rather than wrap it, so that the full stack
            // trace can be displayed.
            throw (RuntimeException)nested;


        // otherwise simply wrap it
        if(nested!=null)
            return new UnmarshalException(nested);
        else
            return new UnmarshalException(e);
    }

    /**
     * Default implementation of the setProperty method always
     * throws PropertyException since there are no required
     * properties. If a provider needs to handle additional
     * properties, it should override this method in a derived class.
     */
    @Override
    public void setProperty( String name, Object value )
        throws PropertyException {

        if( name == null ) {
            throw new IllegalArgumentException(
                Messages.format( Messages.MUST_NOT_BE_NULL, "name" ) );
        }

        throw new PropertyException(name, value);
    }

    /**
     * Default implementation of the getProperty method always
     * throws PropertyException since there are no required
     * properties. If a provider needs to handle additional
     * properties, it should override this method in a derived class.
     */
    @Override
    public Object getProperty( String name )
        throws PropertyException {

        if( name == null ) {
            throw new IllegalArgumentException(
                Messages.format( Messages.MUST_NOT_BE_NULL, "name" ) );
        }

        throw new PropertyException(name);
    }

    @Override
    public Object unmarshal(XMLEventReader reader) throws JAXBException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Object unmarshal(XMLStreamReader reader) throws JAXBException {

        throw new UnsupportedOperationException();
    }

    @Override
    public <T> JAXBElement<T> unmarshal(Node node, Class<T> expectedType) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> JAXBElement<T> unmarshal(Source source, Class<T> expectedType) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> JAXBElement<T> unmarshal(XMLStreamReader reader, Class<T> expectedType) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> JAXBElement<T> unmarshal(XMLEventReader reader, Class<T> expectedType) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSchema(Schema schema) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Schema getSchema() {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends XmlAdapter<?, ?>> void setAdapter(A adapter) {
        if(adapter==null) {
            throw new IllegalArgumentException();
        }
        setAdapter((Class<A>) adapter.getClass(),adapter);
    }

    @Override
    public <A extends XmlAdapter<?, ?>> void setAdapter(Class<A> type, A adapter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <A extends XmlAdapter<?, ?>> A getAdapter(Class<A> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttachmentUnmarshaller(AttachmentUnmarshaller au) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AttachmentUnmarshaller getAttachmentUnmarshaller() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setListener(Listener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Listener getListener() {
        throw new UnsupportedOperationException();
    }
}
