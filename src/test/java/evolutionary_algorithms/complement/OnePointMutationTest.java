package evolutionary_algorithms.complement;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import metaheuristics.strategy.Strategy;
import problem.definition.Codification;
import problem.definition.Problem;
import problem.definition.State;

class OnePointMutationTest {

    @BeforeEach
    void setUp() {
        Strategy strategy = Strategy.getStrategy();
        Problem problem = new Problem();
        
        Codification codification = new Codification() {
            @Override
            public boolean validState(State state) { return true; }
            @Override
            public Object getVariableAleatoryValue(int key) { return 999; } // Value to mutate to
            @Override
            public int getAleatoryKey() { return 2; } // Key to mutate
            @Override
            public int getVariableCount() { return 5; }
        };
        
        problem.setCodification(codification);
        strategy.setProblem(problem);
    }

    @Test
    void testMutationHappens() {
        OnePointMutation mutation = new OnePointMutation();
        
        State state = new State();
        ArrayList<Object> code = new ArrayList<>();
        for(int i=0; i<5; i++) code.add(0);
        state.setCode(code);
        
        // PM = 1.0 ensures mutation happens
        State mutatedState = mutation.mutation(state, 1.0);
        
        assertEquals(999, mutatedState.getCode().get(2), "Element at index 2 should be mutated to 999");
        assertEquals(0, mutatedState.getCode().get(0), "Other elements should remain unchanged");
    }

    @Test
    void testMutationDoesNotHappen() {
        OnePointMutation mutation = new OnePointMutation();
        
        State state = new State();
        ArrayList<Object> code = new ArrayList<>();
        for(int i=0; i<5; i++) code.add(0);
        state.setCode(code);
        
        // PM = -1.0 ensures mutation never happens (probM is [0,1))
        State mutatedState = mutation.mutation(state, -1.0);
        
        assertEquals(0, mutatedState.getCode().get(2), "Element at index 2 should NOT be mutated");
    }
}
