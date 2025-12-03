package factory_method;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import evolutionary_algorithms.complement.FatherSelection;
import evolutionary_algorithms.complement.SelectionType;
import evolutionary_algorithms.complement.RouletteSelection;
import evolutionary_algorithms.complement.TruncationSelection;

class FactoryFatherSelectionTest {

    @Test
    void testCreateFatherSelection() throws Exception {
        FactoryFatherSelection factory = new FactoryFatherSelection();
        
        FatherSelection s1 = factory.createSelectFather(SelectionType.RouletteSelection);
        assertNotNull(s1);
        assertTrue(s1 instanceof RouletteSelection);
        
        FatherSelection s2 = factory.createSelectFather(SelectionType.TruncationSelection);
        assertNotNull(s2);
        assertTrue(s2 instanceof TruncationSelection);
    }
}
