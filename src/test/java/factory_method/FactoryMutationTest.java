package factory_method;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import evolutionary_algorithms.complement.AIOMutation;
import evolutionary_algorithms.complement.Mutation;
import evolutionary_algorithms.complement.MutationType;
import evolutionary_algorithms.complement.OnePointMutation;
import evolutionary_algorithms.complement.TowPointsMutation;

class FactoryMutationTest {

    @Test
    void testCreateMutation() throws Exception {
        FactoryMutation factory = new FactoryMutation();
        
        Mutation m1 = factory.createMutation(MutationType.OnePointMutation);
        assertNotNull(m1);
        assertTrue(m1 instanceof OnePointMutation);
        
        Mutation m2 = factory.createMutation(MutationType.TowPointsMutation);
        assertNotNull(m2);
        assertTrue(m2 instanceof TowPointsMutation);
        
        Mutation m3 = factory.createMutation(MutationType.AIOMutation);
        assertNotNull(m3);
        assertTrue(m3 instanceof AIOMutation);
    }
}
