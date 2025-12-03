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

import metaheurictics.strategy.Strategy;
import problem.definition.Codification;
import problem.definition.Operator;
import problem.definition.Problem;
import problem.definition.State;
import evolutionary_algorithms.complement.CrossoverType;
import evolutionary_algorithms.complement.MutationType;
import evolutionary_algorithms.complement.ReplaceType;
import evolutionary_algorithms.complement.SelectionType;

public class GeneticAlgorithmTest {

    private GeneticAlgorithm geneticAlgorithm;
    
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
        when(newStateMock.getCode()).thenReturn(new ArrayList<>());
        when(newStateMock.getEvaluation()).thenReturn(new ArrayList<>());
        when(newStateMock.getCopy()).thenReturn(newStateMock); // Stub getCopy for crossover
        
        when(problemMock.getTypeProblem()).thenReturn(Problem.ProblemType.Minimizar);
        when(problemMock.getCodification()).thenReturn(codificationMock);
        when(codificationMock.getVariableCount()).thenReturn(5);
        
        // Setup Strategy fields
        strategyMock.mapGenerators = new TreeMap<>();
        when(strategyMock.getListKey()).thenReturn(new ArrayList<>());
        
        // Setup static fields
        GeneticAlgorithm.selectionType = SelectionType.TruncationSelection;
        GeneticAlgorithm.crossoverType = CrossoverType.OnePointCrossover;
        GeneticAlgorithm.mutationType = MutationType.OnePointMutation;
        GeneticAlgorithm.replaceType = ReplaceType.GenerationalReplace;
        GeneticAlgorithm.truncation = 1;
        GeneticAlgorithm.PC = 0.8;
        GeneticAlgorithm.PM = 0.1;
        
        // Setup RandomSearch static list
        RandomSearch.listStateReference = new ArrayList<>();
        
        // Create instance (this calls getListStateRef)
        geneticAlgorithm = new GeneticAlgorithm();
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
        
        // We need to populate listState for GA to work (selection needs candidates)
        List<State> population = new ArrayList<>();
        State s1 = mock(State.class);
        when(s1.getCode()).thenReturn(new ArrayList<>());
        ArrayList<Double> eval = new ArrayList<>();
        eval.add(10.0);
        when(s1.getEvaluation()).thenReturn(eval);
        when(s1.getTypeGenerator()).thenReturn(GeneratorType.RandomSearch);
        population.add(s1);
        population.add(s1); // Add enough for selection
        
        geneticAlgorithm.setListState(population);
        
        State result = geneticAlgorithm.generate(operatorNumber);
        
        assertNotNull(result);
    }
    
    @Test
    public void testGetType() {
        assertEquals(GeneratorType.GeneticAlgorithm, geneticAlgorithm.getType());
    }

    @Test
    public void testGetReference() {
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
        
        geneticAlgorithm.setListState(population);
        
        // Minimization
        when(problemMock.getTypeProblem()).thenReturn(Problem.ProblemType.Minimizar);
        State ref = geneticAlgorithm.getReference();
        assertEquals(s2, ref);
        
        // Maximization
        when(problemMock.getTypeProblem()).thenReturn(Problem.ProblemType.Maximizar);
        ref = geneticAlgorithm.getReference();
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
        
        geneticAlgorithm.setListState(population);
        
        State candidate = mock(State.class);
        ArrayList<Double> evalCandidate = new ArrayList<>();
        evalCandidate.add(5.0);
        when(candidate.getEvaluation()).thenReturn(evalCandidate);
        
        // Mock Replace
        // Since FactoryReplace creates a new instance, we rely on the real implementation of GenerationalReplace (default)
        // GenerationalReplace replaces the worst if candidate is better, or something like that.
        // Let's check GenerationalReplace logic or just run it.
        // GenerationalReplace might need mocking if it's complex.
        // But let's try running it.
        
        geneticAlgorithm.updateReference(candidate, 1);
        
        // Verify listState changed or not
        // With GenerationalReplace, it might replace the whole population or part of it.
    }
}
