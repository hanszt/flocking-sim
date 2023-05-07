package hzt.controller;

import org.junit.jupiter.api.Test;

import java.util.ServiceLoader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FXMLControllerTest {

    @Test
    void testServiceLoaderForFxmlController() {
        ServiceLoader<FXMLController> fxmlControllerServiceLoader = ServiceLoader.load(FXMLController.class);

        final var count = fxmlControllerServiceLoader.stream().count();

        assertEquals(0, count);
    }

}
