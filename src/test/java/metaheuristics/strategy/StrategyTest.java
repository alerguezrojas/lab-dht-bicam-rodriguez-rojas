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

import metaheuristics.generators.MultiGenerator;
import org.mockito.MockedStatic;
import factory_method.FactoryGenerator;
import factory_method.FactoryLoader;
import org.mockito.MockedConstruction;
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
        
        // Setup Operator
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
        
        // Setup State
        // ArrayList<Double> evaluation = new ArrayList<>(); // Already declared above? No, I see it in previous read_file.
        // Let's check previous read_file.
        // Yes, it was there.
        
        // I will just remove the duplicate lines.
        
        when(stateMock.getCopy()).thenReturn(stateMock);
        when(stateMock.clone()).thenReturn(stateMock);
        when(stateMock.Comparator(any(State.class))).thenReturn(false);

        // Inject dependencies via reflection
        setField(s, "problem", problemMock);
        setField(s, "stopexecute", stopExecuteMock);
        setField(s, "updateparameter", updateParameterMock);
        
        // Initialize mapGenerators
        s.mapGenerators = new TreeMap<>();

        // Setup StopExecute
        when(stopExecuteMock.stopIterations(anyInt(), anyInt())).thenReturn(false).thenReturn(true);
        
        // Mock FactoryGenerator construction
        try (MockedConstruction<FactoryGenerator> mockedFactory = org.mockito.Mockito.mockConstruction(FactoryGenerator.class,
                (mock, context) -> {
                    when(mock.createGenerator(any())).thenReturn(generatorMock);
                })) {
            
            // Execute
            s.calculateTime = false;
            s.saveListStates = true;
            s.saveListBestStates = true;
            s.saveFreneParetoMonoObjetivo = true;
            
            s.executeStrategy(10, 5, 1, GeneratorType.GeneticAlgorithm);
            
            // Verify
            verify(stopExecuteMock, atLeastOnce()).stopIterations(anyInt(), anyInt());
            verify(generatorMock, atLeastOnce()).setInitialReference(any());
            verify(generatorMock, atLeastOnce()).generate(anyInt());
        }
    }

    @Test
    void testExecuteStrategyMultiGenerator() throws Exception {
        Strategy s = Strategy.getStrategy();
        
        // Mock dependencies
        Problem problemMock = mock(Problem.class);
        Operator operatorMock = mock(Operator.class);
        StopExecute stopExecuteMock = mock(StopExecute.class);
        UpdateParameter updateParameterMock = mock(UpdateParameter.class);
        State stateMock = mock(State.class);
        MultiGenerator multiGeneratorMock = mock(MultiGenerator.class);
        
        // Setup Problem
        when(problemMock.getOperator()).thenReturn(operatorMock);
        when(problemMock.getTypeProblem()).thenReturn(Problem.ProblemType.Minimizar);
        when(problemMock.getState()).thenReturn(stateMock);
        
        // Setup Operator
        List<State> randomStates = new ArrayList<>();
        randomStates.add(stateMock);
        when(operatorMock.generateRandomState(anyInt())).thenReturn(randomStates);
        
        // Setup State
        ArrayList<Double> evaluation = new ArrayList<>();
        evaluation.add(10.0);
        when(stateMock.getEvaluation()).thenReturn(evaluation);
        when(stateMock.getCopy()).thenReturn(stateMock);
        when(stateMock.clone()).thenReturn(stateMock);
        when(stateMock.Comparator(any(State.class))).thenReturn(false);
        
        // Setup MultiGenerator
        when(multiGeneratorMock.generate(anyInt())).thenReturn(stateMock);
        when(multiGeneratorMock.getReferenceList()).thenReturn(new ArrayList<>());
        when(multiGeneratorMock.getType()).thenReturn(GeneratorType.MultiGenerator);
        when(multiGeneratorMock.clone()).thenReturn(multiGeneratorMock);
        
        // Setup SubGenerator for MultiGenerator list
        Generator subGeneratorMock = mock(Generator.class);
        when(subGeneratorMock.getType()).thenReturn(GeneratorType.GeneticAlgorithm);
        when(subGeneratorMock.getListCountGender()).thenReturn(new int[10]);
        when(subGeneratorMock.getListCountBetterGender()).thenReturn(new int[10]);

        // Inject dependencies via reflection
        setField(s, "problem", problemMock);
        setField(s, "stopexecute", stopExecuteMock);
        setField(s, "updateparameter", updateParameterMock);
        
        // Initialize mapGenerators
        s.mapGenerators = new TreeMap<>();

        // Setup StopExecute
        // Run loop twice to trigger "change detected" logic if we set countIterationsChange small enough
        when(stopExecuteMock.stopIterations(anyInt(), anyInt())).thenReturn(false, false, true);
        
        // Mock FactoryGenerator construction
        try (MockedConstruction<FactoryGenerator> mockedFactory = org.mockito.Mockito.mockConstruction(FactoryGenerator.class,
                (mock, context) -> {
                    when(mock.createGenerator(any())).thenReturn(multiGeneratorMock);
                });
             MockedStatic<MultiGenerator> mockedStaticMultiGenerator = mockStatic(MultiGenerator.class)) {
            
            // Setup static mocks
            mockedStaticMultiGenerator.when(MultiGenerator::initializeGenerators).thenAnswer(invocation -> null);
            mockedStaticMultiGenerator.when(MultiGenerator::getListGenerators).thenReturn(new Generator[] { subGeneratorMock });
            
            // Set activeGenerator
            MultiGenerator.activeGenerator = subGeneratorMock;
            
            // Set listStateReference
            MultiGenerator.listStateReference = new ArrayList<>();
            MultiGenerator.listStateReference.add(stateMock);

            // Execute
            s.calculateTime = true;
            s.saveListStates = true;
            s.saveListBestStates = true;
            s.saveFreneParetoMonoObjetivo = false;
            
            // countIterationsChange = 1 to trigger change logic immediately
            s.executeStrategy(10, 1, 1, GeneratorType.MultiGenerator);
            
            // Verify
            verify(stopExecuteMock, atLeastOnce()).stopIterations(anyInt(), anyInt());
            verify(multiGeneratorMock, atLeastOnce()).setInitialReference(any());
            verify(multiGeneratorMock, atLeastOnce()).generate(anyInt());
            mockedStaticMultiGenerator.verify(MultiGenerator::initializeGenerators);
        }
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

}
