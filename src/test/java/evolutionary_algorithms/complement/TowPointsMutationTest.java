package evolutionary_algorithms.complement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import metaheurictics.strategy.Strategy;
import problem.definition.Codification;
import problem.definition.Problem;
import problem.definition.State;

class TowPointsMutationTest {

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
    void testMutation() {
        TowPointsMutation mutation = new TowPointsMutation();
        State state = new State();
        ArrayList<Object> code = new ArrayList<>();
        // Initial state: [0, 1, 2, 3, 4]
        for (int i = 0; i < 5; i++) {
            code.add(i);
        }
        state.setCode(code);

        // Mock behavior:
        // First key: 1
        // Second key: 3
        // Value for key 1 (to be put in pos 3): 10
        // Value for key 3 (to be put in pos 1): 30
        // Wait, the code says:
        // newind.getCode().set((Integer) key1, (Integer)value2);
        // newind.getCode().set((Integer) key2, (Integer)value1);
        
        when(codificationMock.getAleatoryKey())
            .thenReturn(1)
            .thenReturn(3);
            
        when(codificationMock.getVariableAleatoryValue(1)).thenReturn(10);
        when(codificationMock.getVariableAleatoryValue(3)).thenReturn(30);

        State mutatedState = mutation.mutation(state, 0.5);

        // Expected behavior:
        // key1 = 1, key2 = 3
        // value1 = 10, value2 = 30
        // code.set(1, 30) -> index 1 becomes 30
        // code.set(3, 10) -> index 3 becomes 10
        // Original: [0, 1, 2, 3, 4]
        // Expected: [0, 30, 2, 10, 4]

        List<Object> mutatedCode = mutatedState.getCode();
        assertEquals(0, mutatedCode.get(0));
        assertEquals(30, mutatedCode.get(1));
        assertEquals(2, mutatedCode.get(2));
        assertEquals(10, mutatedCode.get(3));
        assertEquals(4, mutatedCode.get(4));
    }
}