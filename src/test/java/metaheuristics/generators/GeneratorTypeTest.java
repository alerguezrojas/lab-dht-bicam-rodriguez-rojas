package metaheuristics.generators;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GeneratorTypeTest {

    @Test
    public void testEnumValues() {
        assertEquals(16, GeneratorType.values().length);
        assertEquals(GeneratorType.HillClimbing, GeneratorType.valueOf("HillClimbing"));
        assertEquals(GeneratorType.TabuSearch, GeneratorType.valueOf("TabuSearch"));
        assertEquals(GeneratorType.SimulatedAnnealing, GeneratorType.valueOf("SimulatedAnnealing"));
        assertEquals(GeneratorType.RandomSearch, GeneratorType.valueOf("RandomSearch"));
        assertEquals(GeneratorType.LimitThreshold, GeneratorType.valueOf("LimitThreshold"));
        assertEquals(GeneratorType.HillClimbingRestart, GeneratorType.valueOf("HillClimbingRestart"));
        assertEquals(GeneratorType.GeneticAlgorithm, GeneratorType.valueOf("GeneticAlgorithm"));
        assertEquals(GeneratorType.EvolutionStrategies, GeneratorType.valueOf("EvolutionStrategies"));
        assertEquals(GeneratorType.DistributionEstimationAlgorithm, GeneratorType.valueOf("DistributionEstimationAlgorithm"));
        assertEquals(GeneratorType.ParticleSwarmOptimization, GeneratorType.valueOf("ParticleSwarmOptimization"));
        assertEquals(GeneratorType.MultiGenerator, GeneratorType.valueOf("MultiGenerator"));
        assertEquals(GeneratorType.MultiobjectiveTabuSearch, GeneratorType.valueOf("MultiobjectiveTabuSearch"));
        assertEquals(GeneratorType.MultiobjectiveStochasticHillClimbing, GeneratorType.valueOf("MultiobjectiveStochasticHillClimbing"));
        assertEquals(GeneratorType.MultiCaseSimulatedAnnealing, GeneratorType.valueOf("MultiCaseSimulatedAnnealing"));
        assertEquals(GeneratorType.MultiobjectiveHillClimbingRestart, GeneratorType.valueOf("MultiobjectiveHillClimbingRestart"));
        assertEquals(GeneratorType.MultiobjectiveHillClimbingDistance, GeneratorType.valueOf("MultiobjectiveHillClimbingDistance"));
    }
}
