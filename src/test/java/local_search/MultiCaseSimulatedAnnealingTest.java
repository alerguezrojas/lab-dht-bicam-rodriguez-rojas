package local_search;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import factory_interface.IFFactoryAcceptCandidate;
import local_search.acceptation_type.AcceptType;
import local_search.acceptation_type.AcceptableCandidate;
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

    @Test
    public void testUpdateReference() throws Exception {
        State stateCandidate = mock(State.class);
        State stateRef = mock(State.class);
        when(stateCandidate.clone()).thenReturn(stateCandidate);
        
        mcsa.setInitialReference(stateRef);
        
        IFFactoryAcceptCandidate factory = mock(IFFactoryAcceptCandidate.class);
        AcceptableCandidate acceptableCandidate = mock(AcceptableCandidate.class);
        
        when(factory.createAcceptCandidate(any(AcceptType.class))).thenReturn(acceptableCandidate);
        when(acceptableCandidate.acceptCandidate(any(State.class), any(State.class))).thenReturn(true);
        
        mcsa.setFactoryAcceptCandidate(factory);
        
        MultiCaseSimulatedAnnealing.tinitial = 100.0;
        MultiCaseSimulatedAnnealing.alpha = 0.9;
        MultiCaseSimulatedAnnealing.countIterationsT = 10;
        
        mcsa.updateReference(stateCandidate, 10);
        
        verify(factory).createAcceptCandidate(any(AcceptType.class));
        verify(acceptableCandidate).acceptCandidate(any(State.class), any(State.class));
        assertEquals(stateCandidate, mcsa.getReference());
    }

    @Test
    public void testGetReferenceList() {
        State stateRef = mock(State.class);
        when(stateRef.clone()).thenReturn(stateRef);
        mcsa.setInitialReference(stateRef);
        
        List<State> list = mcsa.getReferenceList();
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(stateRef, list.get(0));
    }

    @Test
    public void testGetWeight() {
        mcsa.setWeight(10.5f);
        assertEquals(10.5f, mcsa.getWeight());
    }

    @Test
    public void testGetTrace() {
        float[] trace = mcsa.getTrace();
        assertNotNull(trace);
        assertEquals(1, trace.length);
        assertEquals(50.0f, trace[0]);
    }

    @Test
    public void testGetListCountBetterGender() {
        int[] list = mcsa.getListCountBetterGender();
        assertNotNull(list);
        assertEquals(0, list.length);
    }

    @Test
    public void testGetListCountGender() {
        int[] list = mcsa.getListCountGender();
        assertNotNull(list);
        assertEquals(0, list.length);
    }
}
