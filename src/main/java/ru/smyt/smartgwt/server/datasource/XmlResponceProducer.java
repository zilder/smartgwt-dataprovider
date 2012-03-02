package ru.smyt.smartgwt.server.datasource;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Logger;
//import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Ildar Musin (c) 2012
 */
public class XmlResponceProducer implements IResponseProducer {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final Logger log = Logger.getLogger(XmlResponceProducer.class);
    private HttpServletResponse response;
    private IDataSource dataSource;

    public XmlResponceProducer(HttpServletResponse response, IDataSource ds) throws IOException {
	this.response = response;
	this.dataSource = ds;
    }
    
    @Override
    //public void onFetchAll(List<Object> objects) {
    public void onFetchAll(IResultSet resultSet) {
	try {
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance ();
	    DocumentBuilder db = dbf.newDocumentBuilder ();
	    Document doc = db.newDocument ();
	    Element resp = doc.createElement("response");
	    doc.appendChild(resp);

	    doc.setXmlVersion("1.0");

	    Element status = doc.createElement("status");
	    Element startRow = doc.createElement("startRow");
	    Element endRow = doc.createElement("endRow");
	    Element totalRows = doc.createElement("totalRows");

	    status.appendChild(doc.createTextNode("0"));
	    startRow.appendChild(doc.createTextNode("0"));

	    Element data = doc.createElement("data");
	    
	    /*for(Object obj: objects)
		data.appendChild(addRecord(doc, obj));*/
	    
	    int size = 0;
	    while(resultSet.hasNext()) {
		Element record = doc.createElement("record");
		for(String field: dataSource.getFields()) {
		    
		    Object value = resultSet.get(field);
		    if(value != null) {
			
			// format date to smartgwt format
			// example: "2012-03-01T11:26:15"
			if(value instanceof Date) {
			    Date d = (Date)value;
			    record.setAttribute(field, dateFormat.format(d));
			}
			else
			    record.setAttribute(field, value.toString());
		    }

		}
		data.appendChild(record);
		size++;
		resultSet.next();
	    }
	    
	    endRow.appendChild(doc.createTextNode(Integer.toString(size-1)));
	    totalRows.appendChild(doc.createTextNode(Integer.toString(size)));
	    
	    resp.appendChild(status);
	    resp.appendChild(startRow);
	    resp.appendChild(endRow);
	    resp.appendChild(totalRows);
	    resp.appendChild(data);
	    
	    writeResult(doc);
	    
	} catch(Exception ex) {
	    log.error("Error while fetching", ex);
	}
    }
    
    private void writeResult(Document doc) throws IOException {
	response.setContentType("text/xml; charset=UTF-8");
        PrintWriter out = response.getWriter();

	try {
	    //String type = request.getParameter("type");
	    //Document doc = generateXml(type);
	    
	    TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    Transformer transformer = transformerFactory.newTransformer();
	    
	    DOMSource source = new DOMSource(doc);
	    StreamResult result = new StreamResult(out);
	    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    transformer.transform(source, result);
	}
	catch(Exception ex) {
	    //out.write("<response><status>-1</status><data>" + ex.getMessage() + "</data></response>");
	    out.write(getFormattedError(ex.getMessage()));
	}
	finally {
	    out.close();
	}
    }
    
    private String getFormattedError(String message) {
	return "<response><status>-1</status><data>" + message + "</data></response>";
    }

    /**
     * Генерирует запись <record ...> извлекая данные о значениях полей объекта
     * через Reflection
     * @param doc DOM-документ
     * @param obj объект
     * @return объект DOM, описывающий объект
     */
    /*private Element addRecord(Document doc, Object obj) {
	Element record = doc.createElement("record");
	try {
	    addObjectToRecord(record, obj, obj.getClass());
	} catch(Exception ex) {
	    //log.error("Exception while reading data from object: " + ex.getMessage());
	}
	return record;
    }
    
    private void addSuperclass(Element record, Object obj, Class clazz, String prefix)
	    throws IllegalArgumentException, IllegalAccessException
    {
	Class c = clazz.getSuperclass();
	if(c != null) {
	    addObjectToRecord(record, obj, c, prefix);
	}
    }
    
    private void addObjectToRecord(Element record, Object obj, Class clazz)
	    throws IllegalArgumentException, IllegalAccessException
    {
	addObjectToRecord(record, obj, clazz, "");
    }
    
    private void addObjectToRecord(Element record, Object obj, String prefix)
	    throws IllegalArgumentException, IllegalAccessException
    {
	addObjectToRecord(record, obj, obj.getClass(), prefix);
    }

    private void addObjectToRecord(Element record, Object obj, Class clazz, String prefix)
	    throws IllegalArgumentException, IllegalAccessException
    {
	//Class clazz = obj.getClass();
	//log.error("Serializing object: " + clazz.getName());
	addSuperclass(record, obj, clazz, prefix);
	Field[] fields = clazz.getDeclaredFields();
	for(Field field: fields) {
	    addField(record, field, obj, prefix);
	}
    }
    
    private void addField(Element record, Field field, Object obj, String prefix)
	    throws IllegalArgumentException, IllegalAccessException {
	field.setAccessible(true);
	Object fieldValue = field.get(obj);
	
	// simple types
	if(fieldValue instanceof Number || fieldValue instanceof String || fieldValue instanceof Enum || fieldValue==null)
	    record.setAttribute(prefix+field.getName(), 
			(fieldValue != null) ? fieldValue.toString() : "");
	// complicated types
	else if(fieldValue instanceof Date) {
	    Date d = (Date)fieldValue;
	    record.setAttribute(prefix+field.getName(), dateFormat.format(d));
	}
	else if(!(fieldValue instanceof Collection<?>)) {
	    addObjectToRecord(record, fieldValue, field.getName() + "__");
	}
	// collections are not to be handled
    }*/
}
