package local_search.candidate_type;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import problem.definition.State;

class SmallerCandidateTest {

    @Test
    void testStateSearch() throws Exception {
        SmallerCandidate smallerCandidate = new SmallerCandidate();
        List<State> neighborhood = new ArrayList<>();
        
        State s1 = mock(State.class);
        when(s1.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(10.0)));
        
        State s2 = mock(State.class);
        when(s2.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(5.0)));
        
        State s3 = mock(State.class);
        when(s3.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(15.0)));
        
        neighborhood.add(s1);
        neighborhood.add(s2);
        neighborhood.add(s3);
        
        State result = smallerCandidate.stateSearch(neighborhood);
        
        assertEquals(s2, result);
    }
    
    @Test
    void testStateSearchSingleElement() throws Exception {
        SmallerCandidate smallerCandidate = new SmallerCandidate();
        List<State> neighborhood = new ArrayList<>();
        
        State s1 = mock(State.class);
        when(s1.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(10.0)));
        neighborhood.add(s1);
        
        State result = smallerCandidate.stateSearch(neighborhood);
        
        assertEquals(s1, result);
    }
}
