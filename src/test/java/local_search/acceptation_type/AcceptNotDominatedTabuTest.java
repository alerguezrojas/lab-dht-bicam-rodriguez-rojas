package local_search.acceptation_type;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import metaheuristics.strategy.Strategy;
import metaheuristics.generators.Generator;
import metaheuristics.generators.GeneratorType;
import problem.definition.Problem;
import problem.definition.State;

class AcceptNotDominatedTabuTest {

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
    void testAcceptCandidateAlwaysTrue() {
        State current = mock(State.class);
        State candidate = mock(State.class);
        
        when(current.clone()).thenReturn(current);
        when(candidate.clone()).thenReturn(candidate);
        
        // Even if we don't mock evaluations, it should just run ListDominance and return true.
        // But ListDominance needs evaluations to work without crashing if it compares.
        // However, if list is empty, it adds current.
        // Then calls ListDominance(candidate, list).
        // ListDominance will compare candidate with elements in list (current).
        // So we need evaluations.
        
        // Let's mock evaluations to be safe, although the return value is always true.
        when(current.getEvaluation()).thenReturn(new ArrayList<>(List.of(10.0)));
        when(candidate.getEvaluation()).thenReturn(new ArrayList<>(List.of(20.0)));
        
        // We also need problem type for Dominance check inside ListDominance
        when(problemMock.getTypeProblem()).thenReturn(Problem.ProblemType.Maximizar);

        AcceptNotDominatedTabu accept = new AcceptNotDominatedTabu();
        
        assertTrue(accept.acceptCandidate(current, candidate));
        
        // Verify list interaction
        // List should contain candidate because 20 > 10 (Maximizar)
        // ListDominance removes dominated (current) and adds dominator (candidate)
        assertEquals(1, listRefPoblacFinal.size());
        assertEquals(candidate, listRefPoblacFinal.get(0));
    }
}
