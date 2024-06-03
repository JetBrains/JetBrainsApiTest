/*
 * Copyright 2000-2023 JetBrains s.r.o.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test
 * @run main JBRApiTest
 */

import com.jetbrains.Extensions;
import com.jetbrains.JBR;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JBRApiTest {

    // These services may not always be supported and usually have their own dedicated tests.
    private static final Set<String> IGNORED_SERVICES = new HashSet();

    public static void main(String[] args) throws Exception {
        IGNORED_SERVICES.add("com.jetbrains.RoundedCornersManager");
        String os = System.getProperty("os.name");
        if ("linux".equalsIgnoreCase(os)) {
            IGNORED_SERVICES.add("com.jetbrains.WindowDecorations");
        } else {
            IGNORED_SERVICES.add("com.jetbrains.WindowMove");
        }
        if (!JBR.getApiVersion().equals("SNAPSHOT") &&
            !JBR.getApiVersion().matches("\\d+\\.\\d+\\.\\d+")) throw new Error("Invalid API version: " + JBR.getApiVersion());
        if (!JBR.isAvailable()) throw new Error("JBR API is not available");
        List<String> knownServices = checkMetadata();
        testAllKnownServices(knownServices);
        testPublicServices();
        testExtensions();
    }

    private static List<String> checkMetadata() throws Exception {
        Class<?> metadata = Class.forName(JBR.class.getName() + "$Metadata");
        Field field = metadata.getDeclaredField("KNOWN_SERVICES");
        field.setAccessible(true);
        List<String> knownServices = List.of((String[]) field.get(null));
        if (!knownServices.contains("com.jetbrains.JBR$ServiceApi")) {
            throw new Error("com.jetbrains.JBR$ServiceApi was not found in known services of com.jetbrains.JBR$Metadata");
        }
        return knownServices;
    }

    private static void testAllKnownServices(List<String> knownServices) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ClassLoader classLoader = JBR.class.getClassLoader();
        Field serviceApiField = JBR.class.getDeclaredField("api");
        serviceApiField.setAccessible(true);
        Object serviceApi = serviceApiField.get(null);
        Method getServiceMethod = serviceApi.getClass().getDeclaredMethod("getService", Class.class);
        getServiceMethod.setAccessible(true);
        for (String serviceName : knownServices) {
            if (IGNORED_SERVICES.contains(serviceName)) continue;
            Class<?> serviceClass = Class.forName(serviceName, true, classLoader);
            Object service = getServiceMethod.invoke(serviceApi, serviceClass);
            if (service == null) throw new Error("Service " + serviceName + " is not supported");
        }
    }

    private static void testPublicServices() {
        Arrays.stream(JBR.class.getDeclaredMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers()) && m.getParameterCount() == 0 && m.getName().startsWith("get"))
                .forEach(m -> {
                    if (IGNORED_SERVICES.contains(m.getReturnType().getName())) return;
                    try {
                        Object service = m.invoke(null);
                        if (service == null) throw new Error(m.getName() + " returned null");
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private static void testExtensions() {
        if (System.getProperty("jetbrains.runtime.api.extensions.enabled", "true").equalsIgnoreCase("false")) return;
        for (Extensions ext : Extensions.values()) {
            if (!JBR.isExtensionSupported(ext)) {
                throw new Error("Extension " + ext.name() + " is not supported");
            }
        }
    }
}
