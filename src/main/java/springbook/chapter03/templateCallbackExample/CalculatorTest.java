package springbook.chapter03.templateCallbackExample;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

class CalculatorTest {

    Calculator calculator;
    String numFilePath;

    @BeforeEach
    void setUp() {
        this.calculator = new Calculator();
        this.numFilePath = "src/main/java/springbook/chapter03/templateCallbackExample/numbers.txt";
    }

    @Test
    void sumOfNumbers() throws IOException {
        assertThat(calculator.calcSum(numFilePath)).isEqualTo(10);
    }

    @Test
    void multiplyOfNumbers() throws IOException {
        assertThat(calculator.calcMultiply(this.numFilePath)).isEqualTo(24);
    }

    @Test
    void sumOfNumbers_2() throws IOException {
        assertThat(calculator.calcSum_2(numFilePath)).isEqualTo(10);
    }

    @Test
    void multiplyOfNumbers_2() throws IOException {
        assertThat(calculator.calcMultiply_2(this.numFilePath)).isEqualTo(24);
    }

    @Test
    void concatenate() throws IOException {
        assertThat(calculator.concatenate(this.numFilePath)).isEqualTo("1234");
    }
}
