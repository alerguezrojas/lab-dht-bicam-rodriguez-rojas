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

    @Test
    void testConstructorWithCode() {
        ArrayList<Object> code = new ArrayList<>();
        code.add("A");
        code.add("B");
        
        State state = new State(code);
        
        assertNotNull(state.getCode());
        assertEquals(code, state.getCode());
        // Verify it's the same reference as passed in constructor (based on implementation)
        assertSame(code, state.getCode());
    }

    @Test
    void testClone() {
        State original = new State();
        ArrayList<Object> code = new ArrayList<>();
        code.add(1);
        original.setCode(code);
        
        ArrayList<Double> evaluation = new ArrayList<>();
        evaluation.add(10.5);
        original.setEvaluation(evaluation);
        
        original.setNumber(1);
        original.setTypeGenerator(GeneratorType.HillClimbing);
        
        State cloned = original.clone();
        
        assertNotSame(original, cloned);
        assertEquals(original.getNumber(), cloned.getNumber());
        assertEquals(original.getTypeGenerator(), cloned.getTypeGenerator());
        
        assertEquals(original.getCode(), cloned.getCode());
        assertNotSame(original.getCode(), cloned.getCode()); // Verify new ArrayList created
        
        assertEquals(original.getEvaluation(), cloned.getEvaluation());
        assertNotSame(original.getEvaluation(), cloned.getEvaluation()); // Verify new ArrayList created
    }
    
    @Test
    void testCloneWithNulls() {
        State original = new State();
        original.setCode(null);
        original.setEvaluation(null);
        
        State cloned = original.clone();
        
        assertNull(cloned.getCode());
        assertNull(cloned.getEvaluation());
    }

    @Test
    void testComparator() {
        State s1 = new State();
        ArrayList<Object> code1 = new ArrayList<>();
        code1.add(1);
        s1.setCode(code1);
        
        State s2 = new State();
        ArrayList<Object> code2 = new ArrayList<>();
        code2.add(1);
        s2.setCode(code2);
        
        State s3 = new State();
        ArrayList<Object> code3 = new ArrayList<>();
        code3.add(2);
        s3.setCode(code3);
        
        assertTrue(s1.comparator(s2));
        assertFalse(s1.comparator(s3));
    }

    @Test
    void testDistance() {
        State s1 = new State();
        ArrayList<Object> code1 = new ArrayList<>();
        code1.add(1);
        code1.add(2);
        code1.add(3);
        s1.setCode(code1);
        
        State s2 = new State();
        ArrayList<Object> code2 = new ArrayList<>();
        code2.add(1); // Match
        code2.add(5); // Diff
        code2.add(3); // Match
        s2.setCode(code2);
        
        // Distance should be 1 (one difference at index 1)
        assertEquals(1.0, s1.distance(s2));
        
        State s3 = new State();
        ArrayList<Object> code3 = new ArrayList<>();
        code3.add(4); // Diff
        code3.add(5); // Diff
        code3.add(6); // Diff
        s3.setCode(code3);
        
        // Distance should be 3
        assertEquals(3.0, s1.distance(s3));
    }
    
    @Test
    void testGetCopy() {
        State original = new State();
        ArrayList<Object> code = new ArrayList<>();
        code.add(10);
        original.setCode(code);
        
        Object copyObj = original.getCopy();
        assertTrue(copyObj instanceof State);
        State copy = (State) copyObj;
        
        assertEquals(original.getCode(), copy.getCode());
        assertNotSame(original, copy);
    }
}