import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Generates main class "JBR".
 */
public class SourceGenerator {

    private final ProcessingEnvironment processingEnv;
    private final String jbrTemplate, serviceGetterTemplate;

    public SourceGenerator(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        try {
            jbrTemplate = Files.readString(Path.of("tools/templates/JBR.java"));
            serviceGetterTemplate = Files.readString(Path.of("tools/templates/service-getter.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void generate(Round round) {
        Set<? extends Element>
                serviceElements = round.getElementsAnnotatedWith(round.annotations.service),
                proxyElements = round.getElementsAnnotatedWith(round.annotations.proxy);

        // Generate JBR class source code.
        List<String> serviceGetters = serviceElements.stream()
                .filter(e -> // Only top-level public interfaces are included.
                        e.getEnclosingElement().getKind() == ElementKind.PACKAGE &&
                        e.getModifiers().contains(Modifier.PUBLIC))
                .map(this::generateServiceGetter).toList();
        String result = replaceTemplate(jbrTemplate, "/*GENERATED_METHODS*/", serviceGetters)
                .replace("/*KNOWN_PROXIES*/", joinClassNamesToList(proxyElements))
                .replace("/*KNOWN_SERVICES*/", joinClassNamesToList(serviceElements));

        // Write generated content.
        try {
            JavaFileObject file = processingEnv.getFiler().createSourceFile("jetbrains.api/com.jetbrains.JBR",
                    Stream.concat(serviceElements.stream(), proxyElements.stream()).toArray(Element[]::new));
            try (Writer w = file.openWriter()) {
                w.write(result);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateServiceGetter(Element service) {
        boolean hasFallback = service.getEnclosedElements().stream()
                .anyMatch(e -> e.getSimpleName().contentEquals("__Fallback"));
        String javadoc = processingEnv.getElementUtils().getDocComment(service);
        if (javadoc != null) javadoc = "\n *" + javadoc.replaceAll("\n", "\n *");
        else javadoc = "";
        Deprecated deprecated = service.getAnnotation(Deprecated.class);
        String deprecation;
        if (deprecated == null) deprecation = "";
        else if (!deprecated.forRemoval()) deprecation = "\n" + deprecated;
        else deprecation = "\n@SuppressWarnings(\"removal\")\n" + deprecated;
        return serviceGetterTemplate
            .replace("<FALLBACK>", hasFallback ? "$.__Fallback::new" : "null")
            .replaceAll("\\$", service.getSimpleName().toString())
            .replace("<JAVADOC>", javadoc)
            .replaceAll("<DEPRECATED>", deprecation);
    }

    private String joinClassNamesToList(Set<? extends Element> elements) {
        return elements.stream()
                .map(e -> "\"" + processingEnv.getElementUtils().getBinaryName((TypeElement) e) + "\"")
                .collect(Collectors.joining(", "));
    }

    private static String replaceTemplate(String src, String placeholder, Iterable<String> statements) {
        int placeholderIndex = src.indexOf(placeholder);
        int indent = 0;
        while (placeholderIndex - indent >= 1 && src.charAt(placeholderIndex - indent - 1) == ' ') indent++;
        int nextLineIndex = src.indexOf('\n', placeholderIndex + placeholder.length()) + 1;
        if (nextLineIndex == 0) nextLineIndex = placeholderIndex + placeholder.length();
        String before = src.substring(0, placeholderIndex - indent), after = src.substring(nextLineIndex);
        StringBuilder sb = new StringBuilder(before);
        boolean firstStatement = true;
        for (String s : statements) {
            if (!firstStatement) sb.append('\n');
            sb.append(s.indent(indent));
            firstStatement = false;
        }
        sb.append(after);
        return sb.toString();
    }

}
