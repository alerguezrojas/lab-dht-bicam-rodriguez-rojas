package problem.extension;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import problem.definition.State;

class MetricasMultiobjetivoTest {

    @Test
    void testTasaError() throws Exception {
        MetricasMultiobjetivo metricas = new MetricasMultiobjetivo();
        
        State s1 = new State();
        s1.setEvaluation(new ArrayList<>(Arrays.asList(1.0, 2.0)));
        
        State s2 = new State();
        s2.setEvaluation(new ArrayList<>(Arrays.asList(3.0, 4.0)));
        
        List<State> current = new ArrayList<>();
        current.add(s1);
        current.add(s2);
        
        List<State> truePareto = new ArrayList<>();
        truePareto.add(s1); // s1 is in true pareto
        
        // s2 is not in true pareto
        // Error rate should be 1/2 = 0.5
        
        double error = metricas.TasaError(current, truePareto);
        assertEquals(0.5, error, 0.001);
    }
    
    @Test
    void testDispersion() throws Exception {
        MetricasMultiobjetivo metricas = new MetricasMultiobjetivo();
        
        State s1 = new State();
        s1.setEvaluation(new ArrayList<>(Arrays.asList(1.0)));
        
        State s2 = new State();
        s2.setEvaluation(new ArrayList<>(Arrays.asList(2.0)));
        
        ArrayList<State> solutions = new ArrayList<>();
        solutions.add(s1);
        solutions.add(s2);
        
        double dispersion = metricas.Dispersion(solutions);
        // Just check it doesn't crash and returns something reasonable
        assertTrue(dispersion >= 0);
    }
}
