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
import problem.definition.ObjetiveFunction;
import problem.definition.Problem;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

class AcceptNotBadTest {

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
    void testAcceptCandidateMaximizeTrue() {
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Maximizar);
        // Mock function list to enter the loop
        ArrayList<ObjetiveFunction> functions = new ArrayList<>();
        functions.add(mock(ObjetiveFunction.class));
        when(problemMock.getFunction()).thenReturn(functions);
        
        State current = mock(State.class);
        State candidate = mock(State.class);
        
        // 10 >= 5 -> True
        when(current.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(5.0)));
        when(candidate.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(10.0)));
        
        AcceptNotBad accept = new AcceptNotBad();
        assertTrue(accept.acceptCandidate(current, candidate));
    }

    @Test
    void testAcceptCandidateMaximizeFalse() {
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Maximizar);
        ArrayList<ObjetiveFunction> functions = new ArrayList<>();
        functions.add(mock(ObjetiveFunction.class));
        when(problemMock.getFunction()).thenReturn(functions);
        
        State current = mock(State.class);
        State candidate = mock(State.class);
        
        // 4 < 5 -> False
        when(current.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(5.0)));
        when(candidate.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(4.0)));
        
        AcceptNotBad accept = new AcceptNotBad();
        assertFalse(accept.acceptCandidate(current, candidate));
    }
    
    @Test
    void testAcceptCandidateMinimizeTrue() {
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Minimizar);
        ArrayList<ObjetiveFunction> functions = new ArrayList<>();
        functions.add(mock(ObjetiveFunction.class));
        when(problemMock.getFunction()).thenReturn(functions);
        
        State current = mock(State.class);
        State candidate = mock(State.class);
        
        // 4 <= 5 -> True
        when(current.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(5.0)));
        when(candidate.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(4.0)));
        
        AcceptNotBad accept = new AcceptNotBad();
        assertTrue(accept.acceptCandidate(current, candidate));
    }
    
    @Test
    void testAcceptCandidateMinimizeFalse() {
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Minimizar);
        ArrayList<ObjetiveFunction> functions = new ArrayList<>();
        functions.add(mock(ObjetiveFunction.class));
        when(problemMock.getFunction()).thenReturn(functions);
        
        State current = mock(State.class);
        State candidate = mock(State.class);
        
        // 10 > 5 -> False
        when(current.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(5.0)));
        when(candidate.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(10.0)));
        
        AcceptNotBad accept = new AcceptNotBad();
        assertFalse(accept.acceptCandidate(current, candidate));
    }
}
