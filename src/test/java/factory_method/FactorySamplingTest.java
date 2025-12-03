package factory_method;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import evolutionary_algorithms.complement.Sampling;
import evolutionary_algorithms.complement.SamplingType;
import evolutionary_algorithms.complement.ProbabilisticSampling;

class FactorySamplingTest {

    @Test
    void testCreateSampling() throws Exception {
        FactorySampling factory = new FactorySampling();
        
        Sampling s1 = factory.createSampling(SamplingType.ProbabilisticSampling);
        assertNotNull(s1);
        assertTrue(s1 instanceof ProbabilisticSampling);
    }
}
