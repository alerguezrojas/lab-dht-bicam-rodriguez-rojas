package app;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class BiCIAMAppTest {

    @Test
    void testMain() {
        String[] args = {};
        assertDoesNotThrow(() -> BiCIAMApp.main(args));
    }
    
    @Test
    void testConstructor() {
        assertDoesNotThrow(BiCIAMApp::new);
    }
}
