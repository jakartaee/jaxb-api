/*
 * Copyright (c) 2017, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind;

import java.io.IOException;

/**
 * Intended to be overridden on JDK9, with JEP 238 multi-release class copy.
 * Contains only stubs for methods needed on JDK9 runtime.
 *
 * @author Roman Grigoriadi
 */
class ModuleUtil {

    /**
     * Resolves classes from context path.
     * Only one class per package is needed to access its {@link java.lang.Module}
     */
    static Class<?>[] getClassesFromContextPath(String contextPath, ClassLoader classLoader) throws JAXBException {
        return null;
    }

    /**
     * Find first class in package by {@code jaxb.index} file.
     */
    static Class<?> findFirstByJaxbIndex(String pkg, ClassLoader classLoader) throws IOException, JAXBException {
        return null;
    }

    /**
     * Implementation may be defined in other module than {@code jakarta.xml.bind}. In that case openness
     * {@linkplain java.lang.Module#isOpen open} of classes should be delegated to implementation module.
     *
     * @param classes used to resolve module for {@linkplain java.lang.Module#addOpens(String, java.lang.Module)}
     * @param factorySPI used to resolve {@link java.lang.Module} of the implementation.
     */
    static void delegateAddOpensToImplModule(Class<?>[] classes, Class<?> factorySPI) {
        //stub
    }

}
