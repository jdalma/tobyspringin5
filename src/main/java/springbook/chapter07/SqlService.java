package springbook.chapter07;

public interface SqlService {
    String getSql(String key) throws SqlRetrievalFailureException;
}
