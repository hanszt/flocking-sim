package hzt;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static org.junit.jupiter.api.Assertions.assertEquals;

@AnalyzeClasses(packages = "hzt", importOptions = ImportOption.DoNotIncludeTests.class)
class LayeredArchitectureTest {

    @ArchTest
    static final ArchRule DEPENDENCY_RULE = layeredArchitecture()
            .layer("controller").definedBy("..controller..")
            .layer("service").definedBy("..service..")
            .layer("model").definedBy("..model..")
            .layer("view").definedBy("..view..")

            .whereLayer("view").mayNotBeAccessedByAnyLayer()
            .whereLayer("controller").mayOnlyBeAccessedByLayers("view")
            .whereLayer("service").mayOnlyBeAccessedByLayers("controller", "view");

    @Test
    void testArchRuleDescription() {
        String description = """
                Layered architecture consisting of
                layer 'controller' ('..controller..')
                layer 'service' ('..service..')
                layer 'model' ('..model..')
                layer 'view' ('..view..')
                where layer 'view' may not be accessed by any layer
                where layer 'controller' may only be accessed by layers ['view']
                where layer 'service' may only be accessed by layers ['controller', 'view']
                """.stripLeading().stripTrailing().stripIndent().strip().replaceAll("\\n", System.lineSeparator());
        assertEquals(description, DEPENDENCY_RULE.getDescription());
    }
}

