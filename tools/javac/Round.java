import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
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

    private static String getAnnotationValueAsString(TypeElement annotationType, ExecutableElement annotationValue, Element element) {
        if (annotationType == null) return null;
        for (AnnotationMirror annotation : element.getAnnotationMirrors()) {
            if (annotation.getAnnotationType().asElement().equals(annotationType)) {
                AnnotationValue value = annotation.getElementValues().get(annotationValue);
                return value.getValue().toString();
            }
        }
        return null;
    }

    public String getExtensionName(Element element) {
        return getAnnotationValueAsString(annotations.extension, annotations.extensionValue, element);
    }

    public String getFallbackName(Element element) {
        return getAnnotationValueAsString(annotations.fallback, annotations.fallbackValue, element);
    }

    public static class Annotations {
        public TypeElement service, provided, provides, fallback, extension;
        public ExecutableElement extensionValue, fallbackValue;
    }
}
