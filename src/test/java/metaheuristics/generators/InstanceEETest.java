package metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockitoAnnotations;

import factory_method.FactoryGenerator;

public class InstanceEETest {

    private InstanceEE instanceEE;
    
    @Mock
    private Generator generatorMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        instanceEE = new InstanceEE();
    }

    @Test
    void testRun() {
        // Setup MultiGenerator static list
        Generator[] generators = new Generator[2];
        Generator mockGen1 = mock(Generator.class);
        when(mockGen1.getType()).thenReturn(GeneratorType.HillClimbing);
        Generator mockGen2 = mock(Generator.class);
        when(mockGen2.getType()).thenReturn(GeneratorType.EvolutionStrategies);
        
        generators[0] = mockGen1;
        generators[1] = mockGen2;
        
        MultiGenerator.setListGenerators(generators);
        
        // Mock FactoryGenerator
        try (MockedConstruction<FactoryGenerator> factoryMock = mockConstruction(FactoryGenerator.class,
                (mock, context) -> {
                    when(mock.createGenerator(GeneratorType.EvolutionStrategies)).thenReturn(generatorMock);
                })) {
            
            instanceEE.run();
            
            assertTrue(instanceEE.isTerminate());
            assertEquals(generatorMock, MultiGenerator.getListGenerators()[1]);
        }
    }
    
    @Test
    void testSetTerminate() {
        instanceEE.setTerminate(true);
        assertTrue(instanceEE.isTerminate());
    }
}
