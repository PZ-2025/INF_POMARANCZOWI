package com.orange.bookmanagment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;

@SpringBootTest
class BookManagementApplicationTests {

    @Test
    void contextLoads() {
        var modules = ApplicationModules.of(BookManagementApplication.class);

        for(var m : modules){
            System.out.print("module: " + m.getDisplayName() + " : " + m.getBasePackage());
        }

        modules.verify();
    }

}
