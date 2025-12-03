package factory_method;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import local_search.candidate_type.CandidateType;
import local_search.candidate_type.SearchCandidate;
import local_search.candidate_type.RandomCandidate;
import local_search.candidate_type.NotDominatedCandidate;

class FactoryCandidateTest {

    @Test
    void testCreateSearchCandidate() throws Exception {
        FactoryCandidate factory = new FactoryCandidate();
        
        SearchCandidate s1 = factory.createSearchCandidate(CandidateType.RandomCandidate);
        assertNotNull(s1);
        assertTrue(s1 instanceof RandomCandidate);
        
        SearchCandidate s2 = factory.createSearchCandidate(CandidateType.NotDominatedCandidate);
        assertNotNull(s2);
        assertTrue(s2 instanceof NotDominatedCandidate);
    }
}
