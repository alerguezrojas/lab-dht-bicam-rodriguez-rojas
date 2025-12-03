package evolutionary_algorithms.complement;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ProbabilityTest {

    @Test
    void testConstructorAndGettersSetters() {
        Probability p = new Probability();
        p.setKey("key");
        p.setValue(10);
        p.setProbability(0.5f);

        assertEquals("key", p.getKey());
        assertEquals(10, p.getValue());
        assertEquals(0.5f, p.getProbability());
    }

    @Test
    void testCopyConstructor() {
        Probability p1 = new Probability();
        p1.setKey("key");
        p1.setValue(10);
        p1.setProbability(0.5f);

        Probability p2 = new Probability(p1);

        assertEquals(p1.getKey(), p2.getKey());
        assertEquals(p1.getValue(), p2.getValue());
        assertEquals(p1.getProbability(), p2.getProbability());
    }
}
