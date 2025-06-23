package com.fujentopj.fujento;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

public class ModulithDocumentationTest {
    @Test
    void writeDocumentation() {
        ApplicationModules modules = ApplicationModules.of("com.fujentopj.fujento.module");

        new Documenter(modules)
                .writeDocumentation() // Genera HTML + PlantUML
                .writeIndividualModulesAsPlantUml()
                .writeModuleCanvases();
    }
}
