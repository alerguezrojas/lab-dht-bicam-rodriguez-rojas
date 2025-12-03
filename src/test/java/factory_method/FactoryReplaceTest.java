package factory_method;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import evolutionary_algorithms.complement.Replace;
import evolutionary_algorithms.complement.ReplaceType;
import evolutionary_algorithms.complement.SteadyStateReplace;
import evolutionary_algorithms.complement.GenerationalReplace;

class FactoryReplaceTest {

    @Test
    void testCreateReplace() throws Exception {
        FactoryReplace factory = new FactoryReplace();
        
        Replace r1 = factory.createReplace(ReplaceType.SteadyStateReplace);
        assertNotNull(r1);
        assertTrue(r1 instanceof SteadyStateReplace);
        
        Replace r2 = factory.createReplace(ReplaceType.GenerationalReplace);
        assertNotNull(r2);
        assertTrue(r2 instanceof GenerationalReplace);
    }
}
