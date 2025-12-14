package problem.definition;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import problem.definition.Problem.ProblemType;

class ObjetiveFunctionTest {

    @Test
    void testGetSetWeight() {
        ObjetiveFunction objFunc = mock(ObjetiveFunction.class, CALLS_REAL_METHODS);
        objFunc.setWeight(0.5f);
        assertEquals(0.5f, objFunc.getWeight());
    }

    @Test
    void testGetSetTypeProblem() {
        ObjetiveFunction objFunc = mock(ObjetiveFunction.class, CALLS_REAL_METHODS);
        objFunc.setTypeProblem(ProblemType.Maximizar);
        assertEquals(ProblemType.Maximizar, objFunc.getTypeProblem());
    }
}
