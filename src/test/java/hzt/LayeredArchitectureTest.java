package hzt;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "hzt", importOptions = ImportOption.DoNotIncludeTests.class)
public class LayeredArchitectureTest {

    private static final Logger LOGGER = LogManager.getLogger(LayeredArchitectureTest.class);

    @ArchTest
    static final ArchRule DEPENDENCY_RULE = layeredArchitecture()
            .layer("controller").definedBy("..controller..")
            .layer("model").definedBy("..model..");
//            .layer("view").definedBy("..view..");

//            .whereLayer("controller").mayNotBeAccessedByAnyLayer();
//            .whereLayer("model").mayOnlyBeAccessedByLayers("controller");
}

