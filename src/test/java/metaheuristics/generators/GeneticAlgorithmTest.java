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
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import metaheuristics.strategy.Strategy;
import problem.definition.Codification;
import problem.definition.Operator;
import problem.definition.Problem;
import problem.definition.State;
import evolutionary_algorithms.complement.Crossover;
import evolutionary_algorithms.complement.CrossoverType;
import evolutionary_algorithms.complement.FatherSelection;
import evolutionary_algorithms.complement.Mutation;
import evolutionary_algorithms.complement.MutationType;
import evolutionary_algorithms.complement.Replace;
import evolutionary_algorithms.complement.ReplaceType;
import evolutionary_algorithms.complement.SelectionType;
import factory_method.FactoryCrossover;
import factory_method.FactoryFatherSelection;
import factory_method.FactoryMutation;
import factory_method.FactoryReplace;

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
    
    @Mock
    private FatherSelection fatherSelectionMock;
    
    @Mock
    private Crossover crossoverMock;
    
    @Mock
    private Mutation mutationMock;
    
    @Mock
    private Replace replaceMock;

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
        
        // Use real State objects instead of mocks for data holders to avoid side-effect issues
        State realState = new State();
        realState.setCode(new ArrayList<>());
        realState.setEvaluation(new ArrayList<>());
        
        when(problemMock.getState()).thenReturn(realState);
        // when(stateMock.getCopy()).thenReturn(newStateMock); // No longer needed if we use real objects
        
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
        ArrayList<Object> code = new ArrayList<>();
        code.add(1);
        code.add(0);
        when(s1.getCode()).thenReturn(code);
        ArrayList<Double> eval = new ArrayList<>();
        eval.add(10.0);
        when(s1.getEvaluation()).thenReturn(eval);
        when(s1.getTypeGenerator()).thenReturn(GeneratorType.RandomSearch);
        when(s1.getCopy()).thenReturn(s1);
        population.add(s1);
        population.add(s1); // Add enough for selection
        
        geneticAlgorithm.setListState(population);
        
        try (MockedConstruction<FactoryFatherSelection> mockedFatherFactory = mockConstruction(FactoryFatherSelection.class,
                (mock, context) -> {
                    when(mock.createSelectFather(any())).thenReturn(fatherSelectionMock);
                });
             MockedConstruction<FactoryCrossover> mockedCrossoverFactory = mockConstruction(FactoryCrossover.class,
                (mock, context) -> {
                    when(mock.createCrossover(any())).thenReturn(crossoverMock);
                });
             MockedConstruction<FactoryMutation> mockedMutationFactory = mockConstruction(FactoryMutation.class,
                (mock, context) -> {
                    when(mock.createMutation(any())).thenReturn(mutationMock);
                })) {
            
            List<State> fathers = new ArrayList<>();
            fathers.add(s1);
            fathers.add(s1);
            when(fatherSelectionMock.selection(anyList(), anyInt())).thenReturn(fathers);
            
            when(crossoverMock.crossover(any(State.class), any(State.class), anyDouble())).thenReturn(s1);
            
            when(mutationMock.mutation(any(State.class), anyDouble())).thenReturn(s1);
            
            State result = geneticAlgorithm.generate(operatorNumber);
            
            assertNotNull(result);
        }
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
        State best = geneticAlgorithm.getReference();
        assertEquals(s2, best);
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
        
        try (MockedConstruction<FactoryReplace> mockedReplaceFactory = mockConstruction(FactoryReplace.class,
                (mock, context) -> {
                    when(mock.createReplace(any())).thenReturn(replaceMock);
                })) {
            
            List<State> updatedList = new ArrayList<>();
            updatedList.add(candidate);
            when(replaceMock.replace(any(State.class), anyList())).thenReturn(updatedList);
            
            geneticAlgorithm.updateReference(candidate, 1);
            
            verify(replaceMock).replace(any(State.class), anyList());
        }
    }

    @Test
    public void testGetListStateRef_FromRandomSearch() {
        // Setup RandomSearch list
        List<State> rsList = new ArrayList<>();
        rsList.add(mock(State.class));
        RandomSearch.listStateReference = rsList;
        
        // Setup Strategy to have GeneticAlgorithm key
        ArrayList<String> keys = new ArrayList<>();
        keys.add("GeneticAlgorithm");
        when(strategyMock.getListKey()).thenReturn(keys);
        
        GeneticAlgorithm otherGA = mock(GeneticAlgorithm.class);
        when(otherGA.getListState()).thenReturn(new ArrayList<>()); // Empty
        
        strategyMock.mapGenerators.put(GeneratorType.GeneticAlgorithm, otherGA);
        
        // Clear current list
        geneticAlgorithm.setListState(new ArrayList<>());
        
        List<State> result = geneticAlgorithm.getListStateRef();
        
        assertEquals(1, result.size());
    }

    @Test
    public void testGetListStateRef_FromOtherGenerator() {
        // Setup Strategy to have GeneticAlgorithm key
        ArrayList<String> keys = new ArrayList<>();
        keys.add("GeneticAlgorithm");
        when(strategyMock.getListKey()).thenReturn(keys);
        
        GeneticAlgorithm otherGA = mock(GeneticAlgorithm.class);
        List<State> otherList = new ArrayList<>();
        otherList.add(mock(State.class));
        when(otherGA.getListState()).thenReturn(otherList); // Not empty
        
        strategyMock.mapGenerators.put(GeneratorType.GeneticAlgorithm, otherGA);
        
        List<State> result = geneticAlgorithm.getListStateRef();
        
        assertEquals(1, result.size());
        assertEquals(otherList, result);
    }

    @Test
    public void testGetSetters() {
        geneticAlgorithm.setWeight(10.0f);
        assertEquals(10.0f, geneticAlgorithm.getWeight());
        
        State s = mock(State.class);
        geneticAlgorithm.setStateRef(s);
        
        geneticAlgorithm.setInitialReference(s);
        
        geneticAlgorithm.setGeneratorType(GeneratorType.EvolutionStrategies);
        assertEquals(GeneratorType.EvolutionStrategies, geneticAlgorithm.getGeneratorType());
        
        assertNotNull(geneticAlgorithm.getListCountBetterGender());
        assertNotNull(geneticAlgorithm.getListCountGender());
        assertNotNull(geneticAlgorithm.getTrace());
        assertNull(geneticAlgorithm.getSonList());
        assertFalse(geneticAlgorithm.awardUpdateREF(s));
        
        assertEquals(GeneratorType.EvolutionStrategies, geneticAlgorithm.getType()); // getType returns this.generatorType
    }
    
    @Test
    public void testGetReferenceList() {
        List<State> list = new ArrayList<>();
        State s1 = mock(State.class);
        list.add(s1);
        geneticAlgorithm.setListState(list);
        
        List<State> refList = geneticAlgorithm.getReferenceList();
        assertEquals(1, refList.size());
        assertEquals(s1, refList.get(0));
    }
    
    @Test
    public void testGetListState() {
        List<State> list = new ArrayList<>();
        State s1 = mock(State.class);
        list.add(s1);
        geneticAlgorithm.setListState(list);
        
        assertEquals(list, geneticAlgorithm.getListState());
    }
}
