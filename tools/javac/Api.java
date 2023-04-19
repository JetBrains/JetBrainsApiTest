import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import java.io.Serializable;
import java.util.*;

/**
 * Serialized version of the module API.
 */
@SuppressWarnings("MissingSerialAnnotation")
public class Api implements Serializable {
    private static final long serialVersionUID = 1L;

    public final HashMap<Type, Type> types = new HashMap<>();

    public static class Module extends Api implements Serializable {
        private static final long serialVersionUID = 1L;

        public Version version;
        public int hash;
    }

    public static class Type extends Api implements Serializable {
        private static final long serialVersionUID = 1L;

        public final Api parent;
        public final String qualifiedName;

        public final EnumSet<Modifier> modifiers = EnumSet.noneOf(Modifier.class);
        public ElementKind kind;
        public final HashSet<String> supertypes = new HashSet<>(); // Recursive
        public TypeParameter[] typeParameters;
        public Deprecation deprecation;
        public Usage usage;
        public final HashMap<Field, Field> fields = new HashMap<>();
        public final HashMap<Method, Method> methods = new HashMap<>();

        public Type(Api parent, String qualifiedName) {
            this.parent = parent;
            this.qualifiedName = qualifiedName;
        }

        // Type is distinguishable by its name, so use it in equals and hashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return qualifiedName.equals(((Type) o).qualifiedName);
        }
        @Override
        public int hashCode() {
            return qualifiedName.hashCode();
        }
        @Override
        public String toString() {
            return qualifiedName;
        }
    }

    public static class Field implements Serializable {
        private static final long serialVersionUID = 1L;

        public final Type parent;
        public final String name;

        public final EnumSet<Modifier> modifiers = EnumSet.noneOf(Modifier.class);
        public String type;
        public Serializable constantValue;
        public Deprecation deprecation;

        public Field(Type parent, String name) {
            this.parent = parent;
            this.name = name;
        }

        // Field is distinguishable by its name, so use it in equals and hashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return name.equals(((Field) o).name);
        }
        @Override
        public int hashCode() {
            return name.hashCode();
        }
        @Override
        public String toString() {
            return type + " " + name;
        }
    }

    public static class Method implements Serializable {
        private static final long serialVersionUID = 1L;

        public final Type parent;
        public final String name;
        public final String[] parameterTypes;

        public final EnumSet<Modifier> modifiers = EnumSet.noneOf(Modifier.class);
        public String returnType;
        public HashSet<String> thrownTypes;
        public TypeParameter[] typeParameters;
        public Deprecation deprecation;

        public Method(Type parent, String name, String[] parameterTypes) {
            this.parent = parent;
            this.name = name;
            this.parameterTypes = parameterTypes;
        }

        // Method is distinguishable by its signature, so use it in equals and hashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Method methodApi = (Method) o;
            if (!name.equals(methodApi.name)) return false;
            return Arrays.equals(parameterTypes, methodApi.parameterTypes);
        }
        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + Arrays.hashCode(parameterTypes);
            return result;
        }
        @Override
        public String toString() {
            return name + "(" + String.join(", ", parameterTypes) + ")";
        }
    }

    public static class TypeParameter implements Serializable {
        private static final long serialVersionUID = 1L;
        public final String name;
        public final HashSet<String> bounds;

        public TypeParameter(String name, HashSet<String> bounds) {
            this.name = name;
            this.bounds = bounds;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TypeParameter that = (TypeParameter) o;
            if (!name.equals(that.name)) return false;
            return bounds.equals(that.bounds);
        }
        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + bounds.hashCode();
            return result;
        }
    }

    enum Deprecation {
        NONE,
        DEPRECATED,
        FOR_REMOVAL
    }

    enum Usage {
        DEFAULT(true, false), // No annotations. Non-annotated API types must either be final, or be inherited by some other type.
        SERVICE(true, false), // @Service
        PROXY(true, false), // @Proxy
        CLIENT(false, true), // @Client
        TWO_WAY(true, true); // @Proxy & @Client

        public final boolean inheritableByBackend, inheritableByClient;

        Usage(boolean inheritableByBackend, boolean inheritableByClient) {
            this.inheritableByBackend = inheritableByBackend;
            this.inheritableByClient = inheritableByClient;
        }
    }

    public static class Version implements Serializable {
        private static final long serialVersionUID = 1L;
        public final int major, minor, patch;

        public Version(int major, int minor, int patch) {
            this.major = major;
            this.minor = minor;
            this.patch = patch;
        }

        public static Version parse(String value) {
            String[] c = value.split("\\.");
            if (c.length != 3) throw new IllegalArgumentException("Invalid version format");
            return new Version(parseComponent(c[0]), parseComponent(c[1]), parseComponent(c[2]));
        }
        private static int parseComponent(String value) {
            try {
                if (value.length() > 0 && value.charAt(0) != '+') return Integer.parseUnsignedInt(value);
            } catch (NumberFormatException ignore) {}
            throw new IllegalArgumentException("Invalid version component: " + value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Version version = (Version) o;
            if (major != version.major) return false;
            if (minor != version.minor) return false;
            return patch == version.patch;
        }
        @Override
        public int hashCode() {
            int result = major;
            result = 31 * result + minor;
            result = 31 * result + patch;
            return result;
        }
        @Override
        public String toString() {
            return major + "." + minor + "." + patch;
        }
    }
}
