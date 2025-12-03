package problem.definition;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import metaheuristics.generators.GeneratorType;

class StateTest {

    @Test
    void testConstructorAndGetters() {
        State state = new State();
        assertNotNull(state.getCode());
        assertTrue(state.getCode().isEmpty());
    }

    @Test
    void testSetters() {
        State state = new State();
        ArrayList<Object> code = new ArrayList<>();
        code.add(1);
        code.add(2);
        
        state.setCode(code);
        assertEquals(code, state.getCode());
        
        state.setNumber(10);
        assertEquals(10, state.getNumber());
        
        state.setTypeGenerator(GeneratorType.GeneticAlgorithm);
        assertEquals(GeneratorType.GeneticAlgorithm, state.getTypeGenerator());
    }

    @Test
    void testCopyConstructor() {
        State original = new State();
        ArrayList<Object> code = new ArrayList<>();
        code.add(100);
        original.setCode(code);
        original.setNumber(5);
        original.setTypeGenerator(GeneratorType.RandomSearch);
        
        State copy = new State(original);
        
        assertEquals(original.getNumber(), copy.getNumber());
        assertEquals(original.getTypeGenerator(), copy.getTypeGenerator());
        assertEquals(original.getCode(), copy.getCode());
        assertNotSame(original.getCode(), copy.getCode()); // Deep copy of code list
    }
}