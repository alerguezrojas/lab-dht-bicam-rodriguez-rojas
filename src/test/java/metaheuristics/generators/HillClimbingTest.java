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

public class HillClimbingTest {

    private HillClimbing hillClimbing;
    
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
        
        hillClimbing = new HillClimbing();
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
        
        // Set initial reference
        hillClimbing.setInitialReference(stateMock);
        
        // Mock Operator behavior
        when(operatorMock.generatedNewState(stateMock, operatorNumber)).thenReturn(neighborhood);
        
        State result = hillClimbing.generate(operatorNumber);
        
        assertNotNull(result);
        assertEquals(newStateMock, result);
        verify(operatorMock).generatedNewState(stateMock, operatorNumber);
    }
    
    @Test
    public void testSetAndGetReference() {
        hillClimbing.setInitialReference(stateMock);
        assertEquals(stateMock, hillClimbing.getReference());
    }
    
    @Test
    public void testGetType() {
        assertEquals(GeneratorType.HillClimbing, hillClimbing.getType());
    }
    
    @Test
    public void testUpdateReference() throws Exception {
        hillClimbing.setInitialReference(stateMock);
        
        ArrayList<Double> evalRef = new ArrayList<>();
        evalRef.add(10.0);
        when(stateMock.getEvaluation()).thenReturn(evalRef);
        
        ArrayList<Double> evalNew = new ArrayList<>();
        evalNew.add(5.0);
        when(newStateMock.getEvaluation()).thenReturn(evalNew);
        
        hillClimbing.updateReference(newStateMock, 1);
        
        // Verify reference updated (since 5 < 10 and we are minimizing)
        assertEquals(newStateMock, hillClimbing.getReference());
    }
}
