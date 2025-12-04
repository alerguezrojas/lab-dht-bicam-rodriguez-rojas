package metaheuristics.generators;

import local_search.complement.MultiCaseSimulatedAnnealing;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class MultiCaseSimulatedAnnealingSimpleTest {

    @Test
    public void testSimpleMethods() {
        MultiCaseSimulatedAnnealing generator = new MultiCaseSimulatedAnnealing();
        
        generator.setWeight(12.3f);
        assertEquals(12.3f, generator.getWeight());
        
        generator.setTypeGenerator(GeneratorType.MultiCaseSimulatedAnnealing);
        assertEquals(GeneratorType.MultiCaseSimulatedAnnealing, generator.getTypeGenerator());
        
        assertNotNull(generator.getListCountBetterGender());
        assertNotNull(generator.getListCountGender());
        assertNotNull(generator.getTrace());
        assertEquals(1, generator.simpleTest());
    }
}
