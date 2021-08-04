/*
 * Copyright (c) 2003, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind;

import org.w3c.dom.Node;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * The {@code JAXBContext} class provides the client's entry point to the
 * Jakarta XML Binding API. It provides an abstraction for managing the XML/Java binding
 * information necessary to implement the Jakarta XML Binding binding framework operations:
 * unmarshal, marshal and validate.
 *
 * <p>A client application normally obtains new instances of this class using
 * one of these two styles for newInstance methods, although there are other
 * specialized forms of the method available:
 *
 * <ul>
 * <li>{@link #newInstance(String, ClassLoader) JAXBContext.newInstance( "com.acme.foo:com.acme.bar" )} <br>
 * The JAXBContext instance is initialized from a list of colon
 * separated Java package names. Each java package contains
 * Jakarta XML Binding mapped classes, schema-derived classes and/or user annotated
 * classes. Additionally, the java package may contain Jakarta XML Binding package annotations
 * that must be processed. (see JLS, Section 7.4.1 "Named Packages").
 * </li>
 * <li>{@link #newInstance(Class...) JAXBContext.newInstance( com.acme.foo.Foo.class )} <br>
 * The JAXBContext instance is initialized with class(es)
 * passed as parameter(s) and classes that are statically reachable from
 * these class(es). See {@link #newInstance(Class...)} for details.
 * </li>
 * </ul>
 *
 * <p><i>
 * The provider must call the
 * {@link DatatypeConverter#setDatatypeConverter(DatatypeConverterInterface)
 * DatatypeConverter.setDatatypeConverter} api prior to any client
 * invocations of the marshal and unmarshal methods.  This is necessary to
 * configure the datatype converter that will be used during these operations.</i>
 *
 * <a id="Unmarshalling"></a>
 * <h2>Unmarshalling</h2>
 * <p>
 * The {@link Unmarshaller} class provides the client application the ability
 * to convert XML data into a tree of Java content objects.
 * The unmarshal method allows for
 * any global XML element declared in the schema to be unmarshalled as
 * the root of an instance document.
 * Additionally, the unmarshal method allows for an unrecognized root element that
 * has  an xsi:type attribute's value that references a type definition declared in
 * the schema  to be unmarshalled as the root of an instance document.
 * The {@code JAXBContext} object
 * allows the merging of global elements and type definitions across a set of schemas (listed
 * in the {@code contextPath}). Since each schema in the schema set can belong
 * to distinct namespaces, the unification of schemas to an unmarshalling
 * context must be namespace independent.  This means that a client
 * application is able to unmarshal XML documents that are instances of
 * any of the schemas listed in the {@code contextPath}.  For example:
 *
 * <pre>
 *      JAXBContext jc = JAXBContext.newInstance( "com.acme.foo:com.acme.bar" );
 *      Unmarshaller u = jc.createUnmarshaller();
 *      FooObject fooObj = (FooObject)u.unmarshal( new File( "foo.xml" ) ); // ok
 *      BarObject barObj = (BarObject)u.unmarshal( new File( "bar.xml" ) ); // ok
 *      BazObject bazObj = (BazObject)u.unmarshal( new File( "baz.xml" ) ); // error, "com.acme.baz" not in contextPath
 * </pre>
 *
 * <p>
 * The client application may also generate Java content trees explicitly rather
 * than unmarshalling existing XML data.  For all Jakarta XML Binding-annotated value classes,
 * an application can create content using constructors.
 * For schema-derived interface/implementation classes and for the
 * creation of elements that are not bound to a Jakarta XML Binding-annotated
 * class, an application needs to have access and knowledge about each of
 * the schema derived {@code ObjectFactory} classes that exist in each of
 * java packages contained in the {@code contextPath}.  For each schema
 * derived java class, there is a static factory method that produces objects
 * of that type.  For example,
 * assume that after compiling a schema, you have a package {@code com.acme.foo}
 * that contains a schema derived interface named {@code PurchaseOrder}.  In
 * order to create objects of that type, the client application would use the
 * factory method like this:
 *
 * <pre>
 *       com.acme.foo.PurchaseOrder po =
 *           com.acme.foo.ObjectFactory.createPurchaseOrder();
 * </pre>
 *
 * <p>
 * Once the client application has an instance of the the schema derived object,
 * it can use the mutator methods to set content on it.
 *
 * <p>
 * For more information on the generated {@code ObjectFactory} classes, see
 * Section 4.2 <i>Java Package</i> of the specification.
 *
 * <p>
 * <i>The provider must generate a class in each
 * package that contains all of the necessary object factory methods for that
 * package named ObjectFactory as well as the static
 * {@code newInstance( javaContentInterface )} method</i>
 *
 * <h3>Marshalling</h3>
 * <p>
 * The {@link Marshaller} class provides the client application the ability
 * to convert a Java content tree back into XML data.  There is no difference
 * between marshalling a content tree that is created manually using the factory
 * methods and marshalling a content tree that is the result an {@code unmarshal}
 * operation.  Clients can marshal a java content tree back to XML data
 * to a {@code java.io.OutputStream} or a {@code java.io.Writer}.  The
 * marshalling process can alternatively produce SAX2 event streams to a
 * registered {@code ContentHandler} or produce a DOM Node object.
 * Client applications have control over the output encoding as well as
 * whether or not to marshal the XML data as a complete document or
 * as a fragment.
 *
 * <p>
 * Here is a simple example that unmarshals an XML document and then marshals
 * it back out:
 *
 * <pre>
 *        JAXBContext jc = JAXBContext.newInstance( "com.acme.foo" );
 *
 *        // unmarshal from foo.xml
 *        Unmarshaller u = jc.createUnmarshaller();
 *        FooObject fooObj = (FooObject)u.unmarshal( new File( "foo.xml" ) );
 *
 *        // marshal to System.out
 *        Marshaller m = jc.createMarshaller();
 *        m.marshal( fooObj, System.out );
 * </pre>
 *
 *
 * <h3>Validation</h3>
 * <p>
 * In Jakarta XML Binding, the {@link Unmarshaller} has included convenience methods that expose
 * the JAXP {@link javax.xml.validation} framework.  Please refer to the
 * {@link Unmarshaller#setSchema(javax.xml.validation.Schema)} API for more
 * information.
 *
 *
 * <h3>Jakarta XML Binding Runtime Framework Compatibility</h3>
 * <p>
 * The following JAXB 1.0 restriction only applies to binding schema to
 * interfaces/implementation classes.
 * Since this binding does not require a common runtime system, a Jakarta XML Binding
 * client application must not attempt to mix runtime objects ({@code JAXBContext,
 * Marshaller}, etc. ) from different providers.  This does not
 * mean that the client application isn't portable, it simply means that a
 * client has to use a runtime system provided by the same provider that was
 * used to compile the schema.
 *
 *
 * <h3>Discovery of Jakarta XML Binding implementation</h3>
 * <p>
 * To create an instance of {@link JAXBContext}, one of {@code JAXBContext.newInstance(...)} methods is invoked. After
 * JAX-B implementation is discovered, call is delegated to appropriate provider's method {@code createContext(...)}
 * passing parameters from the original call.
 * <p>
 * JAX-B implementation discovery happens each time {@code JAXBContext.newInstance} is invoked. If there is no user
 * specific configuration provided, default JAX-B provider must be returned.
 * <p>
 * Implementation discovery consists of following steps:
 *
 * <ol>
 *
 * <li>
 * If the system property {@link #JAXB_CONTEXT_FACTORY} exists, then its value is assumed to be the provider
 * factory class. This phase of the look up enables per-JVM override of the Jakarta XML Binding implementation.
 *
 * <li>
 * Provider of {@link jakarta.xml.bind.JAXBContextFactory} is loaded using the service-provider loading
 * facilities, defined by the {@link java.util.ServiceLoader} class, to attempt
 * to locate and load an implementation of the service using the {@linkplain
 * java.util.ServiceLoader#load(java.lang.Class) default loading mechanism}: the service-provider loading facility
 * will use the {@linkplain java.lang.Thread#getContextClassLoader() current thread's context class loader}
 * to attempt to load the context factory. If the context class loader is null, the
 * {@linkplain ClassLoader#getSystemClassLoader() system class loader} will be used.
 * <br>
 * In case of {@link java.util.ServiceConfigurationError service
 * configuration error} a {@link jakarta.xml.bind.JAXBException} will be thrown.
 *
 * <li>
 * Finally, if all the steps above fail, then the rest of the look up is unspecified. That said,
 * the recommended behavior is to simply look for some hard-coded platform default Jakarta XML Binding implementation.
 * This phase of the look up is so that the environment can have its own Jakarta XML Binding implementation as the last resort.
 * </ol>
 *
 * <p>
 * Once the provider factory class is discovered, context creation is delegated to one of its
 * {@code createContext(...)} methods.
 *
 * @implNote
 * Within the last step, if Glassfish AS environment detected, its specific service loader is used to find factory class.
 *
 * @author <ul><li>Ryan Shoemaker, Sun Microsystems, Inc.</li>
 *             <li>Kohsuke Kawaguchi, Sun Microsystems, Inc.</li>
 *             <li>Joe Fialli, Sun Microsystems, Inc.</li></ul>
 *
 * @see Marshaller
 * @see Unmarshaller
 * @see <a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-7.html#jls-7.4.1">S 7.4.1 "Named Packages"
 *      in Java Language Specification</a>
 *
 * @since 1.6, JAXB 1.0
 */
public abstract class JAXBContext {

    /**
     * The name of the property that contains the name of the class capable
     * of creating new {@code JAXBContext} objects.
     */
    public static final String JAXB_CONTEXT_FACTORY = "jakarta.xml.bind.JAXBContextFactory";

    protected JAXBContext() {
    }


    /**
     * Create a new instance of a {@code JAXBContext} class.
     *
     * <p>
     * This is a convenience method to invoke the
     * {@link #newInstance(String,ClassLoader)} method with
     * the context class loader of the current thread.
     *
     * @throws JAXBException if an error was encountered while creating the
     *                       {@code JAXBContext} such as
     * <ol>
     *   <li>failure to locate either ObjectFactory.class or jaxb.index in the packages</li>
     *   <li>an ambiguity among global elements contained in the contextPath</li>
     *   <li>failure to locate a value for the context factory provider property</li>
     *   <li>mixing schema derived packages from different providers on the same contextPath</li>
     *   <li>packages are not open to {@code jakarta.xml.bind} module</li>
     * </ol>
     */
    public static JAXBContext newInstance( String contextPath )
            throws JAXBException {

        //return newInstance( contextPath, JAXBContext.class.getClassLoader() );
        return newInstance( contextPath, getContextClassLoader());
    }

    /**
     * Create a new instance of a {@code JAXBContext} class.
     *
     * <p>
     * The client application must supply a context path which is a list of
     * colon (':', \u005Cu003A) separated java package names that contain
     * schema-derived classes and/or fully qualified Jakarta XML Binding-annotated classes.
     * Schema-derived
     * code is registered with the JAXBContext by the
     * ObjectFactory.class generated per package.
     * Alternatively than being listed in the context path, programmer
     * annotated Jakarta XML Binding mapped classes can be listed in a
     * {@code jaxb.index} resource file, format described below.
     * Note that a java package can contain both schema-derived classes and
     * user annotated Jakarta XML Binding classes. Additionally, the java package may
     * contain Jakarta XML Binding package annotations  that must be processed. (see JLS,
     * Section 7.4.1 "Named Packages").
     * </p>
     *
     * <p>
     * Every package listed on the contextPath must meet <b>one or both</b> of the
     * following conditions otherwise a {@code JAXBException} will be thrown:
     * </p>
     * <ol>
     *   <li>it must contain ObjectFactory.class</li>
     *   <li>it must contain jaxb.index</li>
     * </ol>
     *
     * <p>
     * <b>Format for jaxb.index</b>
     * <p>
     * The file contains a newline-separated list of class names.
     * Space and tab characters, as well as blank
     * lines, are ignored. The comment character
     * is '#' (0x23); on each line all characters following the first comment
     * character are ignored. The file must be encoded in UTF-8. Classes that
     * are reachable, as defined in {@link #newInstance(Class...)}, from the
     * listed classes are also registered with JAXBContext.
     * <p>
     * Constraints on class name occuring in a {@code jaxb.index} file are:
     * <ul>
     *   <li>Must not end with ".class".</li>
     *   <li>Class names are resolved relative to package containing
     *       {@code jaxb.index} file. Only classes occuring directly in package
     *       containing {@code jaxb.index} file are allowed.</li>
     *   <li>Fully qualified class names are not allowed.
     *       A qualified class name,relative to current package,
     *       is only allowed to specify a nested or inner class.</li>
     * </ul>
     *
     * <p>
     * If there are any global XML element name collisions across the various
     * packages listed on the {@code contextPath}, a {@code JAXBException}
     * will be thrown.
     *
     * <p>
     * Mixing generated interface/impl bindings from multiple Jakarta XML Binding Providers
     * in the same context path may result in a {@code JAXBException}
     * being thrown.
     *
     * <p>
     * The steps involved in discovering the Jakarta XML Binding implementation is discussed in the class javadoc.
     *
     * @param contextPath
     *      List of java package names that contain schema
     *      derived class and/or java to schema (Jakarta XML Binding-annotated)
     *      mapped classes.
     *      Packages in {@code contextPath} that are in named modules must be
     *      {@code open} to at least the {@code jakarta.xml.bind} module.
     * @param classLoader
     *      This class loader will be used to locate the implementation
     *      classes.
     *
     * @return a new instance of a {@code JAXBContext}
     * @throws JAXBException if an error was encountered while creating the
     *                       {@code JAXBContext} such as
     * <ol>
     *   <li>failure to locate either ObjectFactory.class or jaxb.index in the packages</li>
     *   <li>an ambiguity among global elements contained in the contextPath</li>
     *   <li>failure to locate a value for the context factory provider property</li>
     *   <li>mixing schema derived packages from different providers on the same contextPath</li>
     *   <li>packages are not open to {@code jakarta.xml.bind} module</li>
     * </ol>
     */
    public static JAXBContext newInstance( String contextPath, ClassLoader classLoader ) throws JAXBException {

        return newInstance(contextPath,classLoader,Collections.<String,Object>emptyMap());
    }

    /**
     * Create a new instance of a {@code JAXBContext} class.
     *
     * <p>
     * This is mostly the same as {@link JAXBContext#newInstance(String, ClassLoader)},
     * but this version allows you to pass in provider-specific properties to configure
     * the instantiation of {@link JAXBContext}.
     *
     * <p>
     * The interpretation of properties is up to implementations. Implementations must
     * throw {@code JAXBException} if it finds properties that it doesn't understand.
     *
     * @param contextPath
     *      List of java package names that contain schema
     *      derived class and/or java to schema (Jakarta XML Binding-annotated)
     *      mapped classes.
     *      Packages in {@code contextPath} that are in named modules must be
     *      {@code open} to at least the {@code jakarta.xml.bind} module.
     * @param classLoader
     *      This class loader will be used to locate the implementation classes.
     * @param properties
     *      provider-specific properties. Can be null, which means the same thing as passing
     *      in an empty map.
     *
     * @return a new instance of a {@code JAXBContext}
     * @throws JAXBException if an error was encountered while creating the
     *                       {@code JAXBContext} such as
     * <ol>
     *   <li>failure to locate either ObjectFactory.class or jaxb.index in the packages</li>
     *   <li>an ambiguity among global elements contained in the contextPath</li>
     *   <li>failure to locate a value for the context factory provider property</li>
     *   <li>mixing schema derived packages from different providers on the same contextPath</li>
     *   <li>packages are not open to {@code jakarta.xml.bind} module</li>
     * </ol>
     * @since 1.6, JAXB 2.0
     */
    public static JAXBContext newInstance( String contextPath,
                                           ClassLoader classLoader,
                                           Map<String,?>  properties  ) throws JAXBException {

        return ContextFinder.find(
                        /* The default property name according to the Jakarta XML Binding spec */
                JAXB_CONTEXT_FACTORY,

                        /* the context path supplied by the client app */
                contextPath,

                        /* class loader to be used */
                classLoader,
                properties );
    }

// TODO: resurrect this once we introduce external annotations
//    /**
//     * Create a new instance of a {@code JAXBContext} class.
//     *
//     * <p>
//     * The client application must supply a list of classes that the new
//     * context object needs to recognize.
//     *
//     * Not only the new context will recognize all the classes specified,
//     * but it will also recognize any classes that are directly/indirectly
//     * referenced statically from the specified classes.
//     *
//     * For example, in the following Java code, if you do
//     * {@code newInstance(Foo.class)}, the newly created {@link JAXBContext}
//     * will recognize both {@code Foo} and {@code Bar}, but not {@code Zot}:
//     * <pre>
//     * class Foo {
//     *      Bar b;
//     * }
//     * class Bar { int x; }
//     * class Zot extends Bar { int y; }
//     * </pre>
//     *
//     * Therefore, a typical client application only needs to specify the
//     * top-level classes, but it needs to be careful.
//     *
//     * TODO: if we are to define other mechanisms, refer to them.
//     *
//     * @param externalBindings
//     *      list of external binding files. Can be null or empty if none is used.
//     *      when specified, those files determine how the classes are bound.
//     *
//     * @param classesToBeBound
//     *      list of java classes to be recognized by the new {@link JAXBContext}.
//     *      Can be empty, in which case a {@link JAXBContext} that only knows about
//     *      spec-defined classes will be returned.
//     *
//     * @return
//     *      A new instance of a {@code JAXBContext}.
//     *
//     * @throws JAXBException
//     *      if an error was encountered while creating the
//     *      {@code JAXBContext}, such as (but not limited to):
//     * <ol>
//     *  <li>No Jakarta XML Binding implementation was discovered
//     *  <li>Classes use Jakarta XML Binding annotations incorrectly
//     *  <li>Classes have colliding annotations (i.e., two classes with the same type name)
//     *  <li>Specified external bindings are incorrect
//     *  <li>The Jakarta XML Binding implementation was unable to locate
//     *      provider-specific out-of-band information (such as additional
//     *      files generated at the development time.)
//     * </ol>
//     *
//     * @throws IllegalArgumentException
//     *      if the parameter contains {@code null} (i.e., {@code newInstance(null);})
//     *
//     * @since JAXB 2.0
//     */
//    public static JAXBContext newInstance( Source[] externalBindings, Class... classesToBeBound )
//        throws JAXBException {
//
//        // empty class list is not an error, because the context will still include
//        // spec-specified classes like String and Integer.
//        // if(classesToBeBound.length==0)
//        //    throw new IllegalArgumentException();
//
//        // but it is an error to have nulls in it.
//        for( int i=classesToBeBound.length-1; i>=0; i-- )
//            if(classesToBeBound[i]==null)
//                throw new IllegalArgumentException();
//
//        return ContextFinder.find(externalBindings,classesToBeBound);
//    }

    /**
     * Create a new instance of a {@code JAXBContext} class.
     *
     * <p>
     * The client application must supply a list of classes that the new
     * context object needs to recognize.
     *
     * Not only the new context will recognize all the classes specified,
     * but it will also recognize any classes that are directly/indirectly
     * referenced statically from the specified classes. Subclasses of
     * referenced classes nor {@code @XmlTransient} referenced classes
     * are not registered with JAXBContext.
     *
     * For example, in the following Java code, if you do
     * {@code newInstance(Foo.class)}, the newly created {@link JAXBContext}
     * will recognize both {@code Foo} and {@code Bar}, but not {@code Zot} or {@code FooBar}:
     * <pre>
     * class Foo {
     *      &#64;XmlTransient FooBar c;
     *      Bar b;
     * }
     * class Bar { int x; }
     * class Zot extends Bar { int y; }
     * class FooBar { }
     * </pre>
     *
     * Therefore, a typical client application only needs to specify the
     * top-level classes, but it needs to be careful.
     *
     * <p>
     * Note that for each java package registered with JAXBContext,
     * when the optional package annotations exist, they must be processed.
     * (see JLS, Section 7.4.1 "Named Packages").
     *
     * <p>
     * The steps involved in discovering the Jakarta XML Binding implementation is discussed in the class javadoc.
     *
     * @param classesToBeBound
     *      List of java classes to be recognized by the new {@link JAXBContext}.
     *      Classes in {@code classesToBeBound} that are in named modules must be in a package
     *      that is {@code open} to at least the {@code jakarta.xml.bind} module.
     *      Can be empty, in which case a {@link JAXBContext} that only knows about
     *      spec-defined classes will be returned.
     *
     * @return
     *      A new instance of a {@code JAXBContext}.
     *
     * @throws JAXBException
     *      if an error was encountered while creating the
     *      {@code JAXBContext}, such as (but not limited to):
     * <ol>
     *  <li>No Jakarta XML Binding implementation was discovered
     *  <li>Classes use Jakarta XML Binding annotations incorrectly
     *  <li>Classes have colliding annotations (i.e., two classes with the same type name)
     *  <li>The Jakarta XML Binding implementation was unable to locate
     *      provider-specific out-of-band information (such as additional
     *      files generated at the development time.)
     *  <li>{@code classesToBeBound} are not open to {@code jakarta.xml.bind} module
     * </ol>
     *
     * @throws IllegalArgumentException
     *      if the parameter contains {@code null} (i.e., {@code newInstance(null);})
     *
     * @since 1.6, JAXB 2.0
     */
    public static JAXBContext newInstance( Class<?> ... classesToBeBound )
            throws JAXBException {

        return newInstance(classesToBeBound,Collections.<String,Object>emptyMap());
    }

    /**
     * Create a new instance of a {@code JAXBContext} class.
     *
     * <p>
     * An overloading of {@link JAXBContext#newInstance(Class...)}
     * to configure 'properties' for this instantiation of {@link JAXBContext}.
     *
     * <p>
     * The interpretation of properties is up to implementations. Implementations must
     * throw {@code JAXBException} if it finds properties that it doesn't understand.
     *
     * @param classesToBeBound
     *      List of java classes to be recognized by the new {@link JAXBContext}.
     *      Classes in {@code classesToBeBound} that are in named modules must be in a package
     *      that is {@code open} to at least the {@code jakarta.xml.bind} module.
     *      Can be empty, in which case a {@link JAXBContext} that only knows about
     *      spec-defined classes will be returned.
     * @param properties
     *      provider-specific properties. Can be null, which means the same thing as passing
     *      in an empty map.
     *
     * @return
     *      A new instance of a {@code JAXBContext}.
     *
     * @throws JAXBException
     *      if an error was encountered while creating the
     *      {@code JAXBContext}, such as (but not limited to):
     * <ol>
     *  <li>No Jakarta XML Binding implementation was discovered
     *  <li>Classes use Jakarta XML Binding annotations incorrectly
     *  <li>Classes have colliding annotations (i.e., two classes with the same type name)
     *  <li>The Jakarta XML Binding implementation was unable to locate
     *      provider-specific out-of-band information (such as additional
     *      files generated at the development time.)
     *  <li>{@code classesToBeBound} are not open to {@code jakarta.xml.bind} module
     * </ol>
     *
     * @throws IllegalArgumentException
     *      if the parameter contains {@code null} (i.e., {@code newInstance(null,someMap);})
     *
     * @since 1.6, JAXB 2.0
     */
    public static JAXBContext newInstance( Class<?>[] classesToBeBound, Map<String,?> properties )
            throws JAXBException {

        if (classesToBeBound == null) {
            throw new IllegalArgumentException();
        }

        // but it is an error to have nulls in it.
        for (int i = classesToBeBound.length - 1; i >= 0; i--) {
            if (classesToBeBound[i] == null) {
                throw new IllegalArgumentException();
            }
        }

        return ContextFinder.find(classesToBeBound,properties);
    }

    /**
     * Create an {@code Unmarshaller} object that can be used to convert XML
     * data into a java content tree.
     *
     * @return an {@code Unmarshaller} object
     *
     * @throws JAXBException if an error was encountered while creating the
     *                       {@code Unmarshaller} object
     */
    public abstract Unmarshaller createUnmarshaller() throws JAXBException;


    /**
     * Create a {@code Marshaller} object that can be used to convert a
     * java content tree into XML data.
     *
     * @return a {@code Marshaller} object
     *
     * @throws JAXBException if an error was encountered while creating the
     *                       {@code Marshaller} object
     */
    public abstract Marshaller createMarshaller() throws JAXBException;


    /**
     * Creates a {@code Binder} object that can be used for
     * associative/in-place unmarshalling/marshalling.
     *
     * @param domType select the DOM API to use by passing in its DOM Node class.
     *
     * @return always a new valid {@code Binder} object.
     *
     * @throws UnsupportedOperationException
     *      if DOM API corresponding to {@code domType} is not supported by
     *      the implementation.
     *
     * @since 1.6, JAXB 2.0
     */
    public <T> Binder<T> createBinder(Class<T> domType) {
        // to make JAXB 1.0 implementations work, this method must not be
        // abstract
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a {@code Binder} for W3C DOM.
     *
     * @return always a new valid {@code Binder} object.
     *
     * @since 1.6, JAXB 2.0
     */
    public Binder<Node> createBinder() {
        return createBinder(Node.class);
    }

    /**
     * Creates a {@code JAXBIntrospector} object that can be used to
     * introspect Jakarta XML Binding objects.
     *
     * @return
     *      always return a non-null valid {@code JAXBIntrospector} object.
     *
     * @throws UnsupportedOperationException
     *      Calling this method on JAXB 1.0 implementations will throw
     *      an UnsupportedOperationException.
     *
     * @since 1.6, JAXB 2.0
     */
    public JAXBIntrospector createJAXBIntrospector() {
        // to make JAXB 1.0 implementations work, this method must not be
        // abstract
        throw new UnsupportedOperationException();
    }

    /**
     * Generates the schema documents for this context.
     *
     * @param outputResolver
     *      this object controls the output to which schemas
     *      will be sent.
     *
     * @throws IOException
     *      if {@link SchemaOutputResolver} throws an {@link IOException}.
     *
     * @throws UnsupportedOperationException
     *      Calling this method on JAXB 1.0 implementations will throw
     *      an UnsupportedOperationException.
     *
     * @since 1.6, JAXB 2.0
     */
    public void generateSchema(SchemaOutputResolver outputResolver) throws IOException  {
        // to make JAXB 1.0 implementations work, this method must not be
        // abstract
        throw new UnsupportedOperationException();
    }

    private static ClassLoader getContextClassLoader() {
        if (System.getSecurityManager() == null) {
            return Thread.currentThread().getContextClassLoader();
        } else {
            return java.security.AccessController.doPrivileged(
                    new java.security.PrivilegedAction<ClassLoader>() {
                        @Override
                        public ClassLoader run() {
                            return Thread.currentThread().getContextClassLoader();
                        }
                    });
        }
    }

}
