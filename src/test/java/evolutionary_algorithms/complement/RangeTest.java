package evolutionary_algorithms.complement;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RangeTest {

    @Test
    void testRange() {
        Range range = new Range();
        range.setMax(10.0f);
        range.setMin(1.0f);
        
        assertEquals(10.0f, range.getMax());
        assertEquals(1.0f, range.getMin());
        
        Probability prob = new Probability();
        prob.setKey("key");
        prob.setValue(100);
        prob.setProbability(0.5f);
        
        range.setData(prob);
        
        Probability retrievedProb = range.getData();
        assertNotNull(retrievedProb);
        assertEquals("key", retrievedProb.getKey());
        assertEquals(100, retrievedProb.getValue());
        assertEquals(0.5f, retrievedProb.getProbability());
        
        // Test deep copy/null safety
        range.setData(null);
        assertNull(range.getData());
    }
}
