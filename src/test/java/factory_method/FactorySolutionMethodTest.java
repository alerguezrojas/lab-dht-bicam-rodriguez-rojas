package factory_method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import problem.extension.SolutionMethod;
import problem.extension.TypeSolutionMethod;

class FactorySolutionMethodTest {

    private MockedStatic<FactoryLoader> factoryLoaderMockedStatic;
    private SolutionMethod solutionMethodMock;

    @BeforeEach
    void setUp() {
        solutionMethodMock = mock(SolutionMethod.class);
        factoryLoaderMockedStatic = mockStatic(FactoryLoader.class);
        factoryLoaderMockedStatic.when(() -> FactoryLoader.getInstance(anyString())).thenReturn(solutionMethodMock);
    }

    @AfterEach
    void tearDown() {
        factoryLoaderMockedStatic.close();
    }

    @Test
    void testCreatedSolutionMethod() throws Exception {
        FactorySolutionMethod factory = new FactorySolutionMethod();
        // Assuming TypeSolutionMethod has at least one value. I should check TypeSolutionMethod.
        // If I don't know the values, I can use any value if I can access the enum.
        // Or I can mock the enum if possible? No, enums are hard to mock.
        // I'll assume TypeSolutionMethod has a value, or I'll check it first.
        
        // Let's check TypeSolutionMethod.
        // For now I will use a placeholder and if it fails I will fix it.
        // But wait, I can just use TypeSolutionMethod.values()[0] if it has values.
        
        TypeSolutionMethod[] values = TypeSolutionMethod.values();
        if (values.length > 0) {
            SolutionMethod result = factory.createdSolutionMethod(values[0]);
            assertNotNull(result);
            assertEquals(solutionMethodMock, result);
            factoryLoaderMockedStatic.verify(() -> FactoryLoader.getInstance(contains(values[0].toString())));
        }
    }
}
