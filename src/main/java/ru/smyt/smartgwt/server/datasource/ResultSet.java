package ru.smyt.smartgwt.server.datasource;

import java.lang.reflect.Method;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Ildar Musin (c) 2012
 */
public class ResultSet implements IResultSet {
    private static final Logger log = Logger.getLogger(ResultSet.class);
    
    private List<Object> objects;
    //private Iterator<Object> current;
    private int cursor = 0;
    private Class PlainClass;
    private IDataSource ds;
    private List<String> fields;
    
    public ResultSet(List<Object> objects, IDataSource ds) {
	//this.ds = ds;
	this.fields = ds.getFields();
	this.objects = objects;
	//this.current = objects.iterator();
    }

    /*private void constructClass(String[] fields) throws CannotCompileException {
	//PlainClass = new Class();
	ClassPool pool = ClassPool.getDefault();
	CtClass proxy = pool.makeClass("DataObjectProxy");
	for(String field: fields)
	    proxy.addField(CtField.make("public " + field + ";", proxy));
    }*/
    
    /**
     * Gets field value from current object.
     * @param field 
     * @return 
     */
    @Override
    public Object get(String field) {
	Class clazz = objects.get(cursor).getClass();
	
	String[] sequence = field.split("\\.");
	Object obj = objects.get(cursor);
	try {
	    for(String fieldName: sequence) {
		// transform field name to get method name
		String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		log.debug("Calling method " + methodName + "()");
		Method method = obj.getClass().getMethod(methodName);
		obj = method.invoke(obj);
	    }
	} catch (Exception ex) {
	    log.error(ex.getMessage());
	}
	
	return obj;
    }

    @Override
    public boolean next() {
	cursor++;
	return cursor <= objects.size();
    }

    @Override
    public boolean hasNext() {
	return cursor+1 <= objects.size();
    }

    /*public IDataSource getDataSource() {
	return ds;
    }*/
    
    public List<String> getFields() {
	return fields;
    }

}
