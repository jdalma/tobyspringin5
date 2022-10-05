package springbook.chapter03.templateCallbackExample;

import java.io.BufferedReader;
import java.io.IOException;

public interface BufferedReaderCallback {

    Integer doSomethingWithReader(BufferedReader br) throws IOException;
}
