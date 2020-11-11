package hzt;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "hzt", importOptions = ImportOption.DoNotIncludeTests.class)
public class LayeredArchitectureTest {

    @ArchTest
    static final ArchRule DEPENDENCY_RULE = layeredArchitecture()
            .layer("controller").definedBy("..controller..")
            .layer("service").definedBy("..service..")
            .layer("model").definedBy("..model..")
            .layer("view").definedBy("..view..")

            .whereLayer("view").mayNotBeAccessedByAnyLayer()
            .whereLayer("controller").mayOnlyBeAccessedByLayers("view")
            .whereLayer("service").mayOnlyBeAccessedByLayers("controller", "view");
}

