package local_search.candidate_type;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import factory_interface.IFFactoryCandidate;
import factory_method.FactoryCandidate;
import local_search.complement.StrategyType;
import local_search.complement.TabuSolutions;
import metaheurictics.strategy.Strategy;
import problem.definition.Problem;
import problem.definition.State;

class CandidateValueTest {

    private MockedStatic<Strategy> strategyMockedStatic;
    private Strategy strategyMock;
    private Problem problemMock;

    @BeforeEach
    void setUp() {
        strategyMock = mock(Strategy.class);
        problemMock = mock(Problem.class);
        
        strategyMockedStatic = mockStatic(Strategy.class);
        strategyMockedStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
        when(strategyMock.getProblem()).thenReturn(problemMock);
    }

    @AfterEach
    void tearDown() {
        strategyMockedStatic.close();
    }

    @Test
    void testNewSearchCandidate() throws Exception {
        CandidateValue candidateValue = new CandidateValue();
        SearchCandidate sc = candidateValue.newSearchCandidate(CandidateType.RandomCandidate);
        assertNotNull(sc);
        assertTrue(sc instanceof RandomCandidate);
    }
    
    @Test
    void testStateCandidateNonTabu() throws Exception {
        CandidateValue candidateValue = new CandidateValue();
        List<State> neighborhood = new ArrayList<>();
        State s1 = mock(State.class);
        neighborhood.add(s1);
        
        // StrategyType.HILL_CLIMBING (or anything not TABU)
        // CandidateType.RandomCandidate
        
        State result = candidateValue.stateCandidate(mock(State.class), CandidateType.RandomCandidate, StrategyType.NORMAL, 1, neighborhood);
        
        assertEquals(s1, result);
    }
    
    // Testing TABU path is harder because of `new TabuSolutions()` inside the method.
    // But we can try to test it if TabuSolutions doesn't crash.
    // TabuSolutions probably uses Strategy.
    
}
