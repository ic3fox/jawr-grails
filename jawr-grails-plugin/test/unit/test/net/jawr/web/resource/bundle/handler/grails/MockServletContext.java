/**
 * Copyright 2009-2013 Ibrahim Chaehoi
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package test.net.jawr.web.resource.bundle.handler.grails;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.servlet.*;
import javax.servlet.descriptor.JspConfigDescriptor;

import org.apache.commons.collections.iterators.IteratorEnumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class defines a mock servlet container.
 * 
 * @author Ibrahim Chaehoi
 */
public class MockServletContext implements ServletContext {

	/** The logger */
	private static Logger logger = LoggerFactory
			.getLogger(MockServletContext.class);

	/** The base directory */
	private String baseDir;

	/** The map attributes */
	private Map<String, String> initParameters = new HashMap<String, String>();

	/** The map attributes */
	private Map<String, Object> attributes = new HashMap<String, Object>();

	/**
	 * Constructor
	 */
	public MockServletContext() {

	}

	/**
	 * Constructor
	 */
	public MockServletContext(String baseDir, String tempDir) {
		this.baseDir = baseDir.replace('/', File.separatorChar);
		this.baseDir = this.baseDir.replaceAll("%20", " ");

		tempDir = tempDir.replace('/', File.separatorChar);
		tempDir = tempDir.replaceAll("%20", " ");
		setAttribute("javax.servlet.context.tempdir", new File(tempDir));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContext#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContext#getAttributeNames()
	 */
	@SuppressWarnings("unchecked")
	public Enumeration<String> getAttributeNames() {
		return new IteratorEnumeration(attributes.keySet().iterator());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContext#getContext(java.lang.String)
	 */
	public ServletContext getContext(String uripath) {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContext#getInitParameter(java.lang.String)
	 */
	public String getInitParameter(String name) {
		return (String) initParameters.get(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContext#getInitParameterNames()
	 */
	@SuppressWarnings("unchecked")
	public Enumeration<String> getInitParameterNames() {
		return new IteratorEnumeration(attributes.keySet().iterator());
	}

    @Override
    public boolean setInitParameter(String name, String value) {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.ServletContext#getMajorVersion()
     */
	public int getMajorVersion() {
		return 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContext#getMimeType(java.lang.String)
	 */
	public String getMimeType(String file) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContext#getMinorVersion()
	 */
	public int getMinorVersion() {
		return 5;
	}

    @Override
    public int getEffectiveMajorVersion() {
        return 0;
    }

    @Override
    public int getEffectiveMinorVersion() {
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.ServletContext#getNamedDispatcher(java.lang.String)
     */
	public RequestDispatcher getNamedDispatcher(String name) {
		throw new RuntimeException("operation not supported");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContext#getRealPath(java.lang.String)
	 */
	public String getRealPath(String path) {

		return baseDir + path;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContext#getRequestDispatcher(java.lang.String)
	 */
	public RequestDispatcher getRequestDispatcher(String path) {
		throw new RuntimeException("operation not supported");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContext#getResource(java.lang.String)
	 */
	public URL getResource(String path) throws MalformedURLException {

		URL url = null;
		File file = new File(baseDir + path);
		if (file.exists()) {
			url = file.toURI().toURL();
		}
		return url;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContext#getResourceAsStream(java.lang.String)
	 */
	public InputStream getResourceAsStream(String path) {

		path = path.replace('/', File.separatorChar);
		InputStream is = null;
		try {
			is = new FileInputStream(new File(baseDir, path));
		} catch (FileNotFoundException e) {
			logger.info("File for path : '" + path + "' not found");
		}

		return is;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContext#getResourcePaths(java.lang.String)
	 */
	public Set<String> getResourcePaths(String path) {

		path = path.replace('/', File.separatorChar);
		File resource = new File(baseDir, path);
		if (!resource.exists()) {
			// throw new InvalidPathException(baseDir + File.separator + path);
			return null;
		}

		// If the path is not valid throw an exception
		String[] resArray = resource.list();
		if (null == resArray)
			return null;

		// Make the returned dirs end with '/', to match a servletcontext
		// behavior.
		for (int i = 0; i < resArray.length; i++) {

			resArray[i] = path + resArray[i];
			if (isDirectory(resArray[i]))
				resArray[i] += '/';
		}
		Set<String> ret = new HashSet<String>();
		ret.addAll(Arrays.asList(resArray));

		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.jawr.web.resource.bundle.ResourceHandler#isDirectory(java.lang.String
	 * )
	 */
	public boolean isDirectory(String path) {
		path = path.replace('/', File.separatorChar);
		return new File(baseDir, path).isDirectory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContext#getServerInfo()
	 */
	public String getServerInfo() {
		throw new RuntimeException("operation not supported");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContext#getServlet(java.lang.String)
	 */
	public Servlet getServlet(String name) throws ServletException {
		throw new RuntimeException("operation not supported");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContext#getServletContextName()
	 */
	public String getServletContextName() {
		throw new RuntimeException("operation not supported");
	}

    public ServletRegistration.Dynamic addServlet(String servletName, String className) {
        return null;
    }

    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        return null;
    }

    public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        return null;
    }

    public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
        return null;
    }

    public ServletRegistration getServletRegistration(String servletName) {
        return null;
    }

    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return null;
    }

    public FilterRegistration.Dynamic addFilter(String filterName, String className) {
        return null;
    }

    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        return null;
    }

    public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        return null;
    }

    public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
        return null;
    }

    public FilterRegistration getFilterRegistration(String filterName) {
        return null;
    }

    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        return null;
    }

    public SessionCookieConfig getSessionCookieConfig() {
        return null;
    }

    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {

    }

    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return null;
    }

    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return null;
    }

    public void addListener(String className) {

    }

    public <T extends EventListener> void addListener(T t) {

    }

    public void addListener(Class<? extends EventListener> listenerClass) {

    }

    public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
        return null;
    }

    public JspConfigDescriptor getJspConfigDescriptor() {
        return null;
    }

    public ClassLoader getClassLoader() {
        return null;
    }

    public void declareRoles(String... roleNames) {

    }

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.ServletContext#getServletNames()
     */
	public Enumeration<String> getServletNames() {
		throw new RuntimeException("operation not supported");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContext#getServlets()
	 */
	public Enumeration<Servlet> getServlets() {
		throw new RuntimeException("operation not supported");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContext#log(java.lang.String)
	 */
	public void log(String msg) {
		logger.info(msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContext#log(java.lang.Exception,
	 * java.lang.String)
	 */
	public void log(Exception exception, String msg) {
		logger.info(msg, exception);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContext#log(java.lang.String,
	 * java.lang.Throwable)
	 */
	public void log(String message, Throwable throwable) {
		logger.info(message, throwable);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContext#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String name) {
		attributes.remove(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContext#setAttribute(java.lang.String,
	 * java.lang.Object)
	 */
	public void setAttribute(String name, Object object) {
		attributes.put(name, object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContext#getContextPath()
	 */
	@Override
	public String getContextPath() {
		return null;
	}

}
