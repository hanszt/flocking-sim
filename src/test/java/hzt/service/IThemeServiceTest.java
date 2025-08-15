package hzt.service;

import org.junit.jupiter.api.Test;

import java.util.ServiceLoader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IThemeServiceTest {

    @Test
    void testServiceLoaderForIThemeService() {
        final var iThemeServices = ServiceLoader.load(IThemeService.class);

        final var count = iThemeServices.stream().count();

        assertEquals(0, count);
    }

}
