/**
 * JBR API is a collection of services, classes, interfaces, etc.,
 * which require tight interaction with JRE and therefore are implemented inside JBR.
 * <div>JBR API consists of two parts:</div>
 * <ul>
 *     <li>Client side - {@code jetbrains.api} module, mostly containing interfaces</li>
 *     <li>JBR side - actual implementation code inside JBR</li>
 * </ul>
 * Entry point of JBR API is the {@link com.jetbrains.JBR} class.
 * @see com.jetbrains.JBR
 */
module jetbrains.api {
    exports com.jetbrains;

    requires static transitive java.desktop;
}