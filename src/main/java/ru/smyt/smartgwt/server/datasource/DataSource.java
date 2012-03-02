package ru.smyt.smartgwt.server.datasource;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Ildar Musin (c) 2012
 */
public abstract class DataSource implements IDataSource {
    private static final Logger log = Logger.getLogger(DataSource.class);
    private List<String> fields;

    @Override
    public List<String> getFields() {
	return fields;
    }
    
    protected void setFields(String... fields) {
	this.fields = Arrays.asList(fields);
    }
    
    /**
     * Researches the class and adds all found fields
     * @param clazz 
     */
    protected void setFields(Class clazz) {
	List<String> flds = new ArrayList<String>();
	collectFields(flds, clazz, "", 2);
	fields = flds;
    }
    
    private void collectFields(List<String> flds, Class clazz, String prefix, int level) {
	Method[] classFields = clazz.getMethods(); //getFields();
	
	// iterate through class fields
	for(Method field: classFields) {
	    String methodName = field.getName();
	    //String[] s = methodName.split();
	    
	    // if methods name doesn't start with "get" then skip it
	    if(methodName.length() <= 3 || !methodName.substring(0, 3).equals("get"))
		continue;
	    
	    // transform get method name to field name. For example:
	    // "getLastName" => "lastName" etc.
	    String fieldName = methodName.substring(3,4).toLowerCase() + methodName.substring(4);
	    
	    // if field is one of built-in types
	    Class fieldType = field.getReturnType();
	    if(fieldType.isPrimitive() || fieldType.isEnum() || Number.class.isAssignableFrom(fieldType)
		 || fieldType.equals(String.class) || fieldType.equals(Date.class))
	    {
		flds.add(prefix + fieldName);
		//log.info("Added field: " + prefix + fieldName);	
	    }
	    
	    // else if not some type of collections (which are not handled)
	    else if(!fieldType.equals(Collection.class) && !fieldType.equals(Class.class) && level > 0) {
		collectFields(flds, fieldType, prefix + fieldName + '.', level-1);
	    }
	}
    }

    @Override
    public abstract ResultSet fetchAll();

}
