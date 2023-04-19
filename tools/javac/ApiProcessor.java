import com.sun.source.util.JavacTask;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;

@SupportedOptions({"output", "version"})
@SupportedSourceVersion(SourceVersion.RELEASE_9)
@SupportedAnnotationTypes("*")
public class ApiProcessor extends AbstractProcessor {

    private Path output;
    private Api.Version referenceVersion;
    private Api.Module oldApi;
    private SourceGenerator sourceGenerator;
    private ApiCollector apiCollector;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        output = Path.of(Objects.requireNonNull(processingEnv.getOptions().get("output"), "-Aoutput option is missing"));
        String version = processingEnv.getOptions().get("version");
        if (version != null) referenceVersion = Api.Version.parse(version);
        else {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("api-blob"))) {
                oldApi = (Api.Module) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            referenceVersion = oldApi.version;
        }

        sourceGenerator = new SourceGenerator(processingEnv, referenceVersion.toString());
        apiCollector = new ApiCollector(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        // Find our annotation elements.
        Round round = new Round(roundEnvironment);
        for (TypeElement e : set) {
            switch (e.getQualifiedName().toString()) {
                case "com.jetbrains.Service": round.annotations.service = e; break;
                case "com.jetbrains.Proxy":   round.annotations.proxy = e;   break;
                case "com.jetbrains.Client":  round.annotations.client = e;  break;
            }
        }

        // Generate sources on first round.
        if (sourceGenerator != null) {
            sourceGenerator.generate(round);
            sourceGenerator = null;
        }

        // Collect API info.
        Api.Module newApi = apiCollector.collect(round);
        if (newApi == null) return true;
        try {
            String message, outputVersion;
            if (oldApi == null) {
                // Override API version from options
                newApi.version = referenceVersion;
                message = "\u2757 Skipping API checks, version override specified: " + referenceVersion + "\n";
                outputVersion = referenceVersion.toString();
            } else {
                // Compare API changes
                ApiComparator.Node result = ApiComparator.compare(oldApi, newApi);
                StringBuilder out = new StringBuilder();
                ApiComparator.Compatibility compatibility = result.traverse(out);
                newApi.version = compatibility.incrementVersion(oldApi.version);
                if (compatibility == ApiComparator.Compatibility.SAME) {
                    // Do not print anything if there were no changes.
                    message = null;
                    outputVersion = referenceVersion.toString();
                } else {
                    // Print details.
                    if (compatibility == ApiComparator.Compatibility.MAJOR) {
                        out.append("\u2757 There are major changes which require extra attention, they are marked with \"\u2757\".\n");
                    }
                    out.append("Compatibility status of API changes: ").append(compatibility).append(' ');
                    switch (compatibility) {
                        case MAJOR: out.append("\uD83E\uDD2F"); break;
                        case MINOR: out.append("\uD83D\uDD27"); break;
                        case PATCH: out.append("\uD83D\uDC85"); break;
                    }
                    out.append("\nVersion increment: ").append(oldApi.version).append(" -> ").append(newApi.version).append('\n');
                    message = out.toString();
                    outputVersion = "SNAPSHOT";
                }
            }
            if (message != null) {
                // Get rid of unicode symbols when printing to stdout.
                System.out.println(message.replaceAll("\u2757", "!!!").replaceAll("[^\\x00-\\x7F]", "").stripTrailing());
            }

            // Save metadata
            Files.createDirectories(output);
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(output.resolve("api-blob")))) {
                out.writeObject(newApi);
            }
            Files.writeString(output.resolve("version.txt"), outputVersion);
            Files.writeString(output.resolve("message.txt"), message != null ? message : "");
            Files.write(output.resolve("sourcelist8.txt"), apiCollector.getJava8CompilationUnitPaths());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
