package problem_operators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import metaheuristics.strategy.Strategy;
import problem.definition.Codification;
import problem.definition.Problem;
import problem.definition.State;

class MutationOperatorTest {

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
    void testGeneratedNewState() {
        State currentState = new State();
        ArrayList<Object> code = new ArrayList<>();
        code.add(0);
        code.add(0);
        currentState.setCode(code);
        
        when(codificationMock.getAleatoryKey()).thenReturn(0);
        when(codificationMock.getVariableAleatoryValue(0)).thenReturn(1);
        
        MutationOperator operator = new MutationOperator();
        List<State> newStates = operator.generatedNewState(currentState, 2);
        
        assertEquals(2, newStates.size());
        assertEquals(1, newStates.get(0).getCode().get(0));
        assertEquals(1, newStates.get(1).getCode().get(0));
    }
}
