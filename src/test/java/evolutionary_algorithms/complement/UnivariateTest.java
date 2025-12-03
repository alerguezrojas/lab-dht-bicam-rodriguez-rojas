package evolutionary_algorithms.complement;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

import problem.definition.State;

class UnivariateTest {

    @Test
    void testDistribution() {
        Univariate univariate = new Univariate();
        List<State> fathers = new ArrayList<>();
        
        // Create 2 fathers with 2 variables
        // Father 1: [1, 2]
        State f1 = new State();
        ArrayList<Object> c1 = new ArrayList<>();
        c1.add(1);
        c1.add(2);
        f1.setCode(c1);
        fathers.add(f1);
        
        // Father 2: [1, 3]
        State f2 = new State();
        ArrayList<Object> c2 = new ArrayList<>();
        c2.add(1);
        c2.add(3);
        f2.setCode(c2);
        fathers.add(f2);
        
        List<Probability> probs = univariate.distribution(fathers);
        
        assertNotNull(probs);
        // Variable 0: Value 1 appears 2 times (100%)
        // Variable 1: Value 2 appears 1 time (50%), Value 3 appears 1 time (50%)
        
        // Check results
        boolean foundVar0Val1 = false;
        for(Probability p : probs) {
            if((Integer)p.getKey() == 0 && (Integer)p.getValue() == 1) {
                assertEquals(1.0f, p.getProbability(), 0.001);
                foundVar0Val1 = true;
            }
        }
        assertTrue(foundVar0Val1);
    }

    @Test
    void testGetListKey() {
        Univariate univariate = new Univariate();
        SortedMap<String, Object> map = new TreeMap<>();
        map.put("key1", 1);
        map.put("key2", 2);
        
        List<String> keys = univariate.getListKey(map);
        assertEquals(2, keys.size());
        assertTrue(keys.contains("key1"));
        assertTrue(keys.contains("key2"));
    }
}
