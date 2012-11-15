/*
This file is part of oauth-login-module.

oauth-login-module is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

oauth-login-module is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with oauth-login-module.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.gadp.alfresco;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: guy
 * Date: 10/2/12
 * Time: 10:11 PM
 */
public class MockHttpSession implements HttpSession {

    private Map<String, Object> attributes = new HashMap<String, Object>();

    /**
     * Returns the time when this session was created, measured
     * in milliseconds since midnight January 1, 1970 GMT.
     *
     * @return a <code>long</code> specifying when this session was created,
     *         expressed in milliseconds since 1/1/1970 GMT
     * @throws IllegalStateException if this method is called on an
     *                               invalidated session
     */
    @Override
    public long getCreationTime() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Returns a string containing the unique identifier assigned to this
     * session. The identifier is assigned by the servlet container and is
     * implementation dependent.
     *
     * @return a string specifying the identifier assigned to this session
     * @throws IllegalStateException if this method is called on an
     *                               invalidated session
     */
    @Override
    public String getId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Returns the last time the client sent a request associated with
     * this session, as the number of milliseconds since midnight
     * January 1, 1970 GMT, and marked by the time the container received the request.
     * <p/>
     * <p>Actions that your application takes, such as getting or setting
     * a value associated with the session, do not affect the access
     * time.
     *
     * @return a <code>long</code> representing the last time the client sent
     *         a request associated with this session, expressed in milliseconds since
     *         1/1/1970 GMT
     * @throws IllegalStateException if this method is called on an
     *                               invalidated session
     */
    @Override
    public long getLastAccessedTime() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Returns the ServletContext to which this session belongs.
     *
     * @return The ServletContext object for the web application
     * @since Servlet 2.3
     */
    @Override
    public ServletContext getServletContext() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Specifies the time, in seconds, between client requests before the
     * servlet container will invalidate this session.  A negative time
     * indicates the session should never timeout.
     *
     * @param interval An integer specifying the number of seconds
     */
    @Override
    public void setMaxInactiveInterval(int interval) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Returns the maximum time interval, in seconds, that
     * the servlet container will keep this session open between
     * client accesses. After this interval, the servlet container
     * will invalidate the session.  The maximum time interval can be set
     * with the <code>setMaxInactiveInterval</code> method.
     * A negative time indicates the session should never timeout.
     *
     * @return an integer specifying the number of seconds this session
     *         remains open between client requests
     * @see #setMaxInactiveInterval
     */
    @Override
    public int getMaxInactiveInterval() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * @deprecated As of Version 2.1, this method is deprecated and has no
     *             replacement. It will be removed in a future version of the Java Servlet
     *             API.
     */
    @Override
    public HttpSessionContext getSessionContext() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Returns the object bound with the specified name in this session, or
     * <code>null</code> if no object is bound under the name.
     *
     * @param name a string specifying the name of the object
     * @return the object with the specified name
     * @throws IllegalStateException if this method is called on an
     *                               invalidated session
     */
    @Override
    public Object getAttribute(String name) {
        return this.attributes.get(name);  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * @param name a string specifying the name of the object
     * @return the object with the specified name
     * @throws IllegalStateException if this method is called on an
     *                               invalidated session
     * @deprecated As of Version 2.2, this method is replaced by
     *             {@link #getAttribute}.
     */
    @Override
    public Object getValue(String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Returns an <code>Enumeration</code> of <code>String</code> objects
     * containing the names of all the objects bound to this session.
     *
     * @return an <code>Enumeration</code> of <code>String</code> objects
     *         specifying the names of all the objects bound to this session
     * @throws IllegalStateException if this method is called on an
     *                               invalidated session
     */
    @Override
    public Enumeration getAttributeNames() {
        return Collections.enumeration(this.attributes.keySet());  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * @return an array of <code>String</code> objects specifying the names of
     *         all the objects bound to this session
     * @throws IllegalStateException if this method is called on an
     *                               invalidated session
     * @deprecated As of Version 2.2, this method is replaced by
     *             {@link #getAttributeNames}
     */
    @Override
    public String[] getValueNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Binds an object to this session, using the name specified. If an object
     * of the same name is already bound to the session, the object is
     * replaced.
     * <p/>
     * <p>After this method executes, and if the new object implements
     * <code>HttpSessionBindingListener</code>, the container calls
     * <code>HttpSessionBindingListener.valueBound</code>. The container then
     * notifies any <code>HttpSessionAttributeListener</code>s in the web
     * application.
     * <p/>
     * <p>If an object was already bound to this session of this name
     * that implements <code>HttpSessionBindingListener</code>, its
     * <code>HttpSessionBindingListener.valueUnbound</code> method is called.
     * <p/>
     * <p>If the value passed in is null, this has the same effect as calling
     * <code>removeAttribute()<code>.
     *
     * @param name  the name to which the object is bound; cannot be null
     * @param value the object to be bound
     * @throws IllegalStateException if this method is called on an
     *                               invalidated session
     */
    @Override
    public void setAttribute(String name, Object value) {
        this.attributes.put(name, value);
    }

    /**
     * @param name  the name to which the object is bound; cannot be null
     * @param value the object to be bound; cannot be null
     * @throws IllegalStateException if this method is called on an
     *                               invalidated session
     * @deprecated As of Version 2.2, this method is replaced by
     *             {@link #setAttribute}
     */
    @Override
    public void putValue(String name, Object value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Removes the object bound with the specified name from this session.
     * If the session does not have an object bound with the specified name,
     * this method does nothing.
     * <p/>
     * <p>After this method executes, and if the object implements
     * <code>HttpSessionBindingListener</code>, the container calls
     * <code>HttpSessionBindingListener.valueUnbound</code>. The container
     * then notifies any <code>HttpSessionAttributeListener</code>s in the web
     * application.
     *
     * @param name the name of the object to remove from this session
     * @throws IllegalStateException if this method is called on an
     *                               invalidated session
     */
    @Override
    public void removeAttribute(String name) {
        this.attributes.remove(name);
    }

    /**
     * @param name the name of the object to remove from this session
     * @throws IllegalStateException if this method is called on an
     *                               invalidated session
     * @deprecated As of Version 2.2, this method is replaced by
     *             {@link #removeAttribute}
     */
    @Override
    public void removeValue(String name) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Invalidates this session then unbinds any objects bound to it.
     *
     * @throws IllegalStateException if this method is called on an already
     *                               invalidated session
     */
    @Override
    public void invalidate() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Returns <code>true</code> if the client does not yet know about the
     * session or if the client chooses not to join the session.  For
     * example, if the server used only cookie-based sessions, and
     * the client had disabled the use of cookies, then a session would
     * be new on each request.
     *
     * @return <code>true</code> if the server has created a session, but the
     *         client has not yet joined
     * @throws IllegalStateException if this method is called on an already
     *                               invalidated session
     */
    @Override
    public boolean isNew() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
