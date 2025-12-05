package problem.definition;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import problem.extension.SolutionMethod;
import problem.extension.TypeSolutionMethod;
import factory_interface.IFFactorySolutionMethod;
import factory_method.FactorySolutionMethod;

class ProblemTest {

    private Problem problem;

    @Mock
    private ObjetiveFunction objetiveFunction;

    @Mock
    private State state;

    @Mock
    private IFFactorySolutionMethod factorySolutionMethod;
    
    @Mock
    private SolutionMethod solutionMethod;

    @Mock
    private Codification codification;

    @Mock
    private Operator operator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        problem = new Problem();
    }

    @Test
    void testGettersAndSetters() {
        ArrayList<ObjetiveFunction> functions = new ArrayList<>();
        functions.add(objetiveFunction);
        problem.setFunction(functions);
        assertEquals(functions, problem.getFunction());

        problem.setState(state);
        assertEquals(state, problem.getState());

        problem.setTypeProblem(Problem.ProblemType.Maximizar);
        assertEquals(Problem.ProblemType.Maximizar, problem.getTypeProblem());

        problem.setCodification(codification);
        assertEquals(codification, problem.getCodification());

        problem.setOperator(operator);
        assertEquals(operator, problem.getOperator());

        problem.setPossibleValue(10);
        assertEquals(10, problem.getPossibleValue());

        problem.setTypeSolutionMethod(TypeSolutionMethod.FactoresPonderados);
        assertEquals(TypeSolutionMethod.FactoresPonderados, problem.getTypeSolutionMethod());

        problem.setFactorySolutionMethod(factorySolutionMethod);
        assertEquals(factorySolutionMethod, problem.getFactorySolutionMethod());
    }

    @Test
    void testEvaluateWithNullTypeSolutionMethod() throws Exception {
        ArrayList<ObjetiveFunction> functions = new ArrayList<>();
        functions.add(objetiveFunction);
        problem.setFunction(functions);
        problem.setTypeSolutionMethod(null);
        
        when(objetiveFunction.Evaluation(state)).thenReturn(10.0);

        problem.Evaluate(state);

        verify(objetiveFunction).Evaluation(state);
        verify(state).setEvaluation(any(ArrayList.class));
    }

    @Test
    void testEvaluateWithTypeSolutionMethod() throws Exception {
        ArrayList<ObjetiveFunction> functions = new ArrayList<>();
        functions.add(objetiveFunction);
        problem.setFunction(functions);
        problem.setTypeSolutionMethod(TypeSolutionMethod.FactoresPonderados);
        problem.setFactorySolutionMethod(factorySolutionMethod);
        
        when(factorySolutionMethod.createdSolutionMethod(TypeSolutionMethod.FactoresPonderados)).thenReturn(solutionMethod);

        problem.Evaluate(state);

        verify(factorySolutionMethod).createdSolutionMethod(TypeSolutionMethod.FactoresPonderados);
        verify(solutionMethod).evaluationState(state);
    }
}
