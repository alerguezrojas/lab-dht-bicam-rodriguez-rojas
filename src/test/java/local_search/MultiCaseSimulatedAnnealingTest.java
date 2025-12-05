package local_search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import metaheuristics.generators.GeneratorType;
import metaheuristics.strategy.Strategy;
import problem.definition.Operator;
import problem.definition.Problem;
import problem.definition.State;

public class MultiCaseSimulatedAnnealingTest {

    private MultiCaseSimulatedAnnealing mcsa;

    @BeforeEach
    public void setUp() {
        mcsa = new MultiCaseSimulatedAnnealing();
    }

    @Test
    public void testConstructor() {
        assertNotNull(mcsa);
        assertEquals(GeneratorType.MultiCaseSimulatedAnnealing, mcsa.getTypeGenerator());
    }

    @Test
    public void testSimpleTest() {
        assertEquals(1, mcsa.simpleTest());
    }

    @Test
    public void testSetTypeGenerator() {
        mcsa.setTypeGenerator(GeneratorType.GeneticAlgorithm);
        assertEquals(GeneratorType.GeneticAlgorithm, mcsa.getTypeGenerator());
    }

    @Test
    public void testSetInitialReference() {
        State state = mock(State.class);
        mcsa.setInitialReference(state);
        assertEquals(state, mcsa.getReference());
    }
    
    @Test
    public void testSetStateRef() {
        State state = mock(State.class);
        mcsa.setStateRef(state);
        assertEquals(state, mcsa.getReference());
    }

    @Test
    public void testGenerate() throws Exception {
        Strategy strategy = Strategy.getStrategy();
        Problem problem = mock(Problem.class);
        strategy.setProblem(problem);
        
        Operator operator = mock(Operator.class);
        when(problem.getOperator()).thenReturn(operator);
        
        State stateRef = mock(State.class);
        mcsa.setInitialReference(stateRef);
        
        List<State> neighborhood = new ArrayList<>();
        State neighbor = mock(State.class);
        neighborhood.add(neighbor);
        
        when(operator.generatedNewState(stateRef, 1)).thenReturn(neighborhood);
        
        State result = mcsa.generate(1);
        assertNotNull(result);
    }
}
