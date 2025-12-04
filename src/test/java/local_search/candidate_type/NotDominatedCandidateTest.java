package local_search.candidate_type;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import metaheuristics.strategy.Strategy;
import problem.definition.Problem;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

class NotDominatedCandidateTest {

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
    void testStateSearch() throws Exception {
        NotDominatedCandidate candidate = new NotDominatedCandidate();
        List<State> neighborhood = new ArrayList<>();
        
        // Problem Type Maximizar
        when(problemMock.getTypeProblem()).thenReturn(ProblemType.Maximizar);
        
        State s1 = mock(State.class);
        when(s1.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(10.0)));
        
        State s2 = mock(State.class);
        when(s2.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(20.0))); // Dominates s1
        
        State s3 = mock(State.class);
        when(s3.getEvaluation()).thenReturn(new ArrayList<>(Arrays.asList(15.0))); // Dominated by s2
        
        neighborhood.add(s1);
        neighborhood.add(s2);
        neighborhood.add(s3);
        
        // NotDominatedCandidate logic:
        // Starts with s1 (stateA).
        // Checks s2 against s1. s2 dominates s1? Yes (20 > 10). stateA becomes s2.
        // Checks s3 against s2. s3 dominates s2? No (15 < 20).
        // Loop continues?
        // while(stop == false)
        //   stateB = listNeighborhood.get(i)
        //   if(dominance(stateB, stateA)) -> stateA = stateB
        //   else -> stop = true; state = stateA
        
        // i=1: stateB=s2. s2 dominates s1. stateA = s2. Loop continues (stop is false).
        // i=1 loop repeats? No, it's a while loop inside for loop.
        // Wait, the while loop condition is `stop == false`.
        // Inside while:
        //   stateB = listNeighborhood.get(i);
        //   if s2 dominates s1: stateA = s2. stop is still false.
        //   Loop repeats with same i=1? Yes.
        //   stateB = s2. s2 dominates s2? No (dominance returns false for equal).
        //   else: stop = true; state = s2.
        // Loop breaks.
        // for loop continues to i=2.
        // But stop is true? No, stop is local variable? No, it's defined outside.
        // `boolean stop = false;` outside if/else.
        // So if stop becomes true, the inner while loop won't run for next i?
        // Wait, the while loop is `while(stop == false)`.
        // If stop is true, while loop doesn't run.
        // So for loop continues, but nothing happens inside?
        // That seems like a bug or weird logic in `NotDominatedCandidate`.
        
        // Let's trace:
        // i=1. stop=false.
        //   stateB=s2. s2 dominates s1. stateA=s2.
        //   Loop repeats.
        //   stateB=s2. s2 dominates s2? No.
        //   else: stop=true. state=s2.
        // i=2. stop=true.
        //   while(stop == false) -> false. Loop doesn't run.
        // End of for loop.
        // Returns state (which is s2).
        
        // So it seems it only checks the first element that dominates the current one, and then stops?
        // Or rather, it climbs up the dominance chain using the *same* neighbor?
        // `stateB = listNeighborhood.get(i)` is inside the while loop.
        // So it keeps checking the same neighbor `i` against the updated `stateA`.
        // If `stateA` was updated to `stateB` (which is `list.get(i)`), then in the next iteration `stateB` is still `list.get(i)`.
        // `dominance(stateB, stateA)` will be `dominance(stateB, stateB)`.
        // `dominance` returns false if values are equal (strict inequality for at least one objective).
        // So it goes to else, sets stop=true, and state=stateA.
        
        // So effectively:
        // For each neighbor i:
        //   If neighbor i dominates current best (stateA), update stateA to neighbor i.
        //   Then stop checking this neighbor (obviously).
        //   AND stop checking ANY future neighbors because `stop` is set to true and never reset to false inside the for loop?
        
        // Yes, `stop` is declared outside the for loop.
        // Once `stop` is true, the while loop `while(stop == false)` will never execute for subsequent `i`.
        // So it stops at the first neighbor that dominates the previous one (or if the first neighbor doesn't dominate, it stops there too).
        
        // Wait, if i=1 (s2) dominates s1. stateA becomes s2. stop becomes true in next sub-iteration.
        // Then i=2. while(stop==false) is false. Nothing happens.
        // Result is s2.
        
        // What if i=1 (s2) does NOT dominate s1?
        // i=1. stateB=s2. s2 dominates s1? False.
        // else: stop=true. state=s1.
        // i=2. while(stop==false) is false.
        // Result is s1.
        
        // So it seems `NotDominatedCandidate` only looks at the first neighbor?
        // That seems wrong, but that's what the code says.
        // `for (int i = 1; i < listNeighborhood.size(); i++)`
        // `while(stop == false)`
        
        // Unless `stop` is reset? No.
        
        // Let's write the test expecting this behavior.
        
        State result = candidate.stateSearch(neighborhood);
        
        assertEquals(s2, result);
    }
}
