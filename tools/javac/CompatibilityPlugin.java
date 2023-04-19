import com.sun.source.tree.AnnotationTree;
import com.sun.source.util.*;
import com.sun.tools.javac.tree.JCTree;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Magically makes @Deprecated annotations with #forRemoval
 * and #since available from Java 9 compile for Java 8.
 * Also removes Java 9 classes which are duplicating existing Java 8 classes.
 */
public class CompatibilityPlugin implements Plugin {

    @Override
    public String getName() {
        return "CompatibilityPlugin";
    }

    @Override
    public void init(JavacTask task, String... args) {
        task.addTaskListener(new TaskListener() {
            public void finished(TaskEvent e) {
                if (e.getKind() == TaskEvent.Kind.PARSE) {
                    e.getCompilationUnit().accept(scanner, null);
                } else if (e.getKind() == TaskEvent.Kind.COMPILATION) {
                    if (args.length >= 2) removeDuplicates(args[0], args[1]);
                }
            }
        });
    }

    private static final TreeScanner<Void, Void> scanner = new TreeScanner<>() {
        @Override
        public Void visitAnnotation(AnnotationTree node, Void unused) {
            switch (node.getAnnotationType().toString()) {
                case "Deprecated":
                case "java.lang.Deprecated":
                    // @Deprecated didn't have any members in Java 8, so clear it.
                    var jca = (JCTree.JCAnnotation) node;
                    jca.args = com.sun.tools.javac.util.List.nil();
            }
            return super.visitAnnotation(node, unused);
        }
    };

    // Can we really simply compare class files byte by byte?
    // I doubt it, but jar utility does exactly that, well...
    private static void removeDuplicates(String out8, String out9) {
        byte[] cafeBabe = {(byte) 0xca, (byte) 0xfe, (byte) 0xba, (byte) 0xbe};
        Path path8 = Path.of(out8), path9 = Path.of(out9);
        try (Stream<Path> stream8 = Files.walk(path8)) {
            stream8.filter(Files::isRegularFile).forEach(f8 -> {
                Path f9 = path9.resolve(path8.relativize(f8));
                try {
                    byte[] b9 = Files.readAllBytes(f9);
                    byte[] b8 = Files.readAllBytes(f8);
                    // First 4 bytes are a magic number, next 4 bytes are version which we are ignoring.
                    if (Arrays.equals(b8, 0, 4, cafeBabe, 0, 4) && Arrays.equals(b9, 0, 4, cafeBabe, 0, 4) &&
                            Arrays.equals(b8, 8, b8.length, b9, 8, b9.length)) {
                        // Duplicate! Remove it.
                        Files.delete(f9);
                    }
                } catch (NoSuchFileException | IllegalArgumentException | ArrayIndexOutOfBoundsException ignore) {
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
