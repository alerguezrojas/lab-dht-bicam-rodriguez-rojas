package metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import metaheurictics.strategy.Strategy;
import problem.definition.Operator;
import problem.definition.Problem;
import problem.definition.State;

public class SimulatedAnnealingTest {

    private SimulatedAnnealing simulatedAnnealing;
    
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
        when(problemMock.getTypeProblem()).thenReturn(Problem.ProblemType.Minimizar);
        
        simulatedAnnealing = new SimulatedAnnealing();
        
        // Set static parameters for SA
        SimulatedAnnealing.alpha = 0.9;
        SimulatedAnnealing.tinitial = 100.0;
        SimulatedAnnealing.tfinal = 1.0;
        SimulatedAnnealing.countIterationsT = 10;
    }

    @AfterEach
    public void tearDown() throws Exception {
        strategyStaticMock.close();
        mocks.close();
        resetSingleton();
    }

    private void resetSingleton() throws Exception {
        Field instance = Strategy.class.getDeclaredField("strategy");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void testGenerate() throws Exception {
        int operatorNumber = 1;
        List<State> neighborhood = new ArrayList<>();
        neighborhood.add(newStateMock);
        
        simulatedAnnealing.setInitialReference(stateMock);
        
        when(operatorMock.generatedNewState(stateMock, operatorNumber)).thenReturn(neighborhood);
        
        State result = simulatedAnnealing.generate(operatorNumber);
        
        assertNotNull(result);
        assertEquals(newStateMock, result);
        verify(operatorMock).generatedNewState(stateMock, operatorNumber);
    }
    
    @Test
    public void testSetAndGetReference() {
        simulatedAnnealing.setInitialReference(stateMock);
        assertEquals(stateMock, simulatedAnnealing.getReference());
    }
    
    @Test
    public void testGetType() {
        assertEquals(GeneratorType.SimulatedAnnealing, simulatedAnnealing.getType());
    }
    
    @Test
    public void testUpdateReference() throws Exception {
        simulatedAnnealing.setInitialReference(stateMock);
        
        ArrayList<Double> evalRef = new ArrayList<>();
        evalRef.add(10.0);
        when(stateMock.getEvaluation()).thenReturn(evalRef);
        
        ArrayList<Double> evalNew = new ArrayList<>();
        evalNew.add(5.0);
        when(newStateMock.getEvaluation()).thenReturn(evalNew);
        
        simulatedAnnealing.updateReference(newStateMock, 1);
    }
}
