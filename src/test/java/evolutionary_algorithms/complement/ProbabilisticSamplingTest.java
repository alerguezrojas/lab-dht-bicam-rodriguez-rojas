package evolutionary_algorithms.complement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import metaheuristics.strategy.Strategy;
import metaheuristics.generators.GeneratorType;
import problem.definition.Codification;
import problem.definition.Problem;
import problem.definition.State;

class ProbabilisticSamplingTest {

    private MockedStatic<Strategy> strategyMockedStatic;
    private Strategy strategyMock;
    private Problem problemMock;
    private Codification codificationMock;

    @BeforeEach
    void setUp() {
        strategyMock = mock(Strategy.class);
        problemMock = mock(Problem.class);
        codificationMock = mock(Codification.class);

        strategyMockedStatic = mockStatic(Strategy.class);
        strategyMockedStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
        when(strategyMock.getProblem()).thenReturn(problemMock);
        when(problemMock.getCodification()).thenReturn(codificationMock);
    }

    @AfterEach
    void tearDown() {
        strategyMockedStatic.close();
    }

    @Test
    void testSampling() {
        ProbabilisticSampling sampling = new ProbabilisticSampling();
        List<State> fathers = new ArrayList<>();
        
        // Create fathers
        // Father 1: [0, 1]
        State f1 = new State();
        ArrayList<Object> c1 = new ArrayList<>();
        c1.add(0);
        c1.add(1);
        f1.setCode(c1);
        fathers.add(f1);
        
        // Father 2: [0, 2]
        State f2 = new State();
        ArrayList<Object> c2 = new ArrayList<>();
        c2.add(0);
        c2.add(2);
        f2.setCode(c2);
        fathers.add(f2);
        
        when(problemMock.getPossibleValue()).thenReturn(3); // Values 0, 1, 2
        when(strategyMock.getCountCurrent()).thenReturn(1);
        
        List<State> offspring = sampling.sampling(fathers, 2);
        
        assertNotNull(offspring);
        assertEquals(2, offspring.size());
        assertEquals(2, offspring.get(0).getCode().size());
        assertEquals(GeneratorType.DistributionEstimationAlgorithm, offspring.get(0).getTypeGenerator());
    }
}
