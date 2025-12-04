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

public class InstanceDETest {

    private InstanceDE instanceDE;
    
    @Mock
    private Generator generatorMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        instanceDE = new InstanceDE();
    }

    @Test
    void testRun() {
        // Setup MultiGenerator static list
        Generator[] generators = new Generator[2];
        Generator mockGen1 = mock(Generator.class);
        when(mockGen1.getType()).thenReturn(GeneratorType.HillClimbing);
        Generator mockGen2 = mock(Generator.class);
        when(mockGen2.getType()).thenReturn(GeneratorType.DistributionEstimationAlgorithm);
        
        generators[0] = mockGen1;
        generators[1] = mockGen2;
        
        MultiGenerator.setListGenerators(generators);
        
        // Mock FactoryGenerator
        try (MockedConstruction<FactoryGenerator> factoryMock = mockConstruction(FactoryGenerator.class,
                (mock, context) -> {
                    when(mock.createGenerator(GeneratorType.DistributionEstimationAlgorithm)).thenReturn(generatorMock);
                })) {
            
            instanceDE.run();
            
            assertTrue(instanceDE.isTerminate());
            assertEquals(generatorMock, MultiGenerator.getListGenerators()[1]);
        }
    }
    
    @Test
    void testSetTerminate() {
        instanceDE.setTerminate(true);
        assertTrue(instanceDE.isTerminate());
    }
}
