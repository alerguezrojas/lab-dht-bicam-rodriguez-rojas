package evolutionary_algorithms.complement;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import problem.definition.State;

class RouletteSelectionTest {

    @Test
    void testSelection() {
        RouletteSelection selection = new RouletteSelection();
        List<State> population = new ArrayList<>();
        
        // Create 3 states with different evaluations
        State s1 = new State();
        ArrayList<Double> eval1 = new ArrayList<>();
        eval1.add(10.0);
        s1.setEvaluation(eval1);
        s1.setNumber(1);
        
        State s2 = new State();
        ArrayList<Double> eval2 = new ArrayList<>();
        eval2.add(20.0);
        s2.setEvaluation(eval2);
        s2.setNumber(2);
        
        State s3 = new State();
        ArrayList<Double> eval3 = new ArrayList<>();
        eval3.add(30.0);
        s3.setEvaluation(eval3);
        s3.setNumber(3);
        
        population.add(s1);
        population.add(s2);
        population.add(s3);
        
        // Truncation parameter is not used in the provided code for RouletteSelection
        List<State> selected = selection.selection(population, 0);
        
        assertNotNull(selected);
        assertEquals(population.size(), selected.size());
        
        for(State s : selected) {
            assertTrue(population.contains(s));
        }
    }
    
    @Test
    void testSelectionWithSingleElement() {
        RouletteSelection selection = new RouletteSelection();
        List<State> population = new ArrayList<>();
        
        State s1 = new State();
        ArrayList<Double> eval1 = new ArrayList<>();
        eval1.add(10.0);
        s1.setEvaluation(eval1);
        population.add(s1);
        
        List<State> selected = selection.selection(population, 0);
        
        assertEquals(1, selected.size());
        assertEquals(s1, selected.get(0));
    }
}