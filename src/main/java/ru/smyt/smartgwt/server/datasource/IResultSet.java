package ru.smyt.smartgwt.server.datasource;

/**
 * 
 * @author Ildar Musin (c) 2012
 */
public interface IResultSet {
    Object get(String field);
    boolean next();
    boolean hasNext();
}
