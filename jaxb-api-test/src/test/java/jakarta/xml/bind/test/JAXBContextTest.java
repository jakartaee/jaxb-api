/*
 * Copyright (c) 2003, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind.test;

import jaxb.test.usr.A;

import jakarta.xml.bind.JAXBContext;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.opentest4j.AssertionFailedError;

/*
 * test for JDK-8131334: SAAJ Plugability Layer: using java.util.ServiceLoader
 *
 * There are unsafe scenarios not to be run within the build (modifying jdk files).
 * To run those, following needs to be done:
 *   1. allow java to write into $JAVA_HOME/conf: mkdir $JAVA_HOME/conf; chmod a+rw $JAVA_HOME/conf
 *   2. use "runUnsafe" property: mvn clean test -DrunUnsafe=true
 */
public class JAXBContextTest {

    static final Logger logger = Logger.getLogger(JAXBContextTest.class.getName());

    static final Boolean skipUnsafe = !Boolean.getBoolean("runUnsafe");

    // test configuration ------------------------------------------

    // test-classes directory (required for setup and for security settings)
    static final String classesDir = JAXBContextTest.class.getProtectionDomain().getCodeSource().getLocation()
            .getFile();

    static {
        System.setProperty("classesDir", classesDir);
    }

    // configuration to be created by the test
    static Path providersDir = Paths.get(classesDir, "META-INF", "services");
    static Path providersFileLegacy = providersDir.resolve("jakarta.xml.bind.JAXBContext");
    static Path providersFile = providersDir.resolve("jakarta.xml.bind.JAXBContextFactory");

    // configuration to be created by the test
    static Path jaxbPropsDir = Paths.get(classesDir, "jaxb", "test", "usr");
    static Path jaxbPropsFile = jaxbPropsDir.resolve("jaxb.properties");

    // test instance -----------------------------------------------

    // scenario name - just for logging
    String scenario;

    // properties to pass to JAXBContext#newContext
    private Map<String, ?> properties;

    // java policy file for testing w/security manager
    private String expectedFactory;
    private Class<?> expectedException;

    void setup(String scenario,
            String jaxbPropertiesClass,
            String spiClass,
            String expectedFactory,
            Class<?> expectedException,
            String systemProperty,
            String mapEntryKey,
            String mapEntryValue) throws IOException {
        // ensure setup may be done ...
        System.setSecurityManager(null);

        if (systemProperty != null) {
            System.setProperty("jakarta.xml.bind.JAXBContextFactory", systemProperty);
        } else {
            System.clearProperty("jakarta.xml.bind.JAXBContextFactory");
        }

        if (mapEntryKey != null && mapEntryValue != null) {
            properties = Map.of(mapEntryKey, mapEntryValue);
        } else {
            properties = Map.of();
        }

        this.scenario = scenario;
        this.expectedFactory = expectedFactory;
        this.expectedException = expectedException;

        if (skipUnsafe && scenario.startsWith("unsafe")) {
            log("Skipping unsafe scenario:" + scenario);
            return;
        }

        prepare(jaxbPropertiesClass, spiClass);
    }

    @JAXBContextOldParameterized
    void testPath(String scenario,
            String jaxbPropertiesClass,
            String spiClass,
            String expectedFactory,
            Class<?> expectedException,
            String systemProperty,
            String mapEntryKey,
            String mapEntryValue) throws IOException {
        setup(scenario,
                jaxbPropertiesClass,
                spiClass,
                expectedFactory,
                expectedException,
                systemProperty,
                mapEntryKey,
                mapEntryValue);
        logConfigurations();
        try {
            JAXBContext ctx = JAXBContext.newInstance("jaxb.test.usr");
            handleResult(ctx);
        } catch (Throwable throwable) {
            handleThrowable(throwable);
        } finally {
            doFinally();
        }
    }

    @JAXBContextOldParameterized
    public void testClasses(String scenario,
            String jaxbPropertiesClass,
            String spiClass,
            String expectedFactory,
            Class<?> expectedException,
            String systemProperty,
            String mapEntryKey,
            String mapEntryValue) throws IOException {
        setup(scenario,
                jaxbPropertiesClass,
                spiClass,
                expectedFactory,
                expectedException,
                systemProperty,
                mapEntryKey,
                mapEntryValue);
        logConfigurations();
        try {
            JAXBContext ctx = JAXBContext.newInstance(new Class<?>[] { A.class }, properties);
            handleResult(ctx);
        } catch (Throwable throwable) {
            handleThrowable(throwable);
        } finally {
            doFinally();
        }
    }

    @JAXBContextOldParameterized
    public void testClass(String scenario,
            String jaxbPropertiesClass,
            String spiClass,
            String expectedFactory,
            Class<?> expectedException,
            String systemProperty,
            String mapEntryKey,
            String mapEntryValue) throws IOException {
        setup(scenario,
                jaxbPropertiesClass,
                spiClass,
                expectedFactory,
                expectedException,
                systemProperty,
                mapEntryKey,
                mapEntryValue);
        logConfigurations();
        try {
            JAXBContext ctx = JAXBContext.newInstance(A.class);
            handleResult(ctx);
        } catch (Throwable throwable) {
            handleThrowable(throwable);
        } finally {
            doFinally();
        }
    }

    private void handleResult(JAXBContext ctx) {
        Assertions.assertTrue(null != ctx, "No ctx found.");
        log("     TEST: context class = [" + ctx.getClass().getName() + "]\n");
        String className = ctx.getClass().getName();
        Assertions.assertTrue(className.equals(expectedFactory),
                "Incorrect ctx: [" + className + "], Expected: [" + expectedFactory + "]");

        log(" TEST PASSED");
    }

    private void handleThrowable(Throwable throwable) {
        if (throwable instanceof AssertionFailedError)
            throw ((AssertionFailedError) throwable);
        Class<?> throwableClass = throwable.getClass();
        boolean correctException = throwableClass.equals(expectedException);
        if (!correctException) {
            throwable.printStackTrace();
        }
        if (expectedException == null) {
            throw new AssertionFailedError("Unexpected exception:" + throwableClass);
        }
        Assertions.assertTrue(correctException,
                "Got unexpected exception: [" + throwableClass + "], expected: [" + expectedException + "]");
        log(" TEST PASSED");
    }

    private void doFinally() {
        cleanResource(providersFile);
        // cleanResource(providersDir);

        // unsafe; not running:
        cleanResource(jaxbPropsFile);
        System.setSecurityManager(null);
    }

    @JAXBContextOldParameterized
    public void testPathSM(String scenario,
            String jaxbPropertiesClass,
            String spiClass,
            String expectedFactory,
            Class<?> expectedException,
            String systemProperty,
            String mapEntryKey,
            String mapEntryValue) throws IOException {
        enableSM();
        testPath(scenario,
                jaxbPropertiesClass,
                spiClass,
                expectedFactory,
                expectedException,
                systemProperty,
                mapEntryKey,
                mapEntryValue);
    }

    @JAXBContextOldParameterized
    public void testClassSM(String scenario,
            String jaxbPropertiesClass,
            String spiClass,
            String expectedFactory,
            Class<?> expectedException,
            String systemProperty,
            String mapEntryKey,
            String mapEntryValue) throws IOException {
        enableSM();
        testClass(scenario,
                jaxbPropertiesClass,
                spiClass,
                expectedFactory,
                expectedException,
                systemProperty,
                mapEntryKey,
                mapEntryValue);
    }

    @JAXBContextOldParameterized
    public void testClassesSM(String scenario,
            String jaxbPropertiesClass,
            String spiClass,
            String expectedFactory,
            Class<?> expectedException,
            String systemProperty,
            String mapEntryKey,
            String mapEntryValue) throws IOException {
        enableSM();
        testClasses(scenario,
                jaxbPropertiesClass,
                spiClass,
                expectedFactory,
                expectedException,
                systemProperty,
                mapEntryKey,
                mapEntryValue);
    }

    private void enableSM() {
        System.setSecurityManager(null);
        System.setProperty("java.security.policy", classesDir + "jakarta/xml/bind/test.policy");
        // System.setSecurityManager(new SecurityManager());
    }

    private void cleanResource(Path resource) {
        try {
            if (Files.exists(resource)) {
                Files.deleteIfExists(resource);
            }
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }
    }

    private void prepare(String propertiesClassName, String providerClassName) {

        try {
            log("providerClassName = " + providerClassName);
            log("propertiesClassName = " + propertiesClassName);

            cleanResource(providersFile);
            cleanResource(providersFileLegacy);
            if (scenario.contains("legacy-svc")) {
                setupFile(providersFileLegacy, providersDir, providerClassName);
            } else {
                setupFile(providersFile, providersDir, providerClassName);
            }

            // unsafe; not running:
            if (propertiesClassName != null) {
                setupFile(jaxbPropsFile, jaxbPropsDir, propertiesClassName);
            } else {
                cleanResource(jaxbPropsFile);
            }

            log(" SETUP OK.");

        } catch (IOException e) {
            log(" SETUP FAILED.");
            e.printStackTrace();
        }
    }

    private void logConfigurations() throws IOException {
        logFile(providersFile);
        logFile(providersFileLegacy);
        logFile(jaxbPropsFile);
    }

    private void logFile(Path path) throws IOException {
        if (Files.exists(path)) {
            log("File [" + path + "] exists: [");
            log(new String(Files.readAllBytes(path)));
            log("]");
        }
    }

    private void setupFile(Path file, Path dir, String value) throws IOException {
        cleanResource(file);
        if (value != null) {
            log("writing configuration [" + value + "] into file [" + file.toAbsolutePath() + "]");
            Files.createDirectories(dir);
            Files.write(
                    file,
                    value.getBytes(),
                    StandardOpenOption.CREATE);
        }
    }

    private void log(String msg) {
        logger.info("[" + scenario + "] " + msg);
        // System.out.println("[" + scenario + "] " + msg);
    }

}
