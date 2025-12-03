package evolutionary_algorithms.complement;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import problem.definition.State;

class UniformCrossoverTest {

    @Test
    void testMascaraGeneration() {
        UniformCrossover crossover = new UniformCrossover();
        int length = 10;
        int[] mascara = crossover.mascara(length);
        
        assertEquals(length, mascara.length);
        for (int val : mascara) {
            assertTrue(val == 0 || val == 1, "Mascara values should be 0 or 1");
        }
    }

    @Test
    void testCrossover() {
        UniformCrossover crossover = new UniformCrossover();
        
        State father1 = new State();
        ArrayList<Object> code1 = new ArrayList<>();
        for(int i=0; i<5; i++) code1.add(0);
        father1.setCode(code1);
        
        State father2 = new State();
        ArrayList<Object> code2 = new ArrayList<>();
        for(int i=0; i<5; i++) code2.add(1);
        father2.setCode(code2);
        
        // PC doesn't matter for UniformCrossover implementation provided (it always crosses)
        State child = crossover.crossover(father1, father2, 0.5);
        
        assertNotNull(child);
        assertEquals(5, child.getCode().size());
        
        for(Object gene : child.getCode()) {
            assertTrue(gene.equals(0) || gene.equals(1), "Gene should come from father1 (0) or father2 (1)");
        }
    }
}