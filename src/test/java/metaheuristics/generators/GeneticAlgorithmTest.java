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

import evolutionary_algorithms.complement.CrossoverType;
import evolutionary_algorithms.complement.MutationType;
import evolutionary_algorithms.complement.ReplaceType;
import evolutionary_algorithms.complement.SelectionType;
import metaheurictics.strategy.Strategy;
import problem.definition.Problem;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

class GeneticAlgorithmTest {

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
            keys.add(GeneratorType.GeneticAlgorithm.toString());
            when(strategy.getListKey()).thenReturn(keys);
            
            SortedMap<GeneratorType, Generator> mapGenerators = new TreeMap<>();
            GeneticAlgorithm existingGA = mock(GeneticAlgorithm.class);
            when(existingGA.getListState()).thenReturn(new ArrayList<>());
            mapGenerators.put(GeneratorType.GeneticAlgorithm, existingGA);
            strategy.mapGenerators = mapGenerators;
            
            // Setup RandomSearch.listStateReference
            State s1 = new State();
            s1.setCode(new ArrayList<>(Arrays.asList(1.0, 2.0)));
            s1.setEvaluation(new ArrayList<>(Arrays.asList(10.0)));
            RandomSearch.listStateReference = new ArrayList<>();
            RandomSearch.listStateReference.add(s1);
            RandomSearch.listStateReference.add(s1); // Add enough for selection
            
            // Setup GeneticAlgorithm static fields
            GeneticAlgorithm.selectionType = SelectionType.TruncationSelection;
            GeneticAlgorithm.crossoverType = CrossoverType.UniformCrossover;
            GeneticAlgorithm.mutationType = MutationType.OnePointMutation;
            GeneticAlgorithm.replaceType = ReplaceType.GenerationalReplace;
            GeneticAlgorithm.truncation = 2;
            GeneticAlgorithm.PC = 0.9;
            GeneticAlgorithm.PM = 0.1;
            
            GeneticAlgorithm ga = new GeneticAlgorithm();
            
            // Run generate
            State result = ga.generate(1);
            
            assertNotNull(result);
        }
    }
}
