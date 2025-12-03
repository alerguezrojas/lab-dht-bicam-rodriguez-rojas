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
        // This is expected to throw NullPointerException because of the bug in FactoryLoader
        // or ClassNotFoundException if the try-catch wasn't there.
        // Given the code:
        /*
        try {
            c = Class.forName(className);
        } catch (ClassNotFoundException e) { ... }
        Object o = null;
        try {
            o = c.newInstance(); // c is null here
        */
        assertThrows(NullPointerException.class, () -> {
            FactoryLoader.getInstance("invalid.ClassName");
        });
    }
}
