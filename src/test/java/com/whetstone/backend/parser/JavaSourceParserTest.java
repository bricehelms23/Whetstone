package com.whetstone.backend.parser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The "prove it" step: Calculator.java -> AST -> method count.
 * Swap the CALCULATOR fixture for the actual Calculator.java from your sample
 * project and adjust the expected count if you want the proof to run against your real upload.
 */
class JavaSourceParserTest {

    private final JavaSourceParser parser = new JavaSourceParser();

    private static final String CALCULATOR = """
            public class Calculator {
                public int add(int a, int b) { return a + b; }
                public int multiply(int a, int b) { return a * b; }
            }
            """;

    @Test
    void parsesSourceIntoAst() {
        CompilationUnit cu = parser.parse(CALCULATOR);

        assertEquals("Calculator", cu.getType(0).getNameAsString());
        assertEquals(2, cu.findAll(MethodDeclaration.class).size());
    }

    @Test
    void rejectsUnparseableSource() {
        assertThrows(IllegalArgumentException.class,
                () -> parser.parse("public class { broken "));
    }

    @Test
    void parseProjectParsesOnlyJavaFilesAndSkipsBadOnes() {
        Map<String, String> files = new LinkedHashMap<>();
        files.put("src/main/Calculator.java", CALCULATOR);
        files.put("README.md", "# hello");
        files.put("src/main/Broken.java", "class Broken { oops");

        Map<String, CompilationUnit> parsed = parser.parseProject(files);

        assertEquals(1, parsed.size());
        assertTrue(parsed.containsKey("src/main/Calculator.java"));
    }
}
