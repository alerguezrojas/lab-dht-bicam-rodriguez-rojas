package metaheuristics.strategy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import factory_interface.IFFactoryGenerator;
import factory_method.FactoryLoader;
import local_search.candidate_type.SearchCandidate;
import local_search.complement.StopExecute;
import local_search.complement.UpdateParameter;
import metaheuristics.generators.Generator;
import metaheuristics.generators.GeneratorType;
import metaheuristics.generators.GeneticAlgorithm;
import metaheuristics.generators.ParticleSwarmOptimization;
import metaheuristics.generators.EvolutionStrategies;
import metaheuristics.generators.DistributionEstimationAlgorithm;
import problem.definition.Operator;
import problem.definition.Problem;
import problem.definition.State;

class StrategyTest {

    @BeforeEach
    void setUp() throws Exception {
        resetSingleton();
        GeneticAlgorithm.countRef = 0;
        ParticleSwarmOptimization.countRef = 0;
        EvolutionStrategies.countRef = 0;
        DistributionEstimationAlgorithm.countRef = 0;
    }

    @AfterEach
    void tearDown() throws Exception {
        resetSingleton();
    }

    private void resetSingleton() throws Exception {
        Field instance = Strategy.class.getDeclaredField("strategy");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    void testGetStrategy() {
        Strategy s1 = Strategy.getStrategy();
        assertNotNull(s1);
        Strategy s2 = Strategy.getStrategy();
        assertSame(s1, s2);
    }

    @Test
    void testSetGetProblem() {
        Strategy s = Strategy.getStrategy();
        Problem p = mock(Problem.class);
        s.setProblem(p);
        assertEquals(p, s.getProblem());
    }
    
    @Test
    void testSetGetCountMax() {
        Strategy s = Strategy.getStrategy();
        s.setCountMax(100);
        assertEquals(100, s.getCountMax());
    }
    
    @Test
    void testSetGetCountCurrent() {
        Strategy s = Strategy.getStrategy();
        s.setCountCurrent(50);
        assertEquals(50, s.getCountCurrent());
    }

    @Test
    void testExecuteStrategy() throws Exception {
        Strategy s = Strategy.getStrategy();
        
        // Mock dependencies
        Problem problemMock = mock(Problem.class);
        Operator operatorMock = mock(Operator.class);
        StopExecute stopExecuteMock = mock(StopExecute.class);
        UpdateParameter updateParameterMock = mock(UpdateParameter.class);
        State stateMock = mock(State.class);
        Generator generatorMock = mock(Generator.class);
        
        // Setup Problem
        when(problemMock.getOperator()).thenReturn(operatorMock);
        when(problemMock.getTypeProblem()).thenReturn(Problem.ProblemType.Minimizar);
        when(problemMock.getState()).thenReturn(stateMock);
        
        // Setup Operator to return a list of states for RandomSearch
        List<State> randomStates = new ArrayList<>();
        randomStates.add(stateMock);
        when(operatorMock.generateRandomState(anyInt())).thenReturn(randomStates);
        
        // Setup State
        ArrayList<Double> evaluation = new ArrayList<>();
        evaluation.add(10.0);
        when(stateMock.getEvaluation()).thenReturn(evaluation);
        when(stateMock.getCopy()).thenReturn(stateMock);
        
        // Setup Generator
        when(generatorMock.generate(anyInt())).thenReturn(stateMock);
        when(generatorMock.getReferenceList()).thenReturn(new ArrayList<>());
        when(generatorMock.getType()).thenReturn(GeneratorType.GeneticAlgorithm);
        
        // Inject dependencies via reflection
        setField(s, "problem", problemMock);
        setField(s, "stopexecute", stopExecuteMock);
        setField(s, "updateparameter", updateParameterMock);
        
        // Initialize mapGenerators to avoid NPE in legacy code if mock fails
        s.mapGenerators = new TreeMap<>();

        // Setup StopExecute to run once then stop
        when(stopExecuteMock.stopIterations(anyInt(), anyInt())).thenReturn(false).thenReturn(true);
        
        // Execute
        s.calculateTime = false;
        s.saveListStates = true;
        s.saveListBestStates = true;
        s.saveFreneParetoMonoObjetivo = true;
        
        // Mock FactoryLoader
        SearchCandidate searchCandidateMock = mock(SearchCandidate.class);
        when(searchCandidateMock.stateSearch(anyList())).thenReturn(stateMock);
        
        try (MockedStatic<FactoryLoader> factoryLoaderMockedStatic = mockStatic(FactoryLoader.class)) {
             factoryLoaderMockedStatic.when(() -> FactoryLoader.getInstance(anyString())).thenAnswer(invocation -> {
                 String className = invocation.getArgument(0);
                 if (className.startsWith("local_search.candidate_type")) {
                     return searchCandidateMock;
                 }
                 return generatorMock;
             });

             s.executeStrategy(10, 5, 1, GeneratorType.GeneticAlgorithm);
        }
        
        // Verify
        verify(stopExecuteMock, atLeastOnce()).stopIterations(anyInt(), anyInt());
        verify(generatorMock, atLeastOnce()).setInitialReference(any());
        verify(generatorMock, atLeastOnce()).generate(anyInt());
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

}
