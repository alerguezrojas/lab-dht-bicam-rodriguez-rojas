package metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import metaheurictics.strategy.Strategy;
import problem.definition.Problem;
import problem.definition.Problem.ProblemType;
import problem.definition.State;
import problem_operators.MutationOperator;

class TabuSearchTest {

    @Test
    void testGenerate() throws Exception {
        try (MockedStatic<Strategy> mockedStrategy = mockStatic(Strategy.class)) {
            Strategy strategy = mock(Strategy.class);
            Problem problem = mock(Problem.class);
            MutationOperator operator = mock(MutationOperator.class);
            
            mockedStrategy.when(Strategy::getStrategy).thenReturn(strategy);
            when(strategy.getProblem()).thenReturn(problem);
            when(problem.getTypeProblem()).thenReturn(ProblemType.Maximizar);
            when(problem.getOperator()).thenReturn(operator);
            
            TabuSearch tabuSearch = new TabuSearch();
            
            State refState = new State();
            // Need to set evaluation for comparisons inside CandidateValue -> SearchCandidate
            ArrayList<Double> eval = new ArrayList<>();
            eval.add(10.0);
            refState.setEvaluation(eval);
            
            tabuSearch.setInitialReference(refState);
            
            List<State> neighborhood = new ArrayList<>();
            State neighbor = new State();
            ArrayList<Double> evalN = new ArrayList<>();
            evalN.add(20.0);
            neighbor.setEvaluation(evalN);
            neighborhood.add(neighbor);
            
            when(operator.generatedNewState(refState, 1)).thenReturn(neighborhood);
            
            // We need to ensure TabuSolutions doesn't crash or filter everything out.
            // TabuSolutions uses Strategy.getStrategy().getTabuList() probably.
            // I should check TabuSolutions.
            
            State result = tabuSearch.generate(1);
            
            assertNotNull(result);
        }
    }
}
