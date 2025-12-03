package metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import metaheurictics.strategy.Strategy;
import problem.definition.Problem;
import problem.definition.State;
import problem_operators.MutationOperator;

class RandomSearchTest {

    @Test
    void testGenerate() throws Exception {
        try (MockedStatic<Strategy> mockedStrategy = mockStatic(Strategy.class)) {
            Strategy strategy = mock(Strategy.class);
            Problem problem = mock(Problem.class);
            MutationOperator operator = mock(MutationOperator.class);
            
            mockedStrategy.when(Strategy::getStrategy).thenReturn(strategy);
            when(strategy.getProblem()).thenReturn(problem);
            when(problem.getOperator()).thenReturn(operator);
            
            RandomSearch rs = new RandomSearch();
            
            State refState = new State();
            rs.setInitialReference(refState);
            
            List<State> randomStates = new ArrayList<>();
            State s1 = new State();
            s1.setEvaluation(new ArrayList<>());
            s1.getEvaluation().add(10.0);
            randomStates.add(s1);
            
            when(operator.generateRandomState(1)).thenReturn(randomStates);
            
            // Run generate
            State result = rs.generate(1);
            
            assertNotNull(result);
        }
    }
}
