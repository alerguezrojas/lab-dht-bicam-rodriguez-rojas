package local_search.candidate_type;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import problem.definition.State;

class RandomCandidateTest {

    @Test
    void testStateSearch() {
        RandomCandidate randomCandidate = new RandomCandidate();
        List<State> neighborhood = new ArrayList<>();
        
        State s1 = mock(State.class);
        State s2 = mock(State.class);
        State s3 = mock(State.class);
        
        neighborhood.add(s1);
        neighborhood.add(s2);
        neighborhood.add(s3);
        
        State result = randomCandidate.stateSearch(neighborhood);
        
        assertNotNull(result);
        assertTrue(neighborhood.contains(result));
    }
    
    @Test
    void testStateSearchSingleElement() {
        RandomCandidate randomCandidate = new RandomCandidate();
        List<State> neighborhood = new ArrayList<>();
        
        State s1 = mock(State.class);
        neighborhood.add(s1);
        
        State result = randomCandidate.stateSearch(neighborhood);
        
        assertEquals(s1, result);
    }
}
