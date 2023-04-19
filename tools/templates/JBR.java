package com.jetbrains;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;

/**
 * Entry point into JBR API.
 * Client and JBR side are linked dynamically at runtime and do not have to be of the same version.
 * In some cases (e.g. running on different JRE or old JBR) system will not be able to find
 * implementation for some services, so you'll need a fallback behavior for that case.
 * <h2>Simple usage example:</h2>
 * <blockquote><pre>{@code
 * if (JBR.isSomeServiceSupported()) {
 *     JBR.getSomeService().doSomething();
 * } else {
 *     planB();
 * }
 * }</pre></blockquote>
 * <h3>Implementation note:</h3>
 * JBR API is initialized on first access to this class (in static initializer).
 * Actual implementation is linked on demand, when corresponding service is requested by client.
 */
public final class JBR {

    private static final ServiceApi api;
    private static final Exception bootstrapException;
    static {
        ServiceApi a = null;
        Exception exception = null;
        try {
            a = (ServiceApi) Class.forName("com.jetbrains.bootstrap.JBRApiBootstrap")
                    .getMethod("bootstrap", MethodHandles.Lookup.class)
                    .invoke(null, MethodHandles.lookup());
        } catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            if (t instanceof Error) throw (Error) t;
            else throw new Error(t);
        } catch (IllegalAccessException | NoSuchMethodException | ClassNotFoundException e) {
            exception = e;
        }
        api = a;
        bootstrapException = exception;
    }

    private JBR() {}

    private static <T> T getService(Class<T> interFace, FallbackSupplier<T> fallback) {
        T service = getService(interFace);
        try {
            return service != null ? service : fallback != null ? fallback.get() : null;
        } catch (Throwable ignore) {
            return null;
        }
    }

    static <T> T getService(Class<T> interFace) {
        return api == null ? null : api.getService(interFace);
    }

    /**
     * Checks whether JBR API is available at runtime.
     * @return true when running on JBR which implements JBR API
     */
    public static boolean isAvailable() {
        return api != null;
    }

    /**
     * Returns JBR API version.
     * <h4>Note:</h4>
     * This is an API version, which comes with client application,
     * it is a compile-time constant and has nothing to do with JRE it runs on.
     * @return JBR API version in form {@code JBR.MAJOR.MINOR.PATCH}
     */
    public static String getApiVersion() {
        return "/*API_VERSION*/";
    }

    /**
     * Internal API interface, contains most basic methods for communication between client and JBR.
     */
    @Service
    private interface ServiceApi {

        <T> T getService(Class<T> interFace);
    }

    @FunctionalInterface
    private interface FallbackSupplier<T> {
        T get() throws Throwable;
    }

    // ========================== Generated metadata ==========================

    /**
     * Generated client-side metadata, needed by JBR when linking the implementation.
     */
    private static final class Metadata {
        private static final String[] KNOWN_SERVICES = {/*KNOWN_SERVICES*/};
        private static final String[] KNOWN_PROXIES = {/*KNOWN_PROXIES*/};
    }

    // ======================= Generated static methods =======================

    /*GENERATED_METHODS*/
}
