package de.uni.heidelberg.Utils;

import com.google.common.io.Closer;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;

import java.io.*;

/**
 * Created by dominik on 05.10.15.
 */
public class PackageExtractor {
    private final File file;

    public PackageExtractor(File file) {
        this.file = file;
    }

    public String extractPackageName() throws IOException {
        Closer closer = Closer.create();
        try {
            FileInputStream fis = closer.register(new FileInputStream(file));
            CompilationUnit cu = JavaParser.parse(fis);
            return cu.getPackage().getName().toString();
        } catch (Throwable t) {
            closer.rethrow(t);
        } finally {
            closer.close();
        }
        throw new IOException("Could not extract package name.");
    }
}