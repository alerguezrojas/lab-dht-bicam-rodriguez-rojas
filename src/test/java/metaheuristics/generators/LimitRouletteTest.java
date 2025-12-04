package metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

public class LimitRouletteTest {

    @Test
    public void testGetSetLimitLow() {
        LimitRoulette limitRoulette = new LimitRoulette();
        float limitLow = 0.1f;
        limitRoulette.setLimitLow(limitLow);
        assertEquals(limitLow, limitRoulette.getLimitLow());
    }

    @Test
    public void testGetSetLimitHigh() {
        LimitRoulette limitRoulette = new LimitRoulette();
        float limitHigh = 0.9f;
        limitRoulette.setLimitHigh(limitHigh);
        assertEquals(limitHigh, limitRoulette.getLimitHigh());
    }

    @Test
    public void testGetSetGenerator() {
        LimitRoulette limitRoulette = new LimitRoulette();
        Generator generator = mock(Generator.class);
        limitRoulette.setGenerator(generator);
        assertEquals(generator, limitRoulette.getGenerator());
    }
    
    @Test
    public void testInitialState() {
        LimitRoulette limitRoulette = new LimitRoulette();
        assertEquals(0.0f, limitRoulette.getLimitLow());
        assertEquals(0.0f, limitRoulette.getLimitHigh());
        assertNull(limitRoulette.getGenerator());
    }
}
