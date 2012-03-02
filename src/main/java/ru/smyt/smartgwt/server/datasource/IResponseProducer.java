package ru.smyt.smartgwt.server.datasource;

/**
 *
 * @author Hp
 */
public interface IResponseProducer {
    //void onFetchAll(List<Object> objects);
    void onFetchAll(IResultSet resultSet);
}
