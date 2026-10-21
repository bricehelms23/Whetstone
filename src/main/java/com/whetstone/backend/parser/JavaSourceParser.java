package com.whetstone.backend.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * File contents -> AST.
 *
 * A reusable {@link JavaParser} (not StaticJavaParser) pinned to JAVA_21 to match
 * the pom's java.version. Rule engine will inject this via constructor
 * injection, same as the controller injects ProjectUploadService.
 */
@Service
public class JavaSourceParser {

    private final JavaParser javaParser;

    public JavaSourceParser() {
        ParserConfiguration config = new ParserConfiguration()
                .setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_21);
        this.javaParser = new JavaParser(config);
    }

    /**
     * Parses one Java source string into an AST.
     *
     * @throws IllegalArgumentException if the source does not parse.
     *         GlobalExceptionHandler already maps this to a 400 JSON body,
     *         so bad code becomes a clean client error for free.
     */
    public CompilationUnit parse(String source) {
        ParseResult<CompilationUnit> result = javaParser.parse(source);
        if (result.isSuccessful() && result.getResult().isPresent()) {
            return result.getResult().get();
        }
        String problems = result.getProblems().stream()
                .map(Object::toString)
                .collect(Collectors.joining("; "));
        throw new IllegalArgumentException("Source did not parse: " + problems);
    }

    /**
     * Parses every .java file from an upload's fileContents map.
     * Non-Java files are skipped; files that fail to parse are skipped too
     * rather than blowing up the whole upload (Step 3 rules must survive one
     * broken file).
     */
    public Map<String, CompilationUnit> parseProject(Map<String, String> fileContents) {
        Map<String, CompilationUnit> parsed = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : fileContents.entrySet()) {
            if (!entry.getKey().endsWith(".java")) {
                continue;
            }
            try {
                parsed.put(entry.getKey(), parse(entry.getValue()));
            } catch (IllegalArgumentException e) {
                // skip unparseable file; one bad file shouldn't kill the batch
            }
        }
        return parsed;
    }
}
