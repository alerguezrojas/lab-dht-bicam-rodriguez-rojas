package factory_method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.TreeMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import metaheuristics.strategy.Strategy;
import metaheuristics.generators.Generator;
import metaheuristics.generators.GeneratorType;
import metaheuristics.generators.GeneticAlgorithm;
import metaheuristics.generators.RandomSearch;

class FactoryGeneratorTest {

    private MockedStatic<Strategy> strategyMockedStatic;
    private Strategy strategyMock;

    @BeforeEach
    void setUp() {
        strategyMock = mock(Strategy.class);
        strategyMock.mapGenerators = new TreeMap<>(); // Initialize mapGenerators
        strategyMockedStatic = mockStatic(Strategy.class);
        strategyMockedStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
        when(strategyMock.getListKey()).thenReturn(new ArrayList<>());
    }

    @AfterEach
    void tearDown() {
        strategyMockedStatic.close();
    }

    @Test
    void testCreateGenerator() throws Exception {
        FactoryGenerator factory = new FactoryGenerator();
        
        Generator g1 = factory.createGenerator(GeneratorType.GeneticAlgorithm);
        assertNotNull(g1);
        assertTrue(g1 instanceof GeneticAlgorithm);
        
        Generator g2 = factory.createGenerator(GeneratorType.RandomSearch);
        assertNotNull(g2);
        assertTrue(g2 instanceof RandomSearch);
    }
}
