package local_search.acceptation_type;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import metaheurictics.strategy.Strategy;
import problem.definition.Problem;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

class AcceptNotBadUTest {

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
    void testAcceptCandidateMaximizeTrue() throws Exception {
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Maximizar);
        when(strategyMock.getThreshold()).thenReturn(5.0);
        
        State current = mock(State.class);
        State candidate = mock(State.class);
        
        // current - candidate = 10 - 6 = 4 < 5 -> True
        when(current.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(10.0)));
        when(candidate.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(6.0)));
        
        AcceptNotBadU accept = new AcceptNotBadU();
        assertTrue(accept.acceptCandidate(current, candidate));
    }

    @Test
    void testAcceptCandidateMaximizeFalse() throws Exception {
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Maximizar);
        when(strategyMock.getThreshold()).thenReturn(3.0);
        
        State current = mock(State.class);
        State candidate = mock(State.class);
        
        // current - candidate = 10 - 6 = 4 > 3 -> False
        when(current.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(10.0)));
        when(candidate.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(6.0)));
        
        AcceptNotBadU accept = new AcceptNotBadU();
        assertFalse(accept.acceptCandidate(current, candidate));
    }
    
    @Test
    void testAcceptCandidateMinimizeTrue() throws Exception {
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Minimizar);
        when(strategyMock.getThreshold()).thenReturn(3.0);
        
        State current = mock(State.class);
        State candidate = mock(State.class);
        
        // current - candidate = 10 - 6 = 4 > 3 -> True
        when(current.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(10.0)));
        when(candidate.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(6.0)));
        
        AcceptNotBadU accept = new AcceptNotBadU();
        assertTrue(accept.acceptCandidate(current, candidate));
    }
    
    @Test
    void testAcceptCandidateMinimizeFalse() throws Exception {
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Minimizar);
        when(strategyMock.getThreshold()).thenReturn(5.0);
        
        State current = mock(State.class);
        State candidate = mock(State.class);
        
        // current - candidate = 10 - 6 = 4 < 5 -> False
        when(current.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(10.0)));
        when(candidate.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(6.0)));
        
        AcceptNotBadU accept = new AcceptNotBadU();
        assertFalse(accept.acceptCandidate(current, candidate));
    }
}
