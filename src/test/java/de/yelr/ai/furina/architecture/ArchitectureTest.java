package de.yelr.ai.furina.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@AnalyzeClasses(
        packages = "de.yelr.ai.furina",
        importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule controllers_should_be_web_adapters =
            classes()
                    .that()
                    .resideInAPackage("..controller..")
                    .should()
                    .beAnnotatedWith(RestController.class)
                    .andShould()
                    .haveSimpleNameEndingWith("Controller");

    @ArchTest
    static final ArchRule controllers_should_not_access_persistence =
            noClasses()
                    .that()
                    .resideInAPackage("..controller..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage("..entity..", "..mapper..");

    @ArchTest
    static final ArchRule services_should_not_depend_on_controllers =
            noClasses()
                    .that()
                    .resideInAPackage("..service..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("..controller..");

    @ArchTest
    static final ArchRule mappers_should_not_depend_on_services =
            noClasses()
                    .that()
                    .resideInAPackage("..mapper..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("..service..");

    @ArchTest
    static final ArchRule entities_should_be_independent =
            noClasses()
                    .that()
                    .resideInAPackage("..entity..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage("..controller..", "..service..", "..mapper..", "..dto..");

    @ArchTest
    static final ArchRule security_should_not_depend_on_controllers =
            noClasses()
                    .that()
                    .resideInAPackage("..security..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("..controller..");

    @ArchTest
    static final ArchRule common_should_not_depend_on_application_layers =
            noClasses()
                    .that()
                    .resideInAPackage("..common..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(
                            "..controller..", "..service..", "..mapper..", "..entity..");

    @ArchTest
    static final ArchRule field_injection_is_not_allowed =
            noFields().should().beAnnotatedWith(Autowired.class);
}
