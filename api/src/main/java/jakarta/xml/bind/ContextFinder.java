/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 2003, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

// Contributor(s):
//     Ryan Shoemaker

package jakarta.xml.bind;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This class is package private and therefore is not exposed as part of the Jakarta XML Binding API.
 * <p>
 * This code is designed to implement the XML Binding spec pluggability feature
 *
 * @see JAXBContext
 */
class ContextFinder {

    private static final Logger logger = System.getLogger("jakarta.xml.bind");

    /**
     * When JAXB is in J2SE, rt.jar has to have a JAXB implementation. However, rt.jar cannot have
     * META-INF/services/jakarta.xml.bind.JAXBContext because if it has, it will take precedence over any file that
     * applications have in their jar files.
     *
     * <p>
     * When the user bundles his own Jakarta XML Binding implementation, we'd like to use it, and we want the platform
     * default to be used only when there's no other Jakarta XML Binding provider.
     *
     * <p>
     * For this reason, we have to hard-code the class name into the API.
     */
    //XXX: should we define and rely on "default" in jakarta?
    static final String DEFAULT_FACTORY_CLASS = "org.glassfish.jaxb.runtime.v2.ContextFactory";

    private static ServiceLoaderUtil.ExceptionHandler<JAXBException> EXCEPTION_HANDLER =
            new ServiceLoaderUtil.ExceptionHandler<>() {
                @Override
                public JAXBException createException(Throwable throwable, String message) {
                    return new JAXBException(message, throwable);
                }
            };

    /**
     * If the {@linkplain InvocationTargetException} wraps an exception that shouldn't be wrapped, throw the wrapped
     * exception. Otherwise, returns exception to be wrapped for further processing.
     */
    private static Throwable handleInvocationTargetException(InvocationTargetException x) throws JAXBException {
        Throwable t = x.getTargetException();
        if (t != null) {
            if (t instanceof JAXBException)
            // one of our exceptions, just re-throw
            {
                throw (JAXBException) t;
            }
            if (t instanceof RuntimeException)
            // avoid wrapping exceptions unnecessarily
            {
                throw (RuntimeException) t;
            }
            if (t instanceof Error) {
                throw (Error) t;
            }
            return t;
        }
        return x;
    }

    /**
     * Determine if two types (JAXBContext in this case) will generate a ClassCastException.
     * <p>
     * For example, (targetType)originalType
     *
     * @param originalType The Class object of the type being cast
     * @param targetType   The Class object of the type that is being cast to
     *
     * @return JAXBException to be thrown.
     */
    private static JAXBException handleClassCastException(Class<?> originalType, Class<?> targetType) {
        final URL targetTypeURL = which(targetType);

        return new JAXBException(Messages.format(Messages.ILLEGAL_CAST,
                // we don't care where the impl class is, we want to know where JAXBContext lives in the impl
                // class' ClassLoader
                originalType.getClassLoader().getResource("jakarta/xml/bind/JAXBContext.class"),
                targetTypeURL));
    }

    /**
     * Create an instance of a class using the specified ClassLoader
     */
    static JAXBContext newInstance(String contextPath,
                                   Class<?>[] contextPathClasses,
                                   String className,
                                   ClassLoader classLoader,
                                   Map<String, ?> properties) throws JAXBException {

        try {
            Class<?> spFactory = ServiceLoaderUtil.safeLoadClass(className, DEFAULT_FACTORY_CLASS, classLoader);
            return newInstance(contextPath, contextPathClasses, spFactory, classLoader, properties);
        } catch (ClassNotFoundException x) {
            throw new JAXBException(Messages.format(Messages.DEFAULT_PROVIDER_NOT_FOUND), x);

        } catch (RuntimeException | JAXBException x) {
            // avoid wrapping RuntimeException to JAXBException,
            // because it indicates a bug in this code.
            // JAXBException re-thrown as is
            throw x;
        } catch (Exception x) {
            // can't catch JAXBException because the method is hidden behind
            // reflection.  Root element collisions detected in the call to
            // createContext() are reported as JAXBExceptions - just re-throw it
            // some other type of exception - just wrap it
            throw new JAXBException(Messages.format(Messages.COULD_NOT_INSTANTIATE, className, x), x);
        }
    }

    static JAXBContext newInstance(String contextPath,
                                   Class<?>[] contextPathClasses,
                                   Class<?> spFactory,
                                   ClassLoader classLoader,
                                   Map<String, ?> properties) throws JAXBException {

        try {

            ModuleUtil.delegateAddOpensToImplModule(contextPathClasses, spFactory);

            /*
             * jakarta.xml.bind.context.factory points to a class which has a
             * static method called 'createContext' that
             * returns a jakarta.xml.bind.JAXBContext.
             */

            Object context = null;

            // first check the method that takes Map as the third parameter.
            // this is added in 2.0.
            try {
                Method m = spFactory.getMethod("createContext", String.class, ClassLoader.class, Map.class);
                // any failure in invoking this method would be considered fatal
                Object obj = instantiateProviderIfNecessary(spFactory);
                context = m.invoke(obj, contextPath, classLoader, properties);
            } catch (NoSuchMethodException ignored) {
                // it's not an error for the provider not to have this method.
            }

            if (context == null) {
                // try the old method that doesn't take properties. compatible with 1.0.
                // it is an error for an implementation not to have both forms of the createContext method.
                Method m = spFactory.getMethod("createContext", String.class, ClassLoader.class);
                Object obj = instantiateProviderIfNecessary(spFactory);
                // any failure in invoking this method would be considered fatal
                context = m.invoke(obj, contextPath, classLoader);
            }

            if (!(context instanceof JAXBContext)) {
                // the cast would fail, so generate an exception with a nice message
                throw handleClassCastException(context.getClass(), JAXBContext.class);
            }

            return (JAXBContext) context;
        } catch (InvocationTargetException x) {
            // throw if it is exception not to be wrapped
            // otherwise, wrap with a JAXBException
            Throwable e = handleInvocationTargetException(x);
            throw new JAXBException(Messages.format(Messages.COULD_NOT_INSTANTIATE, spFactory, e), e);

        } catch (Exception x) {
            // can't catch JAXBException because the method is hidden behind
            // reflection.  Root element collisions detected in the call to
            // createContext() are reported as JAXBExceptions - just re-throw it
            // some other type of exception - just wrap it
            throw new JAXBException(Messages.format(Messages.COULD_NOT_INSTANTIATE, spFactory, x), x);
        }
    }

    private static Object instantiateProviderIfNecessary(final Class<?> implClass) throws JAXBException {
        try {
            if (JAXBContextFactory.class.isAssignableFrom(implClass)) {
                return implClass.getConstructor().newInstance();
            }
            return null;
        } catch (ReflectiveOperationException e) {
            throw new JAXBException(Messages.format(Messages.COULD_NOT_INSTANTIATE, implClass, e), e);
        }
    }

    /**
     * Create an instance of a class using passed in ClassLoader
     */
    private static JAXBContext newInstance(Class<?>[] classes, Map<String, ?> properties, String className, ClassLoader loader) throws JAXBException {

        Class<?> spi;
        try {
            spi = ServiceLoaderUtil.safeLoadClass(className, DEFAULT_FACTORY_CLASS, loader);
        } catch (ClassNotFoundException e) {
            throw new JAXBException(Messages.format(Messages.DEFAULT_PROVIDER_NOT_FOUND), e);
        }

        if (logger.isLoggable(Level.DEBUG)) {
            // extra check to avoid costly which operation if not logged
            logger.log(Level.DEBUG, "loaded {0} from {1}", new Object[]{className, which(spi)});
        }

        return newInstance(classes, properties, spi);
    }

    static JAXBContext newInstance(Class<?>[] classes,
                                   Map<String, ?> properties,
                                   Class<?> spFactory) throws JAXBException {
        try {
            ModuleUtil.delegateAddOpensToImplModule(classes, spFactory);

            Method m = spFactory.getMethod("createContext", Class[].class, Map.class);
            Object obj = instantiateProviderIfNecessary(spFactory);
            Object context = m.invoke(obj, classes, properties);
            if (!(context instanceof JAXBContext)) {
                // the cast would fail, so generate an exception with a nice message
                throw handleClassCastException(context.getClass(), JAXBContext.class);
            }
            return (JAXBContext) context;

        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new JAXBException(e);
        } catch (InvocationTargetException e) {
            // throw if it is exception not to be wrapped
            // otherwise, wrap with a JAXBException
            Throwable x = handleInvocationTargetException(e);

            throw new JAXBException(x);
        }
    }

    static JAXBContext find(String factoryId,
                            String contextPath,
                            ClassLoader classLoader,
                            Map<String, ?> properties) throws JAXBException {

        if (contextPath == null || contextPath.isEmpty()) {
            // no context is specified
            throw new JAXBException(Messages.format(Messages.NO_PACKAGE_IN_CONTEXTPATH));
        }

        //ModuleUtil is mr-jar class, scans context path for jaxb classes on jdk9 and higher
        Class<?>[] contextPathClasses = ModuleUtil.getClassesFromContextPath(contextPath, classLoader);

        String factoryName = classNameFromSystemProperties();
        if (factoryName != null) {
            return newInstance(contextPath, contextPathClasses, factoryName, classLoader, properties);
        }

        if (properties != null) {
            Object factory = properties.get(factoryId);
            if (factory != null) {
                if (factory instanceof String) {
                    factoryName = (String) factory;
                } else {
                    throw new JAXBException(Messages.format(Messages.ILLEGAL_CAST, factory.getClass().getName(), "String"));
                }
            }
            if (factoryName != null) {
                return newInstance(contextPath, contextPathClasses, factoryName, classLoader, cleanProperties(properties));
            }
        }

        JAXBContextFactory obj = ServiceLoaderUtil.firstByServiceLoader(
                JAXBContextFactory.class, logger, EXCEPTION_HANDLER, classLoader);

        if (obj != null) {
            ModuleUtil.delegateAddOpensToImplModule(contextPathClasses, obj.getClass());
            return obj.createContext(contextPath, classLoader, properties);
        }

        Iterable<Class<? extends JAXBContextFactory>> ctxFactories = ServiceLoaderUtil.lookupsUsingOSGiServiceLoader(
                JAXBContext.JAXB_CONTEXT_FACTORY, logger);

        if (ctxFactories != null) {
            for (Class<? extends JAXBContextFactory> ctxFactory : ctxFactories) {
                try {
                    return newInstance(contextPath, contextPathClasses, ctxFactory, classLoader, properties);
                } catch (Throwable t) {
                    logger.log(Level.DEBUG, () -> "Error instantiating provider " + ctxFactory, t);
                }
            }
        }

        // else no provider found
        logger.log(Level.DEBUG, "Trying to create the platform default provider");
        return newInstance(contextPath, contextPathClasses, DEFAULT_FACTORY_CLASS, classLoader, properties);
    }

    static JAXBContext find(Class<?>[] classes, ClassLoader classLoader, Map<String, ?> properties) throws JAXBException {
        String factoryClassName = classNameFromSystemProperties();
        if (factoryClassName != null) {
            return newInstance(classes, properties, factoryClassName, classLoader);
        }

        if (properties != null) {
            Object ctxFactory = properties.get(JAXBContext.JAXB_CONTEXT_FACTORY);
            if (ctxFactory != null) {
                if (ctxFactory instanceof String) {
                    factoryClassName = (String) ctxFactory;
                } else {
                    throw new JAXBException(Messages.format(Messages.ILLEGAL_CAST, ctxFactory.getClass().getName(), "String"));
                }
            }
            if (factoryClassName != null) {
                return newInstance(classes, cleanProperties(properties), factoryClassName, classLoader);
            }
        }

        JAXBContextFactory factory =
                ServiceLoaderUtil.firstByServiceLoader(JAXBContextFactory.class, logger, EXCEPTION_HANDLER, classLoader);

        if (factory != null) {
            ModuleUtil.delegateAddOpensToImplModule(classes, factory.getClass());
            return factory.createContext(classes, properties);
        }

        logger.log(Level.DEBUG, "Trying to create the platform default provider");
        Class<?> ctxFactoryClass =
                ServiceLoaderUtil.lookupUsingOSGiServiceLoader(JAXBContext.JAXB_CONTEXT_FACTORY, logger);

        if (ctxFactoryClass != null) {
            return newInstance(classes, properties, ctxFactoryClass);
        }

        // else no provider found
        logger.log(Level.DEBUG, "Trying to create the platform default provider");
        return newInstance(classes, properties, DEFAULT_FACTORY_CLASS, classLoader);
    }

    private static String classNameFromSystemProperties() throws JAXBException {

        return getSystemProperty(JAXBContext.JAXB_CONTEXT_FACTORY);
    }

    private static Map<String, ?> cleanProperties(Map<String, ?> properties) {
        // Providers are not required to understand JAXB_CONTEXT_FACTORY property,
        // and they must throw a JAXBException if they see it, so we need to remove it
        // from properties passed to them
        return properties.entrySet()
                .stream()
                .filter(Predicate.not(e -> JAXBContext.JAXB_CONTEXT_FACTORY.equals(e.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static String getSystemProperty(String property) {
        logger.log(Level.DEBUG, "Checking system property {0}", property);
        String value = System.getProperty(property);
        if (value != null) {
            logger.log(Level.DEBUG, "  found {0}", value);
        } else {
            logger.log(Level.DEBUG, "  not found");
        }
        return value;
    }

    /**
     * Search the given ClassLoader for an instance of the specified class and return a string representation of the URL
     * that points to the resource.
     *
     * @param clazz  The class to search for
     * @param loader The ClassLoader to search.  If this parameter is null, then the system class loader will be
     *               searched
     *
     * @return the URL for the class or null if it wasn't found
     */
    static URL which(Class<?> clazz, ClassLoader loader) {

        String classnameAsResource = clazz.getName().replace('.', '/') + ".class";

        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }

        return loader.getResource(classnameAsResource);
    }

    /**
     * Get the URL for the Class from it's ClassLoader.
     * <p>
     * Convenience method for {@linkplain #which(Class, ClassLoader)}.
     * <p>
     * Equivalent to calling: which(clazz, clazz.getClassLoader())
     *
     * @param clazz The class to search for
     *
     * @return the URL for the class or null if it wasn't found
     */
    static URL which(Class<?> clazz) {
        return which(clazz, clazz.getClassLoader());
    }

}
