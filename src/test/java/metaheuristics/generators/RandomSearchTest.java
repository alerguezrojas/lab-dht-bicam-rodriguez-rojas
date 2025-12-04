package metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import metaheuristics.strategy.Strategy;
import problem.definition.Operator;
import problem.definition.Problem;
import problem.definition.State;

public class RandomSearchTest {

    private RandomSearch randomSearch;
    
    @Mock
    private Strategy strategyMock;
    
    @Mock
    private Problem problemMock;
    
    @Mock
    private Operator operatorMock;
    
    @Mock
    private State stateMock;
    
    @Mock
    private State newStateMock;

    private MockedStatic<Strategy> strategyStaticMock;
    private AutoCloseable mocks;

    @BeforeEach
    public void setUp() throws Exception {
        mocks = MockitoAnnotations.openMocks(this);
        
        // Reset Singleton
        resetSingleton();
        
        // Mock Strategy Singleton
        strategyStaticMock = mockStatic(Strategy.class);
        strategyStaticMock.when(Strategy::getStrategy).thenReturn(strategyMock);
        
        // Setup chain
        when(strategyMock.getProblem()).thenReturn(problemMock);
        when(problemMock.getOperator()).thenReturn(operatorMock);
        when(problemMock.getTypeProblem()).thenReturn(Problem.ProblemType.Minimizar);
        
        randomSearch = new RandomSearch();
    }

    @AfterEach
    public void tearDown() throws Exception {
        strategyStaticMock.close();
        mocks.close();
        resetSingleton();
    }

    private void resetSingleton() throws Exception {
        Field instance = Strategy.class.getDeclaredField("strategy");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void testGenerate() throws Exception {
        int operatorNumber = 1;
        List<State> neighborhood = new ArrayList<>();
        neighborhood.add(newStateMock);
        
        // Mock Operator behavior
        when(operatorMock.generateRandomState(operatorNumber)).thenReturn(neighborhood);
        
        State result = randomSearch.generate(operatorNumber);
        
        assertNotNull(result);
        assertEquals(newStateMock, result);
        verify(operatorMock).generateRandomState(operatorNumber);
    }
    
    @Test
    public void testSetAndGetReference() {
        randomSearch.setInitialReference(stateMock);
        assertEquals(stateMock, randomSearch.getReference());
    }
    
    @Test
    public void testGetType() {
        assertEquals(GeneratorType.RandomSearch, randomSearch.getType());
    }
    
    @Test
    public void testUpdateReference() throws Exception {
        randomSearch.setInitialReference(stateMock);
        
        ArrayList<Double> evalRef = new ArrayList<>();
        evalRef.add(10.0);
        when(stateMock.getEvaluation()).thenReturn(evalRef);
        
        ArrayList<Double> evalNew = new ArrayList<>();
        evalNew.add(5.0);
        when(newStateMock.getEvaluation()).thenReturn(evalNew);
        
        randomSearch.updateReference(newStateMock, 1);
    }

    @Test
    public void testGenerate_WithCountRef() throws Exception {
        int operatorNumber = 1;
        List<State> neighborhood = new ArrayList<>();
        neighborhood.add(newStateMock);
        
        when(operatorMock.generateRandomState(operatorNumber)).thenReturn(neighborhood);
        
        // Set static countRef to non-zero
        GeneticAlgorithm.countRef = 1;
        
        State result = randomSearch.generate(operatorNumber);
        
        assertNotNull(result);
        assertFalse(RandomSearch.listStateReference.isEmpty());
        
        // Reset
        GeneticAlgorithm.countRef = 0;
    }

    @Test
    public void testGetReferenceList() {
        randomSearch.setInitialReference(stateMock);
        List<State> list = randomSearch.getReferenceList();
        assertNotNull(list);
        assertTrue(list.contains(stateMock));
    }

    @Test
    public void testGetSetters() {
        randomSearch.setWeight(10.0f);
        assertEquals(10.0f, randomSearch.getWeight());
        
        randomSearch.setTypeGenerator(GeneratorType.GeneticAlgorithm);
        assertEquals(GeneratorType.GeneticAlgorithm, randomSearch.getTypeGenerator());
        
        assertNotNull(randomSearch.getListCountBetterGender());
        assertNotNull(randomSearch.getListCountGender());
        assertNotNull(randomSearch.getTrace());
        assertNull(randomSearch.getSonList());
        assertFalse(randomSearch.awardUpdateREF(stateMock));
    }
}
