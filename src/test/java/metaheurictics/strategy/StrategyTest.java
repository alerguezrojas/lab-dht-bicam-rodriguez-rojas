package metaheurictics.strategy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import problem.definition.Problem;
import problem.definition.State;
import problem.definition.Operator;
import local_search.complement.StopExecute;
import local_search.complement.UpdateParameter;
import metaheuristics.generators.Generator;
import metaheuristics.generators.GeneratorType;
import factory_method.FactoryLoader;
import org.mockito.MockedStatic;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;

import static org.mockito.ArgumentMatchers.contains;
import local_search.candidate_type.SearchCandidate;

class StrategyTest {

    @BeforeEach
    void setUp() throws Exception {
        resetSingleton();
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
        List<State> randomStates = new ArrayList<>();
        randomStates.add(stateMock);
        when(operatorMock.generateRandomState(anyInt())).thenReturn(randomStates);
        when(problemMock.getTypeProblem()).thenReturn(Problem.ProblemType.Minimizar);
        when(generatorMock.generate(anyInt())).thenReturn(stateMock);
        ArrayList<Double> evaluation = new ArrayList<>();
        evaluation.add(10.0);
        when(stateMock.getEvaluation()).thenReturn(evaluation);
        when(generatorMock.getReferenceList()).thenReturn(new ArrayList<>());
        
        // Setup Strategy
        s.setProblem(problemMock);
        s.setStopexecute(stopExecuteMock);
        s.setUpdateparameter(updateParameterMock);
        
        // Setup StopExecute to run once then stop
        when(stopExecuteMock.stopIterations(anyInt(), anyInt())).thenReturn(false).thenReturn(true);
        
        // Mock FactoryLoader
        try (MockedStatic<FactoryLoader> factoryLoaderMockedStatic = mockStatic(FactoryLoader.class)) {
            factoryLoaderMockedStatic.when(() -> FactoryLoader.getInstance(anyString())).thenAnswer(invocation -> {
                String className = invocation.getArgument(0);
                if (className.contains("GeneticAlgorithm")) {
                    return generatorMock;
                }
                if (className.contains("RandomCandidate")) {
                     SearchCandidate candidateMock = mock(SearchCandidate.class);
                     when(candidateMock.stateSearch(any())).thenReturn(stateMock);
                     return candidateMock;
                }
                return null;
            });
            
            // Execute
            s.executeStrategy(10, 5, 1, GeneratorType.GeneticAlgorithm);
            
            // Verify
            verify(stopExecuteMock, atLeastOnce()).stopIterations(anyInt(), anyInt());
            verify(generatorMock, atLeastOnce()).setInitialReference(any());
        }
    }

}
