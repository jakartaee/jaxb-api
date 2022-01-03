/*
 * Copyright (c) 2015, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Shared ServiceLoader/FactoryFinder Utils shared among Jakarta SOAP with Attachments, Jakarta XML Binding
 * and Jakarta XML Web Services.
 * Class duplicated to all those projects.
 *
 * @author Miroslav.Kos@oracle.com
 */
class ServiceLoaderUtil {

    private static final String OSGI_SERVICE_LOADER_CLASS_NAME = "org.glassfish.hk2.osgiresourcelocator.ServiceLoader";
    private static final String OSGI_SERVICE_LOADER_METHOD_NAME = "lookupProviderClasses";

    static <P, T extends Exception> P firstByServiceLoader(Class<P> spiClass,
                                                           Logger logger,
                                                           ExceptionHandler<T> handler) throws T {
        // service discovery
        try {
            ServiceLoader<P> serviceLoader = ServiceLoader.load(spiClass);

            for (P impl : serviceLoader) {
                logger.log(Level.FINE, "ServiceProvider loading Facility used; returning object [{0}]",
                        impl.getClass().getName());

                return impl;
            }
        } catch (Throwable t) {
            throw handler.createException(t, "Error while searching for service [" + spiClass.getName() + "]");
        }
        return null;
    }

    static <T> T lookupUsingOSGiServiceLoader(String factoryId, Logger logger) {

        try {
            // Use reflection to avoid having any dependency on ServiceLoader class
            @SuppressWarnings("unchecked")
            Class<? extends T> serviceClass = (Class<? extends T>) Class.forName(factoryId);
            Class<?> target = Class.forName(OSGI_SERVICE_LOADER_CLASS_NAME);
            Method m = target.getMethod(OSGI_SERVICE_LOADER_METHOD_NAME, Class.class);
            @SuppressWarnings("unchecked")
            Iterator<? extends T> iter = ((Iterable<? extends T>) m.invoke(null, serviceClass)).iterator();
            if (iter.hasNext()) {
                T next = iter.next();
                logger.log(Level.FINE, "Found implementation using OSGi facility; returning object [{0}].",
                        next.getClass().getName());
                return next;
            } else {
                return null;
            }
        } catch (IllegalAccessException |
                InvocationTargetException |
                ClassNotFoundException |
                NoSuchMethodException ex) {

            logger.log(Level.FINE, "Unable to find from OSGi: [" + factoryId + "]", ex);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    static <T> Iterable<T> lookupsUsingOSGiServiceLoader(String factoryId, Logger logger) {
        try {
            // Use reflection to avoid having any dependency on ServiceLoader class
            return (Iterable<T>)
                Class.forName(OSGI_SERVICE_LOADER_CLASS_NAME)
                     .getMethod(OSGI_SERVICE_LOADER_METHOD_NAME, Class.class)
                     .invoke(null, Class.forName(factoryId));

        } catch (IllegalAccessException |
                InvocationTargetException |
                ClassNotFoundException |
                NoSuchMethodException ex) {

            logger.log(Level.FINE, ex, () -> "Unable to find from OSGi: [" + factoryId + "]");
            return null;
        }
    }

    static void checkPackageAccess(String className) {
        // make sure that the current thread has an access to the package of the given name.
        SecurityManager s = System.getSecurityManager();
        if (s != null) {
            int i = className.lastIndexOf('.');
            if (i != -1) {
                s.checkPackageAccess(className.substring(0, i));
            }
        }
    }

    static Class<?> nullSafeLoadClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
        if (classLoader == null) {
            return Class.forName(className);
        } else {
            return classLoader.loadClass(className);
        }
    }

    // Returns instance of required class. It checks package access (security)
    // unless it is defaultClassname. It means if you are trying to instantiate
    // default implementation (fallback), pass the class name to both first and second parameter.
    static <T extends Exception> Object newInstance(String className,
                                                    String defaultImplClassName,
                                                    final ExceptionHandler<T> handler) throws T {
        try {
            return safeLoadClass(className, defaultImplClassName, contextClassLoader(handler)).getConstructor().newInstance();
        } catch (ClassNotFoundException x) {
            throw handler.createException(x, "Provider " + className + " not found");
        } catch (Exception x) {
            throw handler.createException(x, "Provider " + className + " could not be instantiated: " + x);
        }
    }

    static Class<?> safeLoadClass(String className,
                               String defaultImplClassName,
                               ClassLoader classLoader) throws ClassNotFoundException {

        try {
            checkPackageAccess(className);
        } catch (SecurityException se) {
            // anyone can access the platform default factory class without permission
            if (defaultImplClassName != null && defaultImplClassName.equals(className)) {
                return Class.forName(className);
            }
            // not platform default implementation ...
            throw se;
        }
        return nullSafeLoadClass(className, classLoader);
    }

    static <T extends Exception> ClassLoader contextClassLoader(ExceptionHandler<T> exceptionHandler) throws T {
        try {
            return Thread.currentThread().getContextClassLoader();
        } catch (Exception x) {
            throw exceptionHandler.createException(x, x.toString());
        }
    }

    static abstract class ExceptionHandler<T extends Exception> {

        public abstract T createException(Throwable throwable, String message);

    }

}
