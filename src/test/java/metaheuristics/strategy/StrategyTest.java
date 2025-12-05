package metaheuristics.strategy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import factory_method.FactoryGenerator;
import local_search.complement.StopExecute;
import local_search.complement.UpdateParameter;
import metaheuristics.generators.DistributionEstimationAlgorithm;
import metaheuristics.generators.EvolutionStrategies;
import metaheuristics.generators.Generator;
import metaheuristics.generators.GeneratorType;
import metaheuristics.generators.GeneticAlgorithm;
import metaheuristics.generators.MultiGenerator;
import metaheuristics.generators.ParticleSwarmOptimization;
import metaheuristics.generators.RandomSearch;
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
    void testUpdate() throws Exception {
        Strategy s = Strategy.getStrategy();
        
        // Mock FactoryGenerator
        try (MockedConstruction<FactoryGenerator> mockedFactory = Mockito.mockConstruction(FactoryGenerator.class,
                (mock, context) -> {
                    when(mock.createGenerator(any())).thenReturn(mock(Generator.class));
                })) {
            
            // Case 1: GeneticAlgorithm
            GeneticAlgorithm.countRef = 10;
            s.update(9);
            verify(mockedFactory.constructed().get(0)).createGenerator(GeneratorType.GeneticAlgorithm);
            
            // Case 2: EvolutionStrategies
            EvolutionStrategies.countRef = 20;
            s.update(19);
            verify(mockedFactory.constructed().get(1)).createGenerator(GeneratorType.EvolutionStrategies);
            
            // Case 3: DistributionEstimationAlgorithm
            DistributionEstimationAlgorithm.countRef = 30;
            s.update(29);
            verify(mockedFactory.constructed().get(2)).createGenerator(GeneratorType.DistributionEstimationAlgorithm);
            
            // Case 4: ParticleSwarmOptimization
            ParticleSwarmOptimization.countRef = 40;
            s.update(39);
            verify(mockedFactory.constructed().get(3)).createGenerator(GeneratorType.ParticleSwarmOptimization);
        }
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
        when(stateMock.clone()).thenReturn(stateMock);
        when(stateMock.Comparator(any(State.class))).thenReturn(false);
        
        // Setup Generator
        when(generatorMock.generate(anyInt())).thenReturn(stateMock);
        when(generatorMock.getReference()).thenReturn(stateMock);
        when(generatorMock.getReferenceList()).thenReturn(new ArrayList<>());
        when(generatorMock.getType()).thenReturn(GeneratorType.GeneticAlgorithm);
        
        // Inject dependencies via reflection
        setField(s, "problem", problemMock);
        setField(s, "stopexecute", stopExecuteMock);
        setField(s, "updateparameter", updateParameterMock);
        
        // Initialize mapGenerators
        s.mapGenerators = new TreeMap<>();

        // Setup StopExecute and UpdateParameter to simulate loop
        when(stopExecuteMock.stopIterations(anyInt(), anyInt())).thenReturn(false, false, false, true);
        
        // Mock FactoryGenerator construction and RandomSearch
        try (MockedConstruction<FactoryGenerator> mockedFactory = Mockito.mockConstruction(FactoryGenerator.class,
                (mock, context) -> {
                    when(mock.createGenerator(any())).thenReturn(generatorMock);
                });
             MockedConstruction<RandomSearch> mockedRandomSearch = Mockito.mockConstruction(RandomSearch.class,
                (mock, context) -> {
                    when(mock.generate(anyInt())).thenReturn(stateMock);
                });
             MockedStatic<UpdateParameter> mockedUpdateParam = mockStatic(UpdateParameter.class)) {
            
            mockedUpdateParam.when(() -> UpdateParameter.updateParameter(anyInt())).thenAnswer(i -> (Integer)i.getArguments()[0] + 1);
            
            // Execute
            s.calculateTime = true;
            s.saveListStates = true;
            s.saveListBestStates = true;
            s.saveFreneParetoMonoObjetivo = true;
            
            s.executeStrategy(10, 2, 1, GeneratorType.GeneticAlgorithm);
            
            // Verify
            assertEquals(1, mockedRandomSearch.constructed().size(), "RandomSearch should be constructed once");
            // assertEquals(1, mockedFactory.constructed().size(), "FactoryGenerator should be constructed once");
            
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
        when(stopExecuteMock.stopIterations(anyInt(), anyInt())).thenReturn(false, false, true);
        
        // Mock FactoryGenerator construction and RandomSearch
        try (MockedConstruction<FactoryGenerator> mockedFactory = Mockito.mockConstruction(FactoryGenerator.class,
                (mock, context) -> {
                    when(mock.createGenerator(any())).thenReturn(multiGeneratorMock);
                });
             MockedConstruction<RandomSearch> mockedRandomSearch = Mockito.mockConstruction(RandomSearch.class,
                (mock, context) -> {
                    when(mock.generate(anyInt())).thenReturn(stateMock);
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
            assertEquals(1, mockedRandomSearch.constructed().size(), "RandomSearch should be constructed once");
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
