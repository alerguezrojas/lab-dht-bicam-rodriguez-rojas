package factory_method;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import evolutionary_algorithms.complement.Distribution;
import evolutionary_algorithms.complement.DistributionType;
import evolutionary_algorithms.complement.Univariate;

class FactoryDistributionTest {

    @Test
    void testCreateDistribution() throws Exception {
        FactoryDistribution factory = new FactoryDistribution();
        
        Distribution d1 = factory.createDistribution(DistributionType.Univariate);
        assertNotNull(d1);
        assertTrue(d1 instanceof Univariate);
    }
}
