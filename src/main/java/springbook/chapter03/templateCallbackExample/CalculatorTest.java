package springbook.chapter03.templateCallbackExample;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class CalculatorTest {

    @Test
    public void sumOfNumbers() throws IOException {
        Calculator calculator = new Calculator();
        String filePath = "src/main/java/springbook/chapter03/templateCallbackExample/numbers.txt";
        int sum = calculator.calcSum(filePath);
        assertThat(sum).isEqualTo(10);
    }
}
