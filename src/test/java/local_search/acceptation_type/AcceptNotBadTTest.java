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
import metaheuristics.generators.SimulatedAnnealing;
import problem.definition.Problem;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

class AcceptNotBadTTest {

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
        SimulatedAnnealing.tinitial = 100.0;
        
        State current = mock(State.class);
        State candidate = mock(State.class);
        
        // 10 >= 5 -> True
        when(current.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(5.0)));
        when(candidate.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(10.0)));
        
        AcceptNotBadT accept = new AcceptNotBadT();
        assertTrue(accept.acceptCandidate(current, candidate));
    }
    
    @Test
    void testAcceptCandidateMaximizeProbabilistic() throws Exception {
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Maximizar);
        SimulatedAnnealing.tinitial = 1000000.0; // Large T to make exp(delta/T) close to 1
        
        State current = mock(State.class);
        State candidate = mock(State.class);
        
        // 4 < 5 but high probability
        when(current.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(5.0)));
        when(candidate.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(4.0)));
        
        AcceptNotBadT accept = new AcceptNotBadT();
        // Since Math.random() is not mocked, this is flaky if T is not large enough.
        // With T=1000000, (4-5)/1000000 is very close to 0. exp(0) is 1.
        // random() < 1 is almost always true.
        assertTrue(accept.acceptCandidate(current, candidate));
    }

    @Test
    void testAcceptCandidateMinimizeTrue() throws Exception {
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Minimizar);
        SimulatedAnnealing.tinitial = 100.0;
        
        State current = mock(State.class);
        State candidate = mock(State.class);
        
        // 4 <= 5 -> True
        when(current.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(5.0)));
        when(candidate.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(4.0)));
        
        AcceptNotBadT accept = new AcceptNotBadT();
        assertTrue(accept.acceptCandidate(current, candidate));
    }
}
