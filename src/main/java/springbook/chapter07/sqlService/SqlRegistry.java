package springbook.chapter07.sqlService;

public interface SqlRegistry {
    /**
     * SQL을 키와 함께 등록한다.
     *
     * @param key
     * @param sql
     */
    void registerSql(String key, String sql);

    /**
     * 키에 해당하는 SQL을 조회한다.
     *
     * @param key
     * @return 찾은 SQL
     * @throws SqlNotFoundException
     */
    String findSql(String key) throws SqlNotFoundException;
}
