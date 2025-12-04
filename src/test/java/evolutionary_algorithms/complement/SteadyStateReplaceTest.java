package evolutionary_algorithms.complement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

class SteadyStateReplaceTest {

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
    void testReplaceMaximization() {
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Maximizar);

        State s1 = mock(State.class);
        when(s1.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(10.0)));
        State s2 = mock(State.class);
        when(s2.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(20.0)));
        State s3 = mock(State.class);
        when(s3.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(5.0))); // Worst

        State candidate = mock(State.class);
        when(candidate.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(15.0))); // Better than worst

        List<State> listState = new ArrayList<>(Arrays.asList(s1, s2, s3));
        SteadyStateReplace replace = new SteadyStateReplace();

        List<State> result = replace.replace(candidate, listState);

        // Should replace s3 with candidate
        assertTrue(result.contains(candidate));
        assertTrue(result.contains(s1));
        assertTrue(result.contains(s2));
        assertEquals(3, result.size());
    }

    @Test
    void testReplaceMaximizationNoReplace() {
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Maximizar);

        State s1 = mock(State.class);
        when(s1.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(10.0)));
        State s2 = mock(State.class);
        when(s2.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(20.0)));
        State s3 = mock(State.class);
        when(s3.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(5.0))); // Worst

        State candidate = mock(State.class);
        when(candidate.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(4.0))); // Worse than worst

        List<State> listState = new ArrayList<>(Arrays.asList(s1, s2, s3));
        SteadyStateReplace replace = new SteadyStateReplace();

        List<State> result = replace.replace(candidate, listState);

        // Should NOT replace
        assertTrue(result.contains(s3));
        assertTrue(result.contains(s1));
        assertTrue(result.contains(s2));
        assertEquals(3, result.size());
    }

    @Test
    void testReplaceMinimization() {
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Minimizar);

        State s1 = mock(State.class);
        when(s1.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(10.0)));
        State s2 = mock(State.class);
        when(s2.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(20.0))); // Worst (Max)
        State s3 = mock(State.class);
        when(s3.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(5.0)));

        State candidate = mock(State.class);
        when(candidate.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(15.0))); // Better than worst

        List<State> listState = new ArrayList<>(Arrays.asList(s1, s2, s3));
        SteadyStateReplace replace = new SteadyStateReplace();

        List<State> result = replace.replace(candidate, listState);

        // Should replace s2 with candidate
        assertTrue(result.contains(candidate));
        assertTrue(result.contains(s1));
        assertTrue(result.contains(s3));
        assertEquals(3, result.size());
    }

    @Test
    void testMinValue() {
        State s1 = mock(State.class);
        when(s1.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(10.0)));
        State s2 = mock(State.class);
        when(s2.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(20.0)));
        State s3 = mock(State.class);
        when(s3.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(5.0)));

        List<State> listState = new ArrayList<>(Arrays.asList(s1, s2, s3));
        SteadyStateReplace replace = new SteadyStateReplace();

        State min = replace.MinValue(listState);
        assertEquals(s3, min);
    }

    @Test
    void testMaxValue() {
        State s1 = mock(State.class);
        when(s1.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(10.0)));
        State s2 = mock(State.class);
        when(s2.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(20.0)));
        State s3 = mock(State.class);
        when(s3.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(5.0)));

        List<State> listState = new ArrayList<>(Arrays.asList(s1, s2, s3));
        SteadyStateReplace replace = new SteadyStateReplace();

        State max = replace.MaxValue(listState);
        assertEquals(s2, max);
    }
}
