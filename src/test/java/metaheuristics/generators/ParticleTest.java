package metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import metaheuristics.strategy.Strategy;
import problem.definition.Problem;
import problem.definition.State;

class ParticleTest {

    @Test
    void testUpdateVelocityAndCode() throws Exception {
        try (MockedStatic<Strategy> mockedStrategy = mockStatic(Strategy.class)) {
            Strategy strategy = mock(Strategy.class);
            Problem problem = mock(Problem.class);
            State problemState = mock(State.class);
            
            mockedStrategy.when(Strategy::getStrategy).thenReturn(strategy);
            when(strategy.getProblem()).thenReturn(problem);
            when(problem.getState()).thenReturn(problemState);
            
            // Setup problem state code size
            ArrayList<Object> code = new ArrayList<>();
            code.add(1.0);
            code.add(2.0);
            when(problemState.getCode()).thenReturn(code);
            
            when(strategy.getCountMax()).thenReturn(100);
            
            // Setup ParticleSwarmOptimization static fields
            ParticleSwarmOptimization.wmax = 0.9;
            ParticleSwarmOptimization.wmin = 0.4;
            ParticleSwarmOptimization.countCurrentIterPSO = 10;
            ParticleSwarmOptimization.learning1 = 2;
            ParticleSwarmOptimization.learning2 = 2;
            ParticleSwarmOptimization.countParticle = 0;
            ParticleSwarmOptimization.countParticleBySwarm = 1;
            ParticleSwarmOptimization.binary = false;
            
            // Setup lBest
            State lBestState = new State();
            lBestState.setCode(new ArrayList<>(Arrays.asList(5.0, 5.0)));
            ParticleSwarmOptimization.lBest = new State[] { lBestState };
            
            Particle particle = new Particle();
            
            // Setup particle state
            State pBest = new State();
            pBest.setCode(new ArrayList<>(Arrays.asList(3.0, 3.0)));
            particle.setStatePBest(pBest);
            
            State actual = new State();
            actual.setCode(new ArrayList<>(Arrays.asList(1.0, 1.0)));
            particle.setStateActual(actual);
            
            // Initial velocity empty
            particle.setVelocity(new ArrayList<>());
            
            // Run generate (which calls UpdateVelocity and UpdateCode)
            particle.generate(0);
            
            // Verify velocity was updated
            ArrayList<Object> newVelocity = particle.getVelocity();
            assertEquals(2, newVelocity.size());
            assertNotNull(newVelocity.get(0));
            
            // Verify state code was updated
            State newActual = particle.getStateActual();
            assertEquals(2, newActual.getCode().size());
        }
    }
}
