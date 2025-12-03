package metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import evolutionary_algorithms.complement.MutationType;
import evolutionary_algorithms.complement.ReplaceType;
import evolutionary_algorithms.complement.SelectionType;
import metaheurictics.strategy.Strategy;
import problem.definition.Problem;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

class EvolutionStrategiesTest {

    @Test
    void testGenerate() throws Exception {
        try (MockedStatic<Strategy> mockedStrategy = mockStatic(Strategy.class)) {
            Strategy strategy = mock(Strategy.class);
            Problem problem = mock(Problem.class);
            State problemState = mock(State.class);
            
            mockedStrategy.when(Strategy::getStrategy).thenReturn(strategy);
            when(strategy.getProblem()).thenReturn(problem);
            when(problem.getState()).thenReturn(problemState);
            when(problem.getTypeProblem()).thenReturn(ProblemType.Maximizar);
            
            State copyState = new State();
            copyState.setCode(new ArrayList<>(Arrays.asList(1.0, 2.0)));
            copyState.setEvaluation(new ArrayList<>(Arrays.asList(10.0)));
            when(problemState.getCopy()).thenReturn(copyState);
            
            // Setup Strategy mapGenerators for getListStateRef
            ArrayList<String> keys = new ArrayList<>();
            keys.add(GeneratorType.EvolutionStrategies.toString());
            when(strategy.getListKey()).thenReturn(keys);
            
            SortedMap<GeneratorType, Generator> mapGenerators = new TreeMap<>();
            EvolutionStrategies existingES = mock(EvolutionStrategies.class);
            when(existingES.getListStateReference()).thenReturn(new ArrayList<>());
            mapGenerators.put(GeneratorType.EvolutionStrategies, existingES);
            strategy.mapGenerators = mapGenerators;
            
            // Setup RandomSearch.listStateReference
            State s1 = new State();
            s1.setCode(new ArrayList<>(Arrays.asList(1.0, 2.0)));
            s1.setEvaluation(new ArrayList<>(Arrays.asList(10.0)));
            RandomSearch.listStateReference = new ArrayList<>();
            RandomSearch.listStateReference.add(s1);
            RandomSearch.listStateReference.add(s1);
            
            // Setup EvolutionStrategies static fields
            EvolutionStrategies.selectionType = SelectionType.TruncationSelection;
            EvolutionStrategies.mutationType = MutationType.OnePointMutation;
            EvolutionStrategies.replaceType = ReplaceType.GenerationalReplace;
            EvolutionStrategies.truncation = 2;
            EvolutionStrategies.PM = 0.1;
            
            EvolutionStrategies es = new EvolutionStrategies();
            
            // Run generate
            State result = es.generate(1);
            
            assertNotNull(result);
        }
    }
}
