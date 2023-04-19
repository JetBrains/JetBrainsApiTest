import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
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

    public static class Annotations {
        public TypeElement service, proxy, client;
    }
}
