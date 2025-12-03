package local_search.complement;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StopExecuteTest {

    @Test
    void testStopIterations() {
        StopExecute stopExecute = new StopExecute();
        
        assertFalse(stopExecute.stopIterations(5, 10));
        assertTrue(stopExecute.stopIterations(10, 10));
        assertTrue(stopExecute.stopIterations(11, 10));
    }
}
