package factory_method;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import local_search.acceptation_type.AcceptType;
import local_search.acceptation_type.AcceptableCandidate;
import local_search.acceptation_type.AcceptBest;
import local_search.acceptation_type.AcceptAnyone;

class FactoryAcceptCandidateTest {

    @Test
    void testCreateAcceptCandidate() throws Exception {
        FactoryAcceptCandidate factory = new FactoryAcceptCandidate();
        
        AcceptableCandidate a1 = factory.createAcceptCandidate(AcceptType.AcceptBest);
        assertNotNull(a1);
        assertTrue(a1 instanceof AcceptBest);
        
        AcceptableCandidate a2 = factory.createAcceptCandidate(AcceptType.AcceptAnyone);
        assertNotNull(a2);
        assertTrue(a2 instanceof AcceptAnyone);
    }
}
