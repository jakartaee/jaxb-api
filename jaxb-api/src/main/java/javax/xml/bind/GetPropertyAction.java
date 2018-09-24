/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package javax.xml.bind;

import java.security.PrivilegedAction;

/**
 * {@link PrivilegedAction} that gets the system property value.
 * @author Kohsuke Kawaguchi
 */
final class GetPropertyAction implements PrivilegedAction<String> {
    private final String propertyName;

    public GetPropertyAction(String propertyName) {
        this.propertyName = propertyName;
    }

    public String run() {
        return System.getProperty(propertyName);
    }
}
