package local_search.complement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import factory_method.FactoryLoader;
import metaheuristics.strategy.Strategy;
import metaheuristics.generators.DistributionEstimationAlgorithm;
import metaheuristics.generators.EvolutionStrategies;
import metaheuristics.generators.Generator;
import metaheuristics.generators.GeneticAlgorithm;
import metaheuristics.generators.ParticleSwarmOptimization;

class UpdateParameterTest {

    private MockedStatic<Strategy> strategyMockedStatic;
    private MockedStatic<FactoryLoader> factoryLoaderMockedStatic;
    private Strategy strategyMock;
    private Generator generatorMock;

    @BeforeEach
    void setUp() {
        strategyMock = mock(Strategy.class);
        generatorMock = mock(Generator.class);
        
        // Mock Strategy singleton
        strategyMockedStatic = mockStatic(Strategy.class);
        strategyMockedStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
        
        // Mock FactoryLoader to avoid real instantiation of Generators
        factoryLoaderMockedStatic = mockStatic(FactoryLoader.class);
        factoryLoaderMockedStatic.when(() -> FactoryLoader.getInstance(anyString())).thenReturn(generatorMock);
    }

    @AfterEach
    void tearDown() {
        strategyMockedStatic.close();
        factoryLoaderMockedStatic.close();
    }

    @Test
    void testConstructor() {
        new UpdateParameter();
    }

    @Test
    void testUpdateParameterGeneticAlgorithm() throws Exception {
        // Setup
        int countRef = 10;
        GeneticAlgorithm.countRef = countRef;
        
        // countIterationsCurrent + 1 == countRef - 1
        // countIterationsCurrent == countRef - 2
        int currentIterations = countRef - 2;
        
        // Execute
        int result = UpdateParameter.updateParameter(currentIterations);
        
        // Verify
        assertEquals(currentIterations + 1, result);
        // Verify that generator was updated
        // Since strategyMock.generator is a field, we can't verify set easily with Mockito on a mock object's field unless we verify the side effect or if Strategy was a real object with injected mocks.
        // But Strategy is a mock. Accessing public field on a mock?
        // Mockito mocks don't hold state for fields by default.
        // Wait, Strategy.generator is a public field.
        // If 'strategyMock' is a mock, accessing 'strategyMock.generator' will return null (or default) and setting it might not do anything useful if we want to verify it was set.
        
        // Actually, if UpdateParameter does: Strategy.getStrategy().generator = ...
        // And Strategy.getStrategy() returns strategyMock.
        // Then it does strategyMock.generator = ...
        // Since strategyMock is a Mockito mock, setting a field on it is just setting a field on the proxy instance.
        // We can verify the value of the field if we read it back?
        // Or we can verify that FactoryLoader was called.
        
        factoryLoaderMockedStatic.verify(() -> FactoryLoader.getInstance(contains("GeneticAlgorithm")));
    }
    
    @Test
    void testUpdateParameterEvolutionStrategies() throws Exception {
        int countRef = 20;
        EvolutionStrategies.countRef = countRef;
        
        int currentIterations = countRef - 2;
        
        UpdateParameter.updateParameter(currentIterations);
        
        factoryLoaderMockedStatic.verify(() -> FactoryLoader.getInstance(contains("EvolutionStrategies")));
    }

    @Test
    void testUpdateParameterDistributionEstimationAlgorithm() throws Exception {
        int countRef = 30;
        DistributionEstimationAlgorithm.countRef = countRef;
        
        int currentIterations = countRef - 2;
        
        UpdateParameter.updateParameter(currentIterations);
        
        factoryLoaderMockedStatic.verify(() -> FactoryLoader.getInstance(contains("DistributionEstimationAlgorithm")));
    }
    
    @Test
    void testUpdateParameterParticleSwarmOptimization() throws Exception {
        int countRef = 40;
        ParticleSwarmOptimization.countRef = countRef;
        
        int currentIterations = countRef - 2;
        
        UpdateParameter.updateParameter(currentIterations);
        
        factoryLoaderMockedStatic.verify(() -> FactoryLoader.getInstance(contains("ParticleSwarmOptimization")));
    }
    
    @Test
    void testUpdateParameterNoMatch() throws Exception {
        // Ensure no match
        GeneticAlgorithm.countRef = 100;
        EvolutionStrategies.countRef = 100;
        DistributionEstimationAlgorithm.countRef = 100;
        ParticleSwarmOptimization.countRef = 100;
        
        int currentIterations = 10; 
        // 10 + 1 = 11 != 99
        
        int result = UpdateParameter.updateParameter(currentIterations);
        
        assertEquals(11, result);
        factoryLoaderMockedStatic.verifyNoInteractions();
    }
}
