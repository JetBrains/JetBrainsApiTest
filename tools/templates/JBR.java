package com.jetbrains;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

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
    private static final Throwable bootstrapException;
    static {
        ServiceApi a = null;
        Throwable exception = null;
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            try { // New version of bootstrap method
                Class<?> bootstrap = Class.forName("com.jetbrains.exported.JBRApi");
                a = (ServiceApi) (Object) lookup
                        .findStatic(bootstrap, "bootstrap", MethodType.methodType(Object.class, Class.class,
                                Class.class, Class.class, Class.class, Map.class, Function.class))
                        .invokeExact(ServiceApi.class, Service.class, Proxy.class, Client.class,
                                Metadata.KNOWN_EXTENSIONS, Metadata.EXTENSION_EXTRACTOR);
            } catch (IllegalAccessException | ClassNotFoundException | NoSuchMethodException | NoSuchMethodError ignore) {
                // Old version of bootstrap method
                Class<?> bootstrap = Class.forName("com.jetbrains.bootstrap.JBRApiBootstrap");
                a = (ServiceApi) (Object) lookup
                        .findStatic(bootstrap, "bootstrap", MethodType.methodType(Object.class, MethodHandles.Lookup.class))
                        .invokeExact(lookup);
            }
        }  catch (IllegalAccessException | ClassNotFoundException | NoSuchMethodException | NoSuchMethodError e) {
            exception = e;
        } catch (Throwable e) {
            Throwable t = e.getCause();
            if (t instanceof Error) throw (Error) t;
            else throw new Error(t);
        }
        api = a;
        bootstrapException = exception;
    }

    private static final String API_VERSION = getApiVersionFromModule();
    private static String getApiVersionFromModule() {
        java.lang.module.ModuleDescriptor descriptor = JBR.class.getModule().getDescriptor();
        if (descriptor != null && descriptor.version().isPresent()) {
            return descriptor.version().get().toString();
        } else {
            return "SNAPSHOT";
        }
    }

    private JBR() {}

    private static <T> T getServiceWithFallback(Class<T> interFace, FallbackSupplier<T> fallback, Extensions... extensions) {
        T service = getService(interFace, extensions);
        try {
            return service != null ? service : fallback != null ? fallback.get() : null;
        } catch (Throwable ignore) {
            return null;
        }
    }

    static <T> T getService(Class<T> interFace, Extensions... extensions) {
        return api == null ? null : api.getService(interFace, extensions);
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
     * Development versions of JBR API return "SNAPSHOT".
     * When running on Java 8, returns "UNKNOWN".
     * <h4>Note:</h4>
     * This is an API version, which comes with client application, it is *almost*
     * a compile-time constant and has nothing to do with JRE it runs on.
     * @return JBR API version in form {@code MAJOR.MINOR.PATCH}, or "SNAPSHOT" / "UNKNOWN".
     */
    public static String getApiVersion() {
        return API_VERSION;
    }

    /**
     * Checks whether given {@linkplain com.jetbrains.Extensions extension} is supported.
     * @param extension extension to check
     * @return true is extension is supported
     */
    public static boolean isExtensionSupported(Extensions extension) {
        return api != null && api.isExtensionSupported(extension);
    }

    /**
     * Internal API interface, contains most basic methods for communication between client and JBR.
     */
    @Service
    private interface ServiceApi {

        <T> T getService(Class<T> interFace);

        default <T> T getService(Class<T> interFace, Enum<?>... extensions) {
            return extensions.length == 0 ? getService(interFace) : null;
        }

        default boolean isExtensionSupported(Enum<?> extension) { return false; }
    }

    @FunctionalInterface
    private interface FallbackSupplier<T> {
        T get() throws Throwable;
    }

    // ========================== Generated metadata ==========================

    /**
     * Generated client-side metadata, needed by JBR when linking the implementation.
     */
    @SuppressWarnings({"rawtypes", "deprecation"})
    private static final class Metadata {
        // Needed only for compatibility.
        private static final String[] KNOWN_SERVICES = {"com.jetbrains.JBR$ServiceApi", /*KNOWN_SERVICES*/};
        private static final String[] KNOWN_PROXIES = {/*KNOWN_PROXIES*/};

        private static final Function<java.lang.reflect.Method, Extensions> EXTENSION_EXTRACTOR = m -> {
            Extension e = m.getAnnotation(Extension.class);
            return e == null ? null : e.value();
        };
        private static final Map<Extensions, Class[]> KNOWN_EXTENSIONS = new EnumMap<>(Extensions.class);
        static {
            /*KNOWN_EXTENSIONS*/
            for (Extensions e : Extensions.values()) KNOWN_EXTENSIONS.putIfAbsent(e, new Class[0]);
        }
    }

    // ======================= Generated static methods =======================

    /*GENERATED_METHODS*/
}
