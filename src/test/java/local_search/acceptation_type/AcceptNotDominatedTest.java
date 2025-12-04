package local_search.acceptation_type;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import metaheuristics.strategy.Strategy;
import metaheuristics.generators.Generator;
import metaheuristics.generators.GeneratorType;
import problem.definition.Problem;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

class AcceptNotDominatedTest {

    private MockedStatic<Strategy> strategyMockedStatic;
    private Strategy strategyMock;
    private Problem problemMock;
    private Generator generatorMock;
    private List<State> listRefPoblacFinal;

    @BeforeEach
    void setUp() {
        strategyMock = mock(Strategy.class);
        problemMock = mock(Problem.class);
        generatorMock = mock(Generator.class);
        listRefPoblacFinal = new ArrayList<>();
        
        strategyMockedStatic = mockStatic(Strategy.class);
        strategyMockedStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
        when(strategyMock.getProblem()).thenReturn(problemMock);
        strategyMock.listRefPoblacFinal = listRefPoblacFinal;
        strategyMock.generator = generatorMock;
        when(generatorMock.getType()).thenReturn(GeneratorType.TabuSearch);
    }

    @AfterEach
    void tearDown() {
        strategyMockedStatic.close();
    }

    @Test
    void testAcceptCandidateNotDominated() {
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Maximizar);
        
        State current = mock(State.class);
        State candidate = mock(State.class);
        
        // Current: [10]
        when(current.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(10.0)));
        when(current.clone()).thenReturn(current);
        
        // Candidate: [20] (Dominates current)
        when(candidate.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(20.0)));
        when(candidate.clone()).thenReturn(candidate);
        
        AcceptNotDominated accept = new AcceptNotDominated();
        
        // First call adds current to list if empty
        // Then checks if candidate dominates current.
        // Dominance logic:
        // If candidate dominates current, it returns true.
        // But AcceptNotDominated logic is:
        // if(dominace.dominance(stateCurrent, stateCandidate)== false)
        // {
        //    if(dominace.ListDominance(stateCandidate, list) == true) ...
        // }
        
        // If current does NOT dominate candidate (which is true, 10 < 20), then check ListDominance.
        // ListDominance checks if candidate dominates any in list.
        // List has [current]. Candidate dominates current. So ListDominance removes current, adds candidate, returns true.
        
        assertTrue(accept.acceptCandidate(current, candidate));
        assertEquals(1, listRefPoblacFinal.size());
        assertEquals(candidate, listRefPoblacFinal.get(0));
    }
    
    @Test
    void testAcceptCandidateDominated() {
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Maximizar);
        
        State current = mock(State.class);
        State candidate = mock(State.class);
        
        // Current: [20]
        when(current.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(20.0)));
        when(current.clone()).thenReturn(current);
        
        // Candidate: [10] (Dominated by current)
        when(candidate.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(10.0)));
        
        AcceptNotDominated accept = new AcceptNotDominated();
        
        // Current dominates candidate.
        // dominace.dominance(stateCurrent, stateCandidate) returns true.
        // The if condition is `== false`. So it skips the block.
        // Returns false (initialized to false).
        
        assertFalse(accept.acceptCandidate(current, candidate));
    }
}
