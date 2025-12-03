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

class AcceptBestTest {

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
    void testAcceptCandidateMaximizeBetter() throws Exception {
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Maximizar);
        
        State current = mock(State.class);
        State candidate = mock(State.class);
        
        when(current.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(10.0)));
        when(candidate.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(20.0)));
        
        AcceptBest acceptBest = new AcceptBest();
        assertTrue(acceptBest.acceptCandidate(current, candidate));
    }

    @Test
    void testAcceptCandidateMaximizeWorse() throws Exception {
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Maximizar);
        
        State current = mock(State.class);
        State candidate = mock(State.class);
        
        when(current.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(20.0)));
        when(candidate.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(10.0)));
        
        AcceptBest acceptBest = new AcceptBest();
        assertFalse(acceptBest.acceptCandidate(current, candidate));
    }

    @Test
    void testAcceptCandidateMinimizeBetter() throws Exception {
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Minimizar);
        
        State current = mock(State.class);
        State candidate = mock(State.class);
        
        when(current.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(20.0)));
        when(candidate.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(10.0)));
        
        AcceptBest acceptBest = new AcceptBest();
        assertTrue(acceptBest.acceptCandidate(current, candidate));
    }

    @Test
    void testAcceptCandidateMinimizeWorse() throws Exception {
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Minimizar);
        
        State current = mock(State.class);
        State candidate = mock(State.class);
        
        when(current.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(10.0)));
        when(candidate.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(20.0)));
        
        AcceptBest acceptBest = new AcceptBest();
        assertFalse(acceptBest.acceptCandidate(current, candidate));
    }
}
