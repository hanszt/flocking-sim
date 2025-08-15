package hzt;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "hzt", importOptions = ImportOption.DoNotIncludeTests.class)
class LayeredArchitectureTest {

    @ArchTest
    static final ArchRule DEPENDENCY_RULE = layeredArchitecture()
            .consideringAllDependencies()
            .layer("controller").definedBy("..controller..")
            .layer("service").definedBy("..service..")
            .layer("model").definedBy("..model..")
            .layer("view").definedBy("..view..")

            .whereLayer("view").mayNotBeAccessedByAnyLayer()
            .whereLayer("controller").mayOnlyBeAccessedByLayers("view")
            .whereLayer("service").mayOnlyBeAccessedByLayers("controller", "view")
            .whereLayer("model").mayOnlyBeAccessedByLayers("model", "service", "controller");

    @Test
    void testServicesShouldOnlyBeAccessedByControllers() {
        final var importedClasses = new ClassFileImporter().importPackages("hzt");

        final var myRule = classes()
                .that().resideInAPackage("..service..")
                .should().onlyBeAccessed().byAnyPackage("..controller..", "..service..");

        myRule.check(importedClasses);
    }

    @Test
    void testDependencyRule() {
        DEPENDENCY_RULE.check(new ClassFileImporter().importPackages("hzt"));
    }
}

