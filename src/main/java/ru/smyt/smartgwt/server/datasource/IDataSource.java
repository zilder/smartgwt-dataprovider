package ru.smyt.smartgwt.server.datasource;

import java.util.List;

/**
 * Data source interface
 * @author Ildar Musin (c) 2012
 */
public interface IDataSource {
    List<String> getFields();
    //List<Object> fetchAll();
    ResultSet fetchAll();
}
