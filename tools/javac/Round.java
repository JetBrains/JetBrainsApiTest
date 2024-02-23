import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Annotation processing round context.
 */
public class Round {

    public final RoundEnvironment env;
    public final Annotations annotations = new Annotations();

    public Round(RoundEnvironment env) {
        this.env = env;
    }

    /**
     * Null-safe version of {@link RoundEnvironment#getElementsAnnotatedWith(TypeElement)}.
     */
    public Set<? extends Element> getElementsAnnotatedWith(TypeElement a) {
        return a == null ? Set.of() : env.getElementsAnnotatedWith(a);
    }

    public String getExtensionName(Element element) {
        if (annotations.extension == null) return null;
        for (AnnotationMirror annotation : element.getAnnotationMirrors()) {
            if (annotation.getAnnotationType().asElement().equals(annotations.extension)) {
                AnnotationValue value = annotation.getElementValues().get(annotations.extensionValue);
                return value.getValue().toString();
            }
        }
        return null;
    }

    public static class Annotations {
        public TypeElement service, proxy, client, extension;
        public ExecutableElement extensionValue;
    }
}
