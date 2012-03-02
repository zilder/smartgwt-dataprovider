package ru.smyt.smartgwt.server.datasource;

import java.io.IOException;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Ildar Musin (c) 2012
 */
public class RestDataProvider extends HttpServlet {
    private HashMap<String, IDataSource> dataSources;
    
    public RestDataProvider() {
	dataSources = new HashMap<String, IDataSource>();
    }
    
    public void register(String name, IDataSource dataSource) {
	dataSources.put(name, dataSource);
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	//super.doGet(req, resp);
	
	String dataSourceName = req.getParameter("dataSource");
	
	IDataSource ds = dataSources.get(dataSourceName);
	if(ds != null) {
	    //List<Object> objects = ds.fetchAll();
	    ResultSet rs = ds.fetchAll();
	    XmlResponceProducer producer = new XmlResponceProducer(resp, ds);
	    producer.onFetchAll(rs);
	}
	else
	    resp.setStatus(404);
    }
}
