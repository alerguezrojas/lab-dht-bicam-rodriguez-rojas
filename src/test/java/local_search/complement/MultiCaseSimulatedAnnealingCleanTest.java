package local_search.complement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import local_search.complement.MultiCaseSimulatedAnnealing;
import metaheuristics.generators.GeneratorType;
import metaheuristics.strategy.Strategy;
import problem.definition.Operator;
import problem.definition.Problem;
import problem.definition.State;
import local_search.candidate_type.CandidateType;
import local_search.complement.StrategyType;

public class MultiCaseSimulatedAnnealingCleanTest {

    private MultiCaseSimulatedAnnealing msa;

    @Mock
    private Problem mockProblem;
    
    @Mock
    private Operator mockOperator;
    
    @Mock
    private State mockState;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        msa = new MultiCaseSimulatedAnnealing();
        
        // Setup Strategy singleton with mock problem
        Strategy strategy = Strategy.getStrategy();
        strategy.setProblem(mockProblem);
        strategy.generator = msa;
    }

    @AfterEach
    public void tearDown() throws Exception {
        Field instance = Strategy.class.getDeclaredField("strategy");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void testConstructor() {
        assertNotNull(msa);
        assertEquals(GeneratorType.MultiCaseSimulatedAnnealing, msa.getTypeGenerator());
        assertEquals(50.0f, msa.getWeight());
    }

    @Test
    public void testGetSetTypeGenerator() {
        msa.setTypeGenerator(GeneratorType.GeneticAlgorithm);
        assertEquals(GeneratorType.GeneticAlgorithm, msa.getTypeGenerator());
        assertEquals(GeneratorType.GeneticAlgorithm, msa.getType());
    }

    @Test
    public void testGetSetWeight() {
        msa.setWeight(10.5f);
        assertEquals(10.5f, msa.getWeight());
    }

    @Test
    public void testGetSetStateRef() {
        msa.setStateRef(mockState);
        assertEquals(mockState, msa.getReference());
        
        msa.setInitialReference(mockState);
        assertEquals(mockState, msa.getReference());
    }
    
    @Test
    public void testGetReferenceList() {
        msa.setStateRef(mockState);
        when(mockState.clone()).thenReturn(mockState);
        List<State> list = msa.getReferenceList();
        assertNotNull(list);
        assertEquals(1, list.size());
    }
    
    @Test
    public void testGetTrace() {
        float[] trace = msa.getTrace();
        assertNotNull(trace);
        assertTrue(trace.length > 0);
        assertEquals(50.0f, trace[0]);
    }
    
    @Test
    public void testGetListCountBetterGender() {
        int[] list = msa.getListCountBetterGender();
        assertNotNull(list);
        assertEquals(0, list.length);
    }
    
    @Test
    public void testGetListCountGender() {
        int[] list = msa.getListCountGender();
        assertNotNull(list);
        assertEquals(0, list.length);
    }
    
    @Test
    public void testGetSonList() {
        assertNull(msa.getSonList());
    }
    
    @Test
    public void testAwardUpdateREF() {
        assertFalse(msa.awardUpdateREF(mockState));
    }

    @Test
    public void testGenerate() throws Exception {
        msa.setStateRef(mockState);
        
        List<State> neighborhood = new ArrayList<>();
        neighborhood.add(mockState);
        
        when(mockProblem.getOperator()).thenReturn(mockOperator);
        when(mockOperator.generatedNewState(any(State.class), anyInt())).thenReturn(neighborhood);
        when(mockState.clone()).thenReturn(mockState);
        
        State result = msa.generate(1);
        assertNotNull(result);
    }
    
    @Test
    public void testUpdateReference() throws Exception {
        msa.setStateRef(mockState);
        MultiCaseSimulatedAnnealing.tinitial = 100.0;
        MultiCaseSimulatedAnnealing.alpha = 0.9;
        MultiCaseSimulatedAnnealing.countIterationsT = 10;
        
        when(mockState.clone()).thenReturn(mockState);
        when(mockProblem.getTypeProblem()).thenReturn(Problem.ProblemType.Minimizar);
        
        // We are testing that it runs without error and potentially updates static vars
        msa.updateReference(mockState, 10);
        
        // Check if tinitial was updated (logic: if countIterationsCurrent == countIterationsT)
        // 100.0 * 0.9 = 90.0
        assertEquals(90.0, MultiCaseSimulatedAnnealing.tinitial, 0.01);
    }
    
    @Test
    public void testSimpleTest() {
        assertEquals(1, msa.simpleTest());
    }
}
