package evolutionary_algorithms.complement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import metaheuristics.strategy.Strategy;
import problem.definition.Problem;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

class TruncationSelectionTest {

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
    void testSelectionMaximization() {
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Maximizar);

        State s1 = mock(State.class);
        when(s1.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(10.0)));
        State s2 = mock(State.class);
        when(s2.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(20.0)));
        State s3 = mock(State.class);
        when(s3.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(5.0)));

        List<State> listState = new ArrayList<>(Arrays.asList(s1, s2, s3));
        TruncationSelection selection = new TruncationSelection();

        // Select top 2
        List<State> result = selection.selection(listState, 2);

        assertEquals(2, result.size());
        // Should be sorted descending: 20.0, 10.0
        assertEquals(s2, result.get(0));
        assertEquals(s1, result.get(1));
    }

    @Test
    void testSelectionMinimization() {
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Minimizar);

        State s1 = mock(State.class);
        when(s1.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(10.0)));
        State s2 = mock(State.class);
        when(s2.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(20.0)));
        State s3 = mock(State.class);
        when(s3.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(5.0)));

        List<State> listState = new ArrayList<>(Arrays.asList(s1, s2, s3));
        TruncationSelection selection = new TruncationSelection();

        // Select top 2
        List<State> result = selection.selection(listState, 2);

        assertEquals(2, result.size());
        // Should be sorted ascending: 5.0, 10.0
        assertEquals(s3, result.get(0));
        assertEquals(s1, result.get(1));
    }
    
    @Test
    void testOrderBetter() {
        State s1 = mock(State.class);
        when(s1.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(10.0)));
        State s2 = mock(State.class);
        when(s2.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(20.0)));
        
        List<State> listState = new ArrayList<>(Arrays.asList(s1, s2));
        TruncationSelection selection = new TruncationSelection();
        
        List<State> result = selection.OrderBetter(listState);
        assertEquals(s2, result.get(0));
        assertEquals(s1, result.get(1));
    }

    @Test
    void testAscOrderBetter() {
        State s1 = mock(State.class);
        when(s1.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(10.0)));
        State s2 = mock(State.class);
        when(s2.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(20.0)));
        
        List<State> listState = new ArrayList<>(Arrays.asList(s2, s1));
        TruncationSelection selection = new TruncationSelection();
        
        List<State> result = selection.ascOrderBetter(listState);
        assertEquals(s1, result.get(0));
        assertEquals(s2, result.get(1));
    }
}
