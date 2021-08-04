/*
 * Copyright (c) 2003, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind.util;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.UnmarshallerHandler;
import javax.xml.transform.sax.SAXResult;

/**
 * JAXP {@link javax.xml.transform.Result} implementation
 * that unmarshals a Jakarta XML Binding object.
 * 
 * <p>
 * This utility class is useful to combine Jakarta XML Binding with
 * other Java/XML technologies.
 * 
 * <p>
 * The following example shows how to use Jakarta XML Binding to unmarshal a document
 * resulting from an XSLT transformation.
 * 
 * <blockquote>
 *    <pre>
 *       JAXBResult result = new JAXBResult(
 *         JAXBContext.newInstance("org.acme.foo") );
 *       
 *       // set up XSLT transformation
 *       TransformerFactory tf = TransformerFactory.newInstance();
 *       Transformer t = tf.newTransformer(new StreamSource("test.xsl"));
 *       
 *       // run transformation
 *       t.transform(new StreamSource("document.xml"),result);
 * 
 *       // obtain the unmarshalled content tree
 *       Object o = result.getResult();
 *    </pre>
 * </blockquote>
 * 
 * <p>
 * The fact that JAXBResult derives from SAXResult is an implementation
 * detail. Thus in general applications are strongly discouraged from
 * accessing methods defined on SAXResult.
 * 
 * <p>
 * In particular it shall never attempt to call the setHandler, 
 * setLexicalHandler, and setSystemId methods.
 * 
 * @author
 * 	Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 * @since 1.6
 */
public class JAXBResult extends SAXResult {

    /**
     * Creates a new instance that uses the specified
     * JAXBContext to unmarshal.
     * 
     * @param context The JAXBContext that will be used to create the
     * necessary Unmarshaller.  This parameter must not be null.
     * @exception JAXBException if an error is encountered while creating the
     * JAXBResult or if the context parameter is null.
     */
    public JAXBResult( JAXBContext context ) throws JAXBException {
        this( ( context == null ) ? assertionFailed() : context.createUnmarshaller() );
    }
    
    /**
     * Creates a new instance that uses the specified
     * Unmarshaller to unmarshal an object.
     * 
     * <p>
     * This JAXBResult object will use the specified Unmarshaller
     * instance. It is the caller's responsibility not to use the
     * same Unmarshaller for other purposes while it is being
     * used by this object.
     * 
     * <p>
     * The primary purpose of this method is to allow the client
     * to configure Unmarshaller. Unless you know what you are doing,
     * it's easier and safer to pass a JAXBContext.
     * 
     * @param _unmarshaller the unmarshaller.  This parameter must not be null.
     * @throws JAXBException if an error is encountered while creating the
     * JAXBResult or the Unmarshaller parameter is null.
     */
    public JAXBResult( Unmarshaller _unmarshaller ) throws JAXBException {
        if( _unmarshaller == null )
            throw new JAXBException( 
                Messages.format( Messages.RESULT_NULL_UNMARSHALLER ) );
            
        this.unmarshallerHandler = _unmarshaller.getUnmarshallerHandler();
        
        super.setHandler(unmarshallerHandler);
    }
    
    /**
     * Unmarshaller that will be used to unmarshal
     * the input documents.
     */
    private final UnmarshallerHandler unmarshallerHandler;

    /**
     * Gets the unmarshalled object created by the transformation.
     * 
     * @return
     *      Always return a non-null object.
     * 
     * @exception IllegalStateException
     * 	if this method is called before an object is unmarshalled.
     * 
     * @exception JAXBException
     *      if there is any unmarshalling error.
     *      Note that the implementation is allowed to throw SAXException
     *      during the parsing when it finds an error.
     */
    public Object getResult() throws JAXBException {
        return unmarshallerHandler.getResult();
    }
    
    /**
     * Hook to throw exception from the middle of a contructor chained call
     * to this
     */
    private static Unmarshaller assertionFailed() throws JAXBException {
        throw new JAXBException( Messages.format( Messages.RESULT_NULL_CONTEXT ) );
    }
}
