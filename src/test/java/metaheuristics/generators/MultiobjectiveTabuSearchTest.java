package metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import local_search.complement.TabuSolutions;
import metaheuristics.strategy.Strategy;
import problem.definition.Problem;
import problem.definition.Problem.ProblemType;
import problem.definition.State;
import problem_operators.MutationOperator;

class MultiobjectiveTabuSearchTest {

    @Test
    void testGenerate() throws Exception {
        try (MockedStatic<Strategy> mockedStrategy = mockStatic(Strategy.class)) {
            Strategy strategy = mock(Strategy.class);
            Problem problem = mock(Problem.class);
            MutationOperator operator = mock(MutationOperator.class);
            
            mockedStrategy.when(Strategy::getStrategy).thenReturn(strategy);
            when(strategy.getProblem()).thenReturn(problem);
            when(problem.getOperator()).thenReturn(operator);
            
            MultiobjectiveTabuSearch tabuSearch = new MultiobjectiveTabuSearch();
            
            State refState = new State();
            ArrayList<Double> eval = new ArrayList<>();
            eval.add(10.0);
            refState.setEvaluation(eval);
            
            tabuSearch.setStateReferenceTS(refState);
            
            List<State> neighborhood = new ArrayList<>();
            State neighbor = new State();
            ArrayList<Double> evalN = new ArrayList<>();
            evalN.add(20.0);
            neighbor.setEvaluation(evalN);
            neighborhood.add(neighbor);
            
            when(operator.generatedNewState(refState, 1)).thenReturn(neighborhood);
            
            // Setup TabuSolutions static list
            TabuSolutions.listTabu = new ArrayList<>();
            TabuSolutions.maxelements = 10;
            
            State result = tabuSearch.generate(1);
            
            assertNotNull(result);
        }
    }
    
    @Test
    void testUpdateReference() throws Exception {
        try (MockedStatic<Strategy> mockedStrategy = mockStatic(Strategy.class)) {
            Strategy strategy = mock(Strategy.class);
            strategy.listRefPoblacFinal = new ArrayList<>();
            Problem problem = mock(Problem.class);
            
            mockedStrategy.when(Strategy::getStrategy).thenReturn(strategy);
            when(strategy.getProblem()).thenReturn(problem);
            when(problem.getTypeProblem()).thenReturn(ProblemType.Maximizar);
            
            MultiobjectiveTabuSearch tabuSearch = new MultiobjectiveTabuSearch();
            
            State refState = new State();
            ArrayList<Double> eval = new ArrayList<>();
            eval.add(10.0);
            refState.setEvaluation(eval);
            tabuSearch.setStateReferenceTS(refState);
            
            State candidate = new State();
            ArrayList<Double> evalC = new ArrayList<>();
            evalC.add(5.0); 
            candidate.setEvaluation(evalC);
            
            // Setup TabuSolutions
            TabuSolutions.listTabu = new ArrayList<>();
            TabuSolutions.maxelements = 10;
            
            tabuSearch.updateReference(candidate, 1);
        }
    }
}
