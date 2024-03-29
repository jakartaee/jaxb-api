/*
 * Copyright (c) 2003, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind;

import org.xml.sax.ContentHandler;

/**
 * Unmarshaller implemented as SAX ContentHandler.
 * 
 * <p>
 * Applications can use this interface to use their Jakarta XML Binding provider as a component 
 * in an XML pipeline.  For example:
 * 
 * {@snippet :
 *  JAXBContext context = JAXBContext.newInstance( "org.acme.foo" );
 *
 *  Unmarshaller unmarshaller = context.createUnmarshaller();
 * 
 *  UnmarshallerHandler unmarshallerHandler = unmarshaller.getUnmarshallerHandler();
 *
 *  SAXParserFactory spf = SAXParserFactory.newInstance();
 *  spf.setNamespaceAware( true );
 * 
 *  XMLReader xmlReader = spf.newSAXParser().getXMLReader();
 *  xmlReader.setContentHandler( unmarshallerHandler );
 *  xmlReader.parse(new InputSource( new FileInputStream( XML_FILE ) ) );
 *
 *  MyObject myObject= (MyObject)unmarshallerHandler.getResult();
 * }
 * 
 * <p>
 * This interface is reusable: even if the user fails to unmarshal
 * an object, s/he can still start a new round of unmarshalling.
 * 
 * @author <ul><li>Kohsuke KAWAGUCHI, Sun Microsystems, Inc.</li></ul>
 * @see Unmarshaller#getUnmarshallerHandler()
 * @since 1.6, JAXB 1.0
 */
public interface UnmarshallerHandler extends ContentHandler
{
    /**
     * Obtains the unmarshalled result.
     * <p>
     * This method can be called only after this handler
     * receives the endDocument SAX event.
     * 
     * @exception IllegalStateException
     *      if this method is called before this handler
     *      receives the endDocument event.
     * 
     * @exception JAXBException
     *      if there is any unmarshalling error.
     *      Note that the implementation is allowed to throw SAXException
     *      during the parsing when it finds an error.
     * 
     * @return
     *      always return a non-null valid object which was unmarshalled.
     */
    Object getResult() throws JAXBException, IllegalStateException;
}
