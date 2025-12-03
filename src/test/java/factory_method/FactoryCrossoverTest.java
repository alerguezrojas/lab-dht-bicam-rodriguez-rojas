package factory_method;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import evolutionary_algorithms.complement.Crossover;
import evolutionary_algorithms.complement.CrossoverType;
import evolutionary_algorithms.complement.OnePointCrossover;
import evolutionary_algorithms.complement.UniformCrossover;

class FactoryCrossoverTest {

    @Test
    void testCreateCrossover() throws Exception {
        FactoryCrossover factory = new FactoryCrossover();
        
        Crossover c1 = factory.createCrossover(CrossoverType.OnePointCrossover);
        assertNotNull(c1);
        assertTrue(c1 instanceof OnePointCrossover);
        
        Crossover c2 = factory.createCrossover(CrossoverType.UniformCrossover);
        assertNotNull(c2);
        assertTrue(c2 instanceof UniformCrossover);
    }
}
