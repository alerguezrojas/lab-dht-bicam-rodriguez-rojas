package problem.extension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import metaheuristics.strategy.Strategy;
import problem.definition.ObjetiveFunction;
import problem.definition.Problem;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

class MultiObjetivoPuroTest {

    private MockedStatic<Strategy> strategyMockedStatic;
    private Strategy strategyMock;
    private Problem problemMock;

    @BeforeEach
    void setUp() {
        strategyMock = mock(Strategy.class);
        problemMock = mock(Problem.class);
        strategyMockedStatic = mockStatic(Strategy.class);
        strategyMockedStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
        when(strategyMock.getProblem()).thenReturn(problemMock);
    }

    @AfterEach
    void tearDown() {
        strategyMockedStatic.close();
    }

    @Test
    void testEvaluationStateMaximizarMaximizar() {
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Maximizar);
        
        ObjetiveFunction objFunc = mock(ObjetiveFunction.class);
        when(objFunc.getTypeProblem()).thenReturn(ProblemType.Maximizar);
        when(objFunc.evaluation(any(State.class))).thenReturn(0.8);
        
        ArrayList<ObjetiveFunction> functions = new ArrayList<>();
        functions.add(objFunc);
        when(problemMock.getFunction()).thenReturn(functions);
        
        State state = new State();
        
        MultiObjetivoPuro method = new MultiObjetivoPuro();
        method.evaluationState(state);
        
        assertEquals(0.8, state.getEvaluation().get(0));
    }
    
    @Test
    void testEvaluationStateMaximizarMinimizar() {
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Maximizar);
        
        ObjetiveFunction objFunc = mock(ObjetiveFunction.class);
        when(objFunc.getTypeProblem()).thenReturn(ProblemType.Minimizar);
        when(objFunc.evaluation(any(State.class))).thenReturn(0.2);
        
        ArrayList<ObjetiveFunction> functions = new ArrayList<>();
        functions.add(objFunc);
        when(problemMock.getFunction()).thenReturn(functions);
        
        State state = new State();
        
        MultiObjetivoPuro method = new MultiObjetivoPuro();
        method.evaluationState(state);
        
        assertEquals(0.8, state.getEvaluation().get(0), 0.0001); // 1 - 0.2 = 0.8
    }
    
    @Test
    void testEvaluationStateMinimizarMaximizar() {
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Minimizar);
        
        ObjetiveFunction objFunc = mock(ObjetiveFunction.class);
        when(objFunc.getTypeProblem()).thenReturn(ProblemType.Maximizar);
        when(objFunc.evaluation(any(State.class))).thenReturn(0.8);
        
        ArrayList<ObjetiveFunction> functions = new ArrayList<>();
        functions.add(objFunc);
        when(problemMock.getFunction()).thenReturn(functions);
        
        State state = new State();
        
        MultiObjetivoPuro method = new MultiObjetivoPuro();
        method.evaluationState(state);
        
        assertEquals(0.2, state.getEvaluation().get(0), 0.0001); // 1 - 0.8 = 0.2
    }
    
    @Test
    void testEvaluationStateMinimizarMinimizar() {
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Minimizar);
        
        ObjetiveFunction objFunc = mock(ObjetiveFunction.class);
        when(objFunc.getTypeProblem()).thenReturn(ProblemType.Minimizar);
        when(objFunc.evaluation(any(State.class))).thenReturn(0.2);
        
        ArrayList<ObjetiveFunction> functions = new ArrayList<>();
        functions.add(objFunc);
        when(problemMock.getFunction()).thenReturn(functions);
        
        State state = new State();
        
        MultiObjetivoPuro method = new MultiObjetivoPuro();
        method.evaluationState(state);
        
        assertEquals(0.2, state.getEvaluation().get(0));
    }
}
