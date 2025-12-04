package evolutionary_algorithms.complement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import config.tspDynamic.TSPState;
import metaheuristics.strategy.Strategy;
import problem.definition.Codification;
import problem.definition.Problem;
import problem.definition.State;

class AIOMutationTest {

    private MockedStatic<Strategy> strategyMockedStatic;
    private Strategy strategyMock;
    private Problem problemMock;
    private Codification codificationMock;

    @BeforeEach
    void setUp() {
        AIOMutation.path.clear();
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
        AIOMutation mutation = new AIOMutation();
        State state = new State();
        ArrayList<Object> code = new ArrayList<>();
        
        // Create TSPStates
        for (int i = 0; i < 5; i++) {
            TSPState tspState = new TSPState();
            tspState.setValue(i);
            tspState.setIdCity(i);
            code.add(tspState);
        }
        state.setCode(code);

        // Mock random keys
        when(codificationMock.getAleatoryKey()).thenReturn(1, 3); // Swap between index 1 and 3

        State mutatedState = mutation.mutation(state, 0.5);

        assertNotNull(mutatedState);
        assertEquals(5, mutatedState.getCode().size());
    }
    
    @Test
    void testFillPath() {
        when(codificationMock.getVariableCount()).thenReturn(5);
        AIOMutation.path.clear();
        AIOMutation.fillPath();
        assertEquals(5, AIOMutation.path.size());
    }
}
