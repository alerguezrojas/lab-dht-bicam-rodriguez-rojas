package local_search.complement;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StrategyTypeTest {

    @Test
    void testEnumValues() {
        StrategyType[] values = StrategyType.values();
        assertEquals(2, values.length);
        assertEquals(StrategyType.TABU, StrategyType.valueOf("TABU"));
        assertEquals(StrategyType.NORMAL, StrategyType.valueOf("NORMAL"));
    }
}
