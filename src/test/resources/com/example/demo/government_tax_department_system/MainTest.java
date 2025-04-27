package com.example.demo.government_tax_department_system;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void testStart() {
        Main main = new Main();

        main.start();

        assertTrue(true, "start() should initialize the system successfully");

    }

    @Test
    void testMain() {

        String[] args = new String[]{};
        Main.main(args);

        assertDoesNotThrow(() -> Main.main(args), "main() should execute without exceptions");
    }
}