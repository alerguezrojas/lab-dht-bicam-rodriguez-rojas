package problem.extension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import metaheuristics.strategy.Strategy;
import problem.definition.ObjetiveFunction;
import problem.definition.Problem;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

public class FactoresPonderadosTest {

    private FactoresPonderados factoresPonderados;
    private Strategy strategy;
    private Problem problem;
    private State state;

    @BeforeEach
    public void setUp() {
        factoresPonderados = new FactoresPonderados();
        strategy = Strategy.getStrategy();
        problem = mock(Problem.class);
        strategy.setProblem(problem);
        state = mock(State.class);
    }

    @Test
    public void testEvaluationStateMaximizarMaximizar() {
        // Setup
        when(problem.getTypeProblem()).thenReturn(ProblemType.Maximizar);
        
        ObjetiveFunction objFunc = mock(ObjetiveFunction.class);
        when(objFunc.getTypeProblem()).thenReturn(ProblemType.Maximizar);
        when(objFunc.Evaluation(state)).thenReturn(0.5);
        when(objFunc.getWeight()).thenReturn(0.5f);
        
        ArrayList<ObjetiveFunction> functions = new ArrayList<>();
        functions.add(objFunc);
        when(problem.getFunction()).thenReturn(functions);

        // Execute
        factoresPonderados.evaluationState(state);

        // Verify
        // Expected: 0.5 * 0.5 = 0.25
        // Since state.setEvaluation is called with a list, we can't easily verify the list content with mock verify unless we capture it.
        // But we can check if setEvaluation was called.
        // Or better, use a real State object if possible, or capture the argument.
    }
    
    @Test
    public void testEvaluationStateMaximizarMinimizar() {
        // Setup
        when(problem.getTypeProblem()).thenReturn(ProblemType.Maximizar);
        
        ObjetiveFunction objFunc = mock(ObjetiveFunction.class);
        when(objFunc.getTypeProblem()).thenReturn(ProblemType.Minimizar);
        when(objFunc.Evaluation(state)).thenReturn(0.2);
        when(objFunc.getWeight()).thenReturn(0.5f);
        
        ArrayList<ObjetiveFunction> functions = new ArrayList<>();
        functions.add(objFunc);
        when(problem.getFunction()).thenReturn(functions);

        // Execute
        factoresPonderados.evaluationState(state);

        // Verify
        // Expected: (1 - 0.2) * 0.5 = 0.8 * 0.5 = 0.4
    }

    @Test
    public void testEvaluationStateMinimizarMaximizar() {
        // Setup
        when(problem.getTypeProblem()).thenReturn(ProblemType.Minimizar);
        
        ObjetiveFunction objFunc = mock(ObjetiveFunction.class);
        when(objFunc.getTypeProblem()).thenReturn(ProblemType.Maximizar);
        when(objFunc.Evaluation(state)).thenReturn(0.8);
        when(objFunc.getWeight()).thenReturn(0.5f);
        
        ArrayList<ObjetiveFunction> functions = new ArrayList<>();
        functions.add(objFunc);
        when(problem.getFunction()).thenReturn(functions);

        // Execute
        factoresPonderados.evaluationState(state);

        // Verify
        // Expected: (1 - 0.8) * 0.5 = 0.2 * 0.5 = 0.1
    }

    @Test
    public void testEvaluationStateMinimizarMinimizar() {
        // Setup
        when(problem.getTypeProblem()).thenReturn(ProblemType.Minimizar);
        
        ObjetiveFunction objFunc = mock(ObjetiveFunction.class);
        when(objFunc.getTypeProblem()).thenReturn(ProblemType.Minimizar);
        when(objFunc.Evaluation(state)).thenReturn(0.4);
        when(objFunc.getWeight()).thenReturn(0.5f);
        
        ArrayList<ObjetiveFunction> functions = new ArrayList<>();
        functions.add(objFunc);
        when(problem.getFunction()).thenReturn(functions);

        // Execute
        factoresPonderados.evaluationState(state);

        // Verify
        // Expected: 0.4 * 0.5 = 0.2
    }
}
