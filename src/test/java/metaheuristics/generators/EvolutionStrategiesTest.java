package metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import metaheuristics.strategy.Strategy;
import problem.definition.Codification;
import problem.definition.Operator;
import problem.definition.Problem;
import problem.definition.State;
import evolutionary_algorithms.complement.MutationType;
import evolutionary_algorithms.complement.ReplaceType;
import evolutionary_algorithms.complement.SelectionType;

public class EvolutionStrategiesTest {

    private EvolutionStrategies evolutionStrategies;
    
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

    @Mock
    private Codification codificationMock;

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
        when(problemMock.getState()).thenReturn(stateMock);
        when(stateMock.getCopy()).thenReturn(newStateMock);
        // Setup code list
        ArrayList<Object> codeList = new ArrayList<>();
        codeList.add(1);
        codeList.add(2);
        codeList.add(3);
        codeList.add(4);
        codeList.add(5);
        
        when(newStateMock.getCode()).thenReturn(codeList);
        when(newStateMock.getEvaluation()).thenReturn(new ArrayList<>());
        when(newStateMock.getCopy()).thenReturn(newStateMock);
        
        when(problemMock.getTypeProblem()).thenReturn(Problem.ProblemType.Minimizar);
        when(problemMock.getCodification()).thenReturn(codificationMock);
        when(codificationMock.getVariableCount()).thenReturn(5);
        when(codificationMock.getAleatoryKey()).thenReturn(0);
        when(codificationMock.getVariableAleatoryValue(anyInt())).thenReturn(99);
        
        // Setup Strategy fields
        strategyMock.mapGenerators = new TreeMap<>();
        when(strategyMock.getListKey()).thenReturn(new ArrayList<>());
        
        // Setup static fields
        EvolutionStrategies.selectionType = SelectionType.TruncationSelection;
        EvolutionStrategies.mutationType = MutationType.OnePointMutation;
        EvolutionStrategies.replaceType = ReplaceType.GenerationalReplace;
        EvolutionStrategies.truncation = 1;
        EvolutionStrategies.PM = 0.1;
        
        // Setup RandomSearch static list
        RandomSearch.listStateReference = new ArrayList<>();
        
        // Create instance
        evolutionStrategies = new EvolutionStrategies();
    }

    @AfterEach
    public void tearDown() throws Exception {
        strategyStaticMock.close();
        mocks.close();
        resetSingleton();
        RandomSearch.listStateReference.clear();
    }

    private void resetSingleton() throws Exception {
        Field instance = Strategy.class.getDeclaredField("strategy");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void testGenerate() throws Exception {
        int operatorNumber = 1;
        
        // Populate listStateReference
        List<State> population = new ArrayList<>();
        State s1 = mock(State.class);
        when(s1.getCode()).thenReturn(new ArrayList<>());
        ArrayList<Double> eval = new ArrayList<>();
        eval.add(10.0);
        when(s1.getEvaluation()).thenReturn(eval);
        when(s1.getTypeGenerator()).thenReturn(GeneratorType.RandomSearch);
        population.add(s1);
        population.add(s1);
        
        Field listField = EvolutionStrategies.class.getDeclaredField("listStateReference");
        listField.setAccessible(true);
        listField.set(evolutionStrategies, population);
        
        State result = evolutionStrategies.generate(operatorNumber);
        
        assertNotNull(result);
    }
    
    @Test
    public void testGetType() {
        assertEquals(GeneratorType.EvolutionStrategies, evolutionStrategies.getType());
    }

    @Test
    public void testGetReference() throws Exception {
        List<State> population = new ArrayList<>();
        State s1 = mock(State.class);
        ArrayList<Double> eval1 = new ArrayList<>();
        eval1.add(10.0);
        when(s1.getEvaluation()).thenReturn(eval1);
        
        State s2 = mock(State.class);
        ArrayList<Double> eval2 = new ArrayList<>();
        eval2.add(5.0);
        when(s2.getEvaluation()).thenReturn(eval2);
        
        population.add(s1);
        population.add(s2);
        
        Field listField = EvolutionStrategies.class.getDeclaredField("listStateReference");
        listField.setAccessible(true);
        listField.set(evolutionStrategies, population);
        
        // Minimization
        when(problemMock.getTypeProblem()).thenReturn(Problem.ProblemType.Minimizar);
        State ref = evolutionStrategies.getReference();
        assertEquals(s2, ref);
        
        // Maximization
        when(problemMock.getTypeProblem()).thenReturn(Problem.ProblemType.Maximizar);
        ref = evolutionStrategies.getReference();
        assertEquals(s1, ref);
    }
    
    @Test
    public void testUpdateReference() throws Exception {
        List<State> population = new ArrayList<>();
        State s1 = mock(State.class);
        ArrayList<Double> eval1 = new ArrayList<>();
        eval1.add(10.0);
        when(s1.getEvaluation()).thenReturn(eval1);
        population.add(s1);
        
        Field listField = EvolutionStrategies.class.getDeclaredField("listStateReference");
        listField.setAccessible(true);
        listField.set(evolutionStrategies, population);
        
        State candidate = mock(State.class);
        ArrayList<Double> evalCandidate = new ArrayList<>();
        evalCandidate.add(5.0);
        when(candidate.getEvaluation()).thenReturn(evalCandidate);
        
        evolutionStrategies.updateReference(candidate, 1);
    }

    @Test
    public void testGetListStateRef_FromRandomSearch() {
        // Setup RandomSearch list
        List<State> rsList = new ArrayList<>();
        rsList.add(mock(State.class));
        RandomSearch.listStateReference = rsList;
        
        // Setup Strategy to have EvolutionStrategies key
        ArrayList<String> keys = new ArrayList<>();
        keys.add("EvolutionStrategies");
        when(strategyMock.getListKey()).thenReturn(keys);
        
        EvolutionStrategies otherES = mock(EvolutionStrategies.class);
        when(otherES.getListStateReference()).thenReturn(new ArrayList<>()); // Empty
        
        strategyMock.mapGenerators.put(GeneratorType.EvolutionStrategies, otherES);
        
        // Clear current list
        evolutionStrategies.setListStateReference(new ArrayList<>());
        
        List<State> result = evolutionStrategies.getListStateRef();
        
        assertEquals(1, result.size());
    }

    @Test
    public void testGetListStateRef_FromOtherGenerator() {
        // Setup Strategy to have EvolutionStrategies key
        ArrayList<String> keys = new ArrayList<>();
        keys.add("EvolutionStrategies");
        when(strategyMock.getListKey()).thenReturn(keys);
        
        EvolutionStrategies otherES = mock(EvolutionStrategies.class);
        List<State> otherList = new ArrayList<>();
        otherList.add(mock(State.class));
        when(otherES.getListStateReference()).thenReturn(otherList); // Not empty
        
        strategyMock.mapGenerators.put(GeneratorType.EvolutionStrategies, otherES);
        
        List<State> result = evolutionStrategies.getListStateRef();
        
        assertEquals(1, result.size());
        assertEquals(otherList, result);
    }

    @Test
    public void testGetSetters() {
        evolutionStrategies.setWeight(10.0f);
        assertEquals(10.0f, evolutionStrategies.getWeight());
        
        State s = mock(State.class);
        evolutionStrategies.setStateRef(s);
        
        evolutionStrategies.setInitialReference(s);
        
        evolutionStrategies.setTypeGenerator(GeneratorType.GeneticAlgorithm);
        assertEquals(GeneratorType.GeneticAlgorithm, evolutionStrategies.getTypeGenerator());
        
        assertNotNull(evolutionStrategies.getListCountBetterGender());
        assertNotNull(evolutionStrategies.getListCountGender());
        assertNotNull(evolutionStrategies.getTrace());
        assertNull(evolutionStrategies.getSonList());
        assertFalse(evolutionStrategies.awardUpdateREF(s));
    }
    
    @Test
    public void testGetReferenceList() {
        List<State> list = new ArrayList<>();
        State s1 = mock(State.class);
        list.add(s1);
        evolutionStrategies.setListStateReference(list);
        
        List<State> refList = evolutionStrategies.getReferenceList();
        assertEquals(1, refList.size());
        assertEquals(s1, refList.get(0));
    }

    @Test
    public void testGetListStateReference() {
        List<State> list = new ArrayList<>();
        State s1 = mock(State.class);
        list.add(s1);
        evolutionStrategies.setListStateReference(list);
        
        assertEquals(list, evolutionStrategies.getListStateReference());
    }
}
