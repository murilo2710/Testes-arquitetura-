package com.deviante

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import kotlin.test.Test

class ArchitectureTest {
    private val applicationClasses: JavaClasses by lazy {
        ClassFileImporter().importPackages("com.deviante")
    }

    @Test
    fun `domain records do not depend on framework or outer layers`() {
        noClasses()
            .that().resideInAnyPackage("com.deviante.model", "com.deviante.model..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "com.deviante.db..",
                "com.deviante.dto..",
                "com.deviante.repository..",
                "io.ktor..",
                "org.jetbrains.exposed..",
            )
            .check(applicationClasses)
    }

    @Test
    fun `database schema does not depend on application or delivery layers`() {
        noClasses()
            .that().resideInAnyPackage("com.deviante.db", "com.deviante.db..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "com.deviante.dto..",
                "com.deviante.model..",
                "com.deviante.repository..",
                "io.ktor..",
            )
            .check(applicationClasses)
    }

    @Test
    fun `transport DTOs do not depend on persistence implementations`() {
        noClasses()
            .that().resideInAnyPackage("com.deviante.dto", "com.deviante.dto..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "com.deviante.db..",
                "com.deviante.repository..",
                "org.jetbrains.exposed..",
            )
            .check(applicationClasses)
    }

    @Test
    fun `repositories do not depend on the Ktor delivery framework`() {
        noClasses()
            .that().resideInAPackage("com.deviante.repository..")
            .should().dependOnClassesThat().resideInAnyPackage("io.ktor.server..")
            .check(applicationClasses)
    }
}