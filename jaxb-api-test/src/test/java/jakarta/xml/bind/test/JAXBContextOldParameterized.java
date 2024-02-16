/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/**
 * Tests for jaxb API.
 */
package jakarta.xml.bind.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

@ParameterizedTest
@CsvFileSource(resources = "/jakarta/xml/bind/test/allScenarios.csv",
    numLinesToSkip = 9,
    nullValues = "**")
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JAXBContextOldParameterized {
    
}
