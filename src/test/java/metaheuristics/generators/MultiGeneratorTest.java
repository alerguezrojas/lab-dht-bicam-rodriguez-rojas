package metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

import factory_method.FactoryGenerator;
import metaheuristics.generators.HillClimbing;
import metaheuristics.generators.EvolutionStrategies;
import metaheuristics.generators.LimitThreshold;
import metaheuristics.generators.GeneticAlgorithm;
import metaheuristics.generators.RandomSearch;
import metaheuristics.strategy.Strategy;
import problem.definition.Problem;
import problem.definition.State;

public class MultiGeneratorTest {

    private MultiGenerator mg;
    
    @Mock
    private Strategy strategyMock;
    
    @Mock
    private Problem problemMock;
    
    @Mock
    private State stateMock;
    
    @Mock
    private Generator generatorMock1;
    
    @Mock
    private Generator generatorMock2;

    private MockedStatic<Strategy> strategyStaticMock;
    private AutoCloseable mocks;

    @BeforeEach
    public void setUp() throws Exception {
        mocks = MockitoAnnotations.openMocks(this);
        
        resetSingleton();
        
        strategyStaticMock = mockStatic(Strategy.class);
        strategyStaticMock.when(Strategy::getStrategy).thenReturn(strategyMock);
        
        when(strategyMock.getProblem()).thenReturn(problemMock);
        when(problemMock.getTypeProblem()).thenReturn(Problem.ProblemType.Minimizar);
        
        strategyMock.mapGenerators = new TreeMap<>();
        when(strategyMock.getListKey()).thenReturn(new ArrayList<>());
        
        mg = new MultiGenerator();
        
        // Setup mock generators
        when(generatorMock1.getWeight()).thenReturn(50.0f);
        when(generatorMock2.getWeight()).thenReturn(50.0f);
        when(generatorMock1.getType()).thenReturn(GeneratorType.HillClimbing);
        when(generatorMock2.getType()).thenReturn(GeneratorType.RandomSearch);
        
        // Fix NPE in updateAwardSC
        when(generatorMock1.getTrace()).thenReturn(new float[10]);
        when(generatorMock2.getTrace()).thenReturn(new float[10]);
        when(generatorMock1.getListCountBetterGender()).thenReturn(new int[10]);
        when(generatorMock2.getListCountBetterGender()).thenReturn(new int[10]);
        
        Generator[] generators = new Generator[] { generatorMock1, generatorMock2 };
        MultiGenerator.setListGenerators(generators);
        
        // Reset static lists
        MultiGenerator.listGeneratedPP = new ArrayList<>();
        MultiGenerator.listStateReference = new ArrayList<>();
    }

    @AfterEach
    public void tearDown() throws Exception {
        strategyStaticMock.close();
        mocks.close();
        resetSingleton();
        MultiGenerator.destroyMultiGenerator();
    }

    private void resetSingleton() throws Exception {
        Field instance = Strategy.class.getDeclaredField("strategy");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void testGenerate() throws Exception {
        State generatedState = mock(State.class);
        when(generatorMock1.generate(anyInt())).thenReturn(generatedState);
        when(generatorMock2.generate(anyInt())).thenReturn(generatedState);
        
        // Force roulette to pick one (randomness makes it hard to guarantee which one, 
        // but we can verify one of them was called)
        
        State result = mg.generate(1);
        
        assertNotNull(result);
        assertEquals(generatedState, result);
        
        // Verify one of the generators was called
        boolean g1Called = false;
        try {
            verify(generatorMock1).generate(1);
            g1Called = true;
        } catch (AssertionError e) {}
        
        boolean g2Called = false;
        try {
            verify(generatorMock2).generate(1);
            g2Called = true;
        } catch (AssertionError e) {}
        
        assertTrue(g1Called || g2Called);
        assertNotNull(MultiGenerator.activeGenerator);
    }

    @Test
    void testInitializeListGenerator() throws Exception {
        MultiGenerator.initializeListGenerator();
        Generator[] generators = MultiGenerator.getListGenerators();
        assertNotNull(generators);
        assertEquals(4, generators.length);
        assertTrue(generators[0] instanceof HillClimbing);
        assertTrue(generators[1] instanceof EvolutionStrategies);
        assertTrue(generators[2] instanceof LimitThreshold);
        assertTrue(generators[3] instanceof GeneticAlgorithm);
    }
    
    @Test
    void testUpdateWeightSearchStateTrue() throws Exception {
        Generator activeGen = generatorMock1;
        when(activeGen.getType()).thenReturn(GeneratorType.GeneticAlgorithm);
        
        MultiGenerator.setActiveGenerator(activeGen);
        
        when(strategyMock.getCountCurrent()).thenReturn(0);
        when(strategyMock.getBestState()).thenReturn(stateMock);
        when(stateMock.getEvaluation()).thenReturn(new ArrayList<>(List.of(10.0)));
        
        State candidate = mock(State.class);
        when(candidate.getEvaluation()).thenReturn(new ArrayList<>(List.of(5.0))); // Better than 10.0
        
        mg.updateWeight(candidate);
        
        verify(activeGen).setWeight(anyFloat());
        // Verify weight increased (rewarded)
        // 50 * (1 - 0.1) + 10 = 45 + 10 = 55
        verify(activeGen).setWeight(55.0f);
    }

    @Test
    void testUpdateWeightSearchStateFalse() throws Exception {
        Generator activeGen = generatorMock1;
        when(activeGen.getType()).thenReturn(GeneratorType.GeneticAlgorithm);
        
        MultiGenerator.setActiveGenerator(activeGen);
        
        when(strategyMock.getCountCurrent()).thenReturn(0);
        when(strategyMock.getBestState()).thenReturn(stateMock);
        when(stateMock.getEvaluation()).thenReturn(new ArrayList<>(List.of(10.0)));
        
        State candidate = mock(State.class);
        when(candidate.getEvaluation()).thenReturn(new ArrayList<>(List.of(15.0))); // Worse than 10.0
        
        mg.updateWeight(candidate);
        
        verify(activeGen).setWeight(anyFloat());
        // Verify weight decreased (penalized)
        // 50 * (1 - 0.1) = 45
        verify(activeGen).setWeight(45.0f);
    }
    
    @Test
    public void testUpdateReference() throws Exception {
        // Setup active generator
        MultiGenerator.activeGenerator = generatorMock1;
        
        State candidate = mock(State.class);
        ArrayList<Double> eval = new ArrayList<>();
        eval.add(5.0);
        when(candidate.getEvaluation()).thenReturn(eval);
        
        State bestState = mock(State.class);
        ArrayList<Double> bestEval = new ArrayList<>();
        bestEval.add(10.0);
        when(bestState.getEvaluation()).thenReturn(bestEval);
        
        when(strategyMock.getBestState()).thenReturn(bestState);
        
        // Minimization: 5.0 < 10.0 -> Better
        when(problemMock.getTypeProblem()).thenReturn(Problem.ProblemType.Minimizar);
        
        mg.updateReference(candidate, 1);
        
        // Should call updateAwardSC because it's better
        verify(generatorMock1).setWeight(anyFloat());
    }
    
    @Test
    public void testGetType() {
        assertEquals(GeneratorType.MultiGenerator, mg.getType());
    }

    @Test
    public void testRoulette() {
        // Setup generators with weights
        when(generatorMock1.getWeight()).thenReturn(10.0f);
        when(generatorMock2.getWeight()).thenReturn(90.0f);
        
        Generator result = mg.roulette();
        assertNotNull(result);
        assertTrue(result == generatorMock1 || result == generatorMock2);
    }

    @Test
    public void testUpdateAwardSC() {
        MultiGenerator.activeGenerator = generatorMock1;
        when(generatorMock1.getWeight()).thenReturn(50.0f);
        when(strategyMock.getCountCurrent()).thenReturn(0);
        
        mg.updateAwardSC();
        
        // New weight = 50 * 0.9 + 10 = 45 + 10 = 55
        verify(generatorMock1).setWeight(55.0f);
    }

    @Test
    public void testUpdateAwardImp() {
        MultiGenerator.activeGenerator = generatorMock1;
        when(generatorMock1.getWeight()).thenReturn(50.0f);
        when(strategyMock.getCountCurrent()).thenReturn(0);
        
        mg.updateAwardImp();
        
        // New weight = 50 * 0.9 = 45
        verify(generatorMock1).setWeight(45.0f);
    }

    @Test
    public void testTournament() throws Exception {
        State candidate = mock(State.class);
        when(candidate.getEvaluation()).thenReturn(new ArrayList<>());
        when(candidate.getCode()).thenReturn(new ArrayList<>());
        
        mg.tournament(candidate, 1);
        
        verify(generatorMock1).updateReference(any(State.class), eq(1));
        verify(generatorMock2).updateReference(any(State.class), eq(1));
    }

    @Test
    public void testInitializeGenerators() throws Exception {
        // Setup static vars
        EvolutionStrategies.countRef = 2;
        
        // Mock FactoryGenerator construction
        try (MockedConstruction<FactoryGenerator> mockedFactory = mockConstruction(FactoryGenerator.class,
                (mock, context) -> {
                    when(mock.createGenerator(any())).thenReturn(generatorMock1);
                });
             MockedConstruction<RandomSearch> mockedRandomSearch = mockConstruction(RandomSearch.class,
                (mock, context) -> {
                    when(mock.generate(anyInt())).thenReturn(stateMock);
                    when(mock.getType()).thenReturn(GeneratorType.RandomSearch);
                })) {
            
            // Setup Strategy state
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getState()).thenReturn(stateMock);
            when(stateMock.clone()).thenReturn(stateMock); 
            
            when(stateMock.getEvaluation()).thenReturn(new ArrayList<>());
            when(stateMock.getCode()).thenReturn(new ArrayList<>());
            
            MultiGenerator.initializeGenerators();
            
            assertNotNull(MultiGenerator.getListGenerators());
            assertEquals(4, MultiGenerator.getListGenerators().length);
            assertFalse(MultiGenerator.listGeneratedPP.isEmpty());
            assertEquals(2, MultiGenerator.listGeneratedPP.size());
        }
    }
}