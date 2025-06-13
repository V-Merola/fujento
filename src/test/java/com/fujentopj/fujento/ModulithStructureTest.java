package com.fujentopj.fujento;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
public class ModulithStructureTest {

    @Test
    void verifiesModuleDependencies() {
        ApplicationModules modules = ApplicationModules.of(FujentoApplication.class);
        modules.verify(); // Fails if illegal dependencies are found
        modules.detectViolations();



    }

    @Test
    void printModulesStructure() {
        ApplicationModules modules = ApplicationModules.of(FujentoApplication.class);

        modules.forEach(module -> {
            System.out.println("📦 Modulo: " + module.getName());

            System.out.println("├─ Base package: " + module.getBasePackage().getName());
            System.out.println("├─ Spring beans:");
            module.getSpringBeans().forEach(bean ->
                    System.out.println("│   ├── " + bean.getType() + " (" + bean.getType().getSimpleName() + ")")
            );

            System.out.println();
        });
    }

}