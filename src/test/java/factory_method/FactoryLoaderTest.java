package factory_method;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import evolutionary_algorithms.complement.OnePointCrossover;

class FactoryLoaderTest {

    @Test
    void testGetInstanceValid() throws Exception {
        Object obj = FactoryLoader.getInstance("evolutionary_algorithms.complement.OnePointCrossover");
        assertNotNull(obj);
        assertTrue(obj instanceof OnePointCrossover);
    }

    @Test
    void testGetInstanceInvalid() {
        assertThrows(ClassNotFoundException.class, () -> {
            FactoryLoader.getInstance("invalid.ClassName");
        });
    }
}
