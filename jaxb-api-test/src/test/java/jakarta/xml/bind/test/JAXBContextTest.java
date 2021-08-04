/*
 * Copyright (c) 2003, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind.test;


import jaxb.test.usr.A;
import junit.framework.AssertionFailedError;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import jakarta.xml.bind.JAXBContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;

import static junit.framework.TestCase.assertTrue;

/*
 * test for JDK-8131334: SAAJ Plugability Layer: using java.util.ServiceLoader
 *
 * There are unsafe scenarios not to be run within the build (modifying jdk files).
 * To run those, following needs to be done:
 *   1. allow java to write into $JAVA_HOME/conf: mkdir $JAVA_HOME/conf; chmod a+rw $JAVA_HOME/conf
 *   2. use "runUnsafe" property: mvn clean test -DrunUnsafe=true
 */
@RunWith(Parameterized.class)
public class JAXBContextTest {

    static final Logger logger = Logger.getLogger(JAXBContextTest.class.getName());

    static final Boolean skipUnsafe = !Boolean.getBoolean("runUnsafe");

    // test configuration ------------------------------------------

    // test-classes directory (required for setup and for security settings)
    static final String classesDir = JAXBContextTest.class.getProtectionDomain().getCodeSource().getLocation().getFile();
    private static final String FACTORY_ID_LEGACY = "jakarta.xml.bind.context.factory";
    private static final String FACTORY_ID = "jakarta.xml.bind.JAXBContextFactory";
    private static final String PACKAGE_LEGACY = "jaxb.factory.legacy."; // TODO: ???
    private static final String PACKAGE_SPI = "jaxb.factory.spi."; // TODO: ???
    private static final Object DEFAULT = "com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl";


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

    // java policy file for testing w/security manager
    private String expectedFactory;
    private Class<?> expectedException;

    // Broken configurations were commented out.
    @Parameterized.Parameters
    public static Collection configurations() {
        return Arrays.asList(new Object[][]{
                // scenario-name, jaxb.properties, svc, arg1, arg2, system-props
//                {"scenario-1", FACTORY_ID_LEGACY + "="+PACKAGE_LEGACY+"Valid", null, PACKAGE_LEGACY+"Valid$JAXBContext1", null, null},
                {"scenario-3", FACTORY_ID_LEGACY + "=non.existing.FactoryClass", null, null, jakarta.xml.bind.JAXBException.class, null},
                {"scenario-4", FACTORY_ID_LEGACY + "="+PACKAGE_LEGACY+"Invalid", null, null, jakarta.xml.bind.JAXBException.class, null},
//                {"scenario-13", FACTORY_ID_LEGACY + "="+PACKAGE_LEGACY+"Valid", PACKAGE_LEGACY+"Valid2", PACKAGE_LEGACY+"Valid$JAXBContext1", null, PACKAGE_LEGACY+"Valid3"},

//                {"scenario-1", FACTORY_ID_LEGACY + "="+PACKAGE_SPI+"Valid", null, PACKAGE_SPI+"Valid$JAXBContext1", null, null},
                {"scenario-3", FACTORY_ID_LEGACY + "=non.existing.FactoryClass", null, null, jakarta.xml.bind.JAXBException.class, null},
                {"scenario-4", FACTORY_ID_LEGACY + "="+PACKAGE_SPI+"Invalid", null, null, jakarta.xml.bind.JAXBException.class, null},
//                {"scenario-13", FACTORY_ID_LEGACY + "="+PACKAGE_SPI+"Valid", PACKAGE_SPI+"Valid2", PACKAGE_SPI+"Valid$JAXBContext1", null, PACKAGE_SPI+"Valid3"},

//                {"scenario-1", FACTORY_ID + "="+PACKAGE_SPI+"Valid", null, PACKAGE_SPI+"Valid$JAXBContext1", null, null},
                {"scenario-3", FACTORY_ID + "=non.existing.FactoryClass", null, null, jakarta.xml.bind.JAXBException.class, null},
                {"scenario-4", FACTORY_ID + "="+PACKAGE_SPI+"Invalid", null, null, jakarta.xml.bind.JAXBException.class, null},
//                {"scenario-13", FACTORY_ID + "="+PACKAGE_SPI+"Valid", PACKAGE_SPI+"Valid2", PACKAGE_SPI+"Valid$JAXBContext1", null, PACKAGE_SPI+"Valid3"},

//                {"scenario-1", FACTORY_ID + "="+PACKAGE_LEGACY+"Valid", null, PACKAGE_LEGACY+"Valid$JAXBContext1", null, null},
                {"scenario-3", FACTORY_ID + "=non.existing.FactoryClass", null, null, jakarta.xml.bind.JAXBException.class, null},
                {"scenario-4", FACTORY_ID + "="+PACKAGE_LEGACY+"Invalid", null, null, jakarta.xml.bind.JAXBException.class, null},
//                {"scenario-13", FACTORY_ID + "="+PACKAGE_LEGACY+"Valid", PACKAGE_LEGACY+"Valid2", PACKAGE_LEGACY+"Valid$JAXBContext1", null, PACKAGE_LEGACY+"Valid3"},


                {"scenario-2", "something=AnotherThing", null, null, jakarta.xml.bind.JAXBException.class, null},

                // service loader
                {"scenario-8", null, PACKAGE_SPI+"Valid\n", PACKAGE_SPI+"Valid$JAXBContext1", null, null},
                {"scenario-9", null, PACKAGE_SPI+"Valid", PACKAGE_SPI+"Valid$JAXBContext1", null, null},
                {"scenario-11", null, PACKAGE_SPI+"Invalid", null, jakarta.xml.bind.JAXBException.class, null},
                {"scenario-15", null, PACKAGE_SPI+"Valid", PACKAGE_SPI+"Valid$JAXBContext1", null, null},

                // service loader - legacy
//                {"scenario-8 legacy-svc", null, PACKAGE_SPI+"Valid\n", PACKAGE_SPI+"Valid$JAXBContext1", null, null},
//                {"scenario-9 legacy-svc", null, PACKAGE_SPI+"Valid", PACKAGE_SPI+"Valid$JAXBContext1", null, null},
                {"scenario-11 legacy-svc", null, PACKAGE_SPI+"Invalid", null, jakarta.xml.bind.JAXBException.class, null},
//                {"scenario-15 legacy-svc", null, PACKAGE_SPI+"Valid", PACKAGE_SPI+"Valid$JAXBContext1", null, null},

                // service loader - legacy
//                {"scenario-8 legacy-svc", null, PACKAGE_LEGACY+"Valid\n", PACKAGE_LEGACY+"Valid$JAXBContext1", null, null},
//                {"scenario-9 legacy-svc", null, PACKAGE_LEGACY+"Valid", PACKAGE_LEGACY+"Valid$JAXBContext1", null, null},
                {"scenario-11 legacy-svc", null, PACKAGE_LEGACY+"Invalid", null, jakarta.xml.bind.JAXBException.class, null},
//                {"scenario-15 legacy-svc", null, PACKAGE_LEGACY+"Valid", PACKAGE_LEGACY+"Valid$JAXBContext1", null, null},

                // system property
                {"scenario-5", null, null, PACKAGE_SPI+"Valid$JAXBContext1", null, PACKAGE_SPI+"Valid"},
                {"scenario-7", null, null, null, jakarta.xml.bind.JAXBException.class, PACKAGE_SPI+"Invalid"},
                {"scenario-14", null, PACKAGE_SPI+"Valid2", PACKAGE_SPI+"Valid$JAXBContext1", null, PACKAGE_SPI+"Valid"},

                {"scenario-5", null, null, PACKAGE_LEGACY+"Valid$JAXBContext1", null, PACKAGE_LEGACY+"Valid"},
                {"scenario-7", null, null, null, jakarta.xml.bind.JAXBException.class, PACKAGE_LEGACY+"Invalid"},
                {"scenario-14", null, PACKAGE_LEGACY+"Valid2", PACKAGE_LEGACY+"Valid$JAXBContext1", null, PACKAGE_LEGACY+"Valid"},
                {"scenario-6", null, null, null, jakarta.xml.bind.JAXBException.class, "jaxb.factory.NonExisting"},

                {"scenario-10", null, "jaxb.factory.NonExisting", null, jakarta.xml.bind.JAXBException.class, null},

                {"scenario-12", null, null, DEFAULT, jakarta.xml.bind.JAXBException.class, null},
        });
    }

    // scenario-name, jaxb.properties, svc, arg1, arg2, system-props
    public JAXBContextTest(
            String scenario,
            String jaxbPropertiesClass,
            String spiClass,
            String expectedFactory,
            Class<?> expectedException,
            String systemProperty
    ) {

        // ensure setup may be done ...
        System.setSecurityManager(null);

        if (systemProperty != null) {
            System.setProperty("jakarta.xml.bind.JAXBContextFactory", systemProperty);
        } else {
            System.clearProperty("jakarta.xml.bind.JAXBContextFactory");
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

    @Test
    public void testPath() throws IOException {
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

    @Test
    public void testClasses() throws IOException {
        logConfigurations();
        try {
            JAXBContext ctx = JAXBContext.newInstance(new Class[] {A.class}, null);
            handleResult(ctx);
        } catch (Throwable throwable) {
            handleThrowable(throwable);
        } finally {
            doFinally();
        }
    }

    @Test
    public void testClass() throws IOException {
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
        assertTrue("No ctx found.", ctx != null);
        log("     TEST: context class = [" + ctx.getClass().getName() + "]\n");
        String className = ctx.getClass().getName();
        assertTrue("Incorrect ctx: [" + className + "], Expected: [" + expectedFactory + "]",
                className.equals(expectedFactory));

        log(" TEST PASSED");
    }

    private void handleThrowable(Throwable throwable) {
        if (throwable instanceof AssertionFailedError) throw ((AssertionFailedError)throwable);
        Class<?> throwableClass = throwable.getClass();
        boolean correctException = throwableClass.equals(expectedException);
        if (!correctException) {
            throwable.printStackTrace();
        }
        if (expectedException == null) {
            throw new AssertionFailedError("Unexpected exception:" + throwableClass);
        }
        assertTrue("Got unexpected exception: [" + throwableClass + "], expected: [" + expectedException + "]",
                correctException);
        log(" TEST PASSED");
    }

    private void doFinally() {
        cleanResource(providersFile);
        //cleanResource(providersDir);

        // unsafe; not running:
        cleanResource(jaxbPropsFile);
        System.setSecurityManager(null);
    }

    @Test
    public void testPathSM() throws IOException {
        enableSM();
        testPath();
    }

    @Test
    public void testClassSM() throws IOException {
        enableSM();
        testClass();
    }

    @Test
    public void testClassesSM() throws IOException {
        enableSM();
        testClasses();
    }


    private void enableSM() {
        System.setSecurityManager(null);
        System.setProperty("java.security.policy", classesDir + "jakarta/xml/bind/test.policy");
        System.setSecurityManager(new SecurityManager());
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
//        System.out.println("[" + scenario + "] " + msg);
    }

}


