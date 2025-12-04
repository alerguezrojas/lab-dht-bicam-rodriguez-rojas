package local_search.acceptation_type;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import metaheuristics.strategy.Strategy;
import problem.definition.Problem;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

class DominanceTest {

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
    void testDominanceMaximizeTrue() {
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Maximizar);
        
        State s1 = mock(State.class);
        State s2 = mock(State.class);
        
        // s1 dominates s2 (10 > 5, 20 > 15)
        when(s1.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(10.0, 20.0)));
        when(s2.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(5.0, 15.0)));
        
        Dominance dominance = new Dominance();
        assertTrue(dominance.dominance(s1, s2));
    }

    @Test
    void testDominanceMaximizeFalse() {
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Maximizar);
        
        State s1 = mock(State.class);
        State s2 = mock(State.class);
        
        // s1 does not dominate s2 (10 > 5, 10 < 15)
        when(s1.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(10.0, 10.0)));
        when(s2.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(5.0, 15.0)));
        
        Dominance dominance = new Dominance();
        assertFalse(dominance.dominance(s1, s2));
    }
    
    @Test
    void testDominanceMinimizeTrue() {
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Minimizar);
        
        State s1 = mock(State.class);
        State s2 = mock(State.class);
        
        // s1 dominates s2 (5 < 10, 15 < 20)
        when(s1.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(5.0, 15.0)));
        when(s2.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(10.0, 20.0)));
        
        Dominance dominance = new Dominance();
        assertTrue(dominance.dominance(s1, s2));
    }

    @Test
    void testDominanceMinimizeFalse() {
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Minimizar);
        
        State s1 = mock(State.class);
        State s2 = mock(State.class);
        
        // s1 does not dominate s2 (5 < 10, 20 > 15)
        when(s1.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(5.0, 20.0)));
        when(s2.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(10.0, 15.0)));
        
        Dominance dominance = new Dominance();
        assertFalse(dominance.dominance(s1, s2));
    }
}
