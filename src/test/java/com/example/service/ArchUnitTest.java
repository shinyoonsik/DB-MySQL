package com.example.service;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "com.example",
        importOptions = {ImportOption.DoNotIncludeTests.class})
public class ArchUnitTest {

    @ArchTest
    static final ArchRule serviceRuleTest = classes().that()
            .resideInAPackage("..service..")
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage(
                    "..service..",           // Service 계층
                    "..repository..",        // Repository 계층
                    "java..",                // JDK 기본 패키지
                    "..entity..",
                    "org.springframework..", // Spring 패키지 허용
                    "javax.."                // Javax 패키지 허용
            )
            .as("Service계층에서는 Service또는 Repository계층만 참조 할수 잇다.");


    @ArchTest
    static final ArchRule controllerRuleTest = classes().that().resideInAPackage("..controller..")
            .should().beAnnotatedWith(RestController.class)
            .orShould().beAnnotatedWith(Controller.class)
            .orShould().haveSimpleNameEndingWith ("Controller")
            .as("Controller클래스는 RestController 또는 Controller 주석이 달려있어야 하며 Controller로 이름이 끝나야 한다.");
}
