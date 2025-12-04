package metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class SimpleTestClassTest {
    @Test
    public void testReturnOne() {
        SimpleTestClass stc = new SimpleTestClass();
        assertEquals(1, stc.returnOne());
    }
}
