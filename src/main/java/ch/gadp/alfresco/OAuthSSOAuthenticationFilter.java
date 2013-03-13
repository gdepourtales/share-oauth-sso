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

import com.google.gson.Gson;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.routines.EmailValidator;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: guy
 * Date: 11/15/12
 * Time: 9:56 AM
 */
public class OAuthSSOAuthenticationFilter implements Filter {

    private static Log logger = LogFactory.getLog(OAuthSSOAuthenticationFilter.class);

    private static final String ATTR_OAUTH_REQUEST_TOKEN = "oauthRequestToken";

    private static final String REPOSITORY_PROTOCOL = "repository.protocol";
    private static final String REPOSITORY_HOST = "repository.host";
    private static final String REPOSITORY_PORT = "repository.port";
    private static final String REPOSITORY_API = "repository.api";
    private static final String REPOSITORY_ADMIN_USER = "repository.admin";
    private static final String REPOSITORY_ADMIN_PASSWORD = "repository.password";


    private static final String USER_DOMAIN = "repository.user-domains";
    private static final String USER_PASSWORD = "repository.user-password";


    private static final String API_KEY = "oauth-api.key";
    private static final String API_URI = "oauth-api.uri";
    private static final String API_SECRET = "oauth-api.secret";
    private static final String API_SCOPE = "oauth-api.scope";
    private static final String API_NAME = "oauth-api.name";
    private static final String API_PROMPT = "oauth-api.prompt";

    private static final String SCRIBE_API_PACKAGE = "org.scribe.builder.api";

    private static final String REPOSITORY_API_PEOPLE = "people";
    private static final String REPOSITORY_API_LOGIN =  "login";
    private static final String DEFAULT_TICKET_NAME = "alf_ticket";

    private ServletContext servletContext;


    private String getAPIUri(String service) {
        return getConfigurationValue(REPOSITORY_PROTOCOL) +
                "://" +
                getConfigurationValue(REPOSITORY_HOST) +
                ":" +
                getConfigurationValue(REPOSITORY_PORT) +
                getConfigurationValue(REPOSITORY_API) +
                "/" +
                service;

    }

    private String getConfigurationValue(String name) {
        ConfigService configService = (ConfigService) this.getApplicationContext().getBean("web.config");
        Config oauthConfig = configService.getConfig("OAuthFilter");

        if (oauthConfig == null) {
            throw new IllegalStateException("OAuth Filter has no configuration");
        }

        String[] parts = StringUtils.split(name, '.');

        ConfigElement mainConfig = oauthConfig.getConfigElement(parts[0]);
        if (mainConfig == null) {
            return null;
        }

        ConfigElement subConfig = mainConfig.getChild(parts[1]);
        if (subConfig == null) {
            return null;
        }

        return subConfig.getValue();
    }



    private Class<Api> getAPIClass(String name) {

        String fullName = SCRIBE_API_PACKAGE + "." + name;

        try {
            return (Class<Api>) Class.forName(fullName);
        } catch (ClassNotFoundException cnfe) {
            return null;
        }
    }

    private OAuthService getOAuthService(String callbackURL) {
        ServiceBuilder sb = new ServiceBuilder()
                .provider(this.getAPIClass(getConfigurationValue(API_NAME)))
                .apiKey(getConfigurationValue(API_KEY))
                .apiSecret(getConfigurationValue(API_SECRET))
                .scope(getConfigurationValue(API_SCOPE))
                .approvalPrompt(getConfigurationValue(API_PROMPT));
        if (StringUtils.isNotEmpty(callbackURL)) {
            sb.callback(callbackURL);
        }

        return sb.build();
    }


    protected void processNoRequestToken(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        OAuthService oauthService = this.getOAuthService(req.getRequestURL().toString());

        /*
        Token requestToken = oauthService.getRequestToken();
        req.getSession().setAttribute(ATTR_OAUTH_REQUEST_TOKEN, requestToken);
        */
        resp.sendRedirect(oauthService.getAuthorizationUrl(null));
    }


    private GoogleProfileInfo getUserProfile(HttpServletRequest req, HttpServletResponse resp, String authcode) throws IOException {
        OAuthService oauthService = this.getOAuthService(req.getRequestURL().toString());

        /*
        String oauthVerifierToken = req.getParameter("oauth_verifier");

        if (oauthVerifierToken == null) {
            this.processNoRequestToken(req, resp);
            return null;
        }
        */

        // getting access token
        // Verifier verifier = new Verifier(oauthVerifierToken);
        // Token accessToken = oauthService.getAccessToken(requestToken, verifier);

        Verifier verifier = new Verifier(authcode);
        Token accessToken = oauthService.getAccessToken(null, verifier);

        OAuthRequest request = new OAuthRequest(Verb.GET, getConfigurationValue(API_URI));
        oauthService.signRequest(accessToken, request);
        Response oauthResponse = request.send();

        Gson gson = new Gson();
        return gson.fromJson(oauthResponse.getBody(), GoogleProfileInfo.class);
    }


    /**
     * Gets an Alfresco authentication ticket to handle user creation and update
     * @return The new ticket
     * @throws IOException
     */
    protected String getAdminAlfrescoTicket() throws IOException {
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(getAPIUri(REPOSITORY_API_LOGIN));

        String input = "{ " +
                "\"username\" : \"" + getConfigurationValue(REPOSITORY_ADMIN_USER) + "\", " +
                "\"password\" : \"" + getConfigurationValue(REPOSITORY_ADMIN_PASSWORD) + "\" " +
                "}";
        method.setRequestEntity(new StringRequestEntity(input, "application/json", "utf-8"));
        int statusCode = client.executeMethod(method);

        if (statusCode != HttpStatus.SC_OK) {
            return null;
        }

        Gson ticketJSON = new Gson();
        TicketInfo ticket = ticketJSON.fromJson(method.getResponseBodyAsString(), TicketInfo.class);
        return ticket.data.ticket;
    }

    /**
     * Add the ticket parameter to the request
     * @param method The method to expand
     * @param ticket The ticket to use
     */
    protected void addTicketParameter(HttpMethodBase method, String ticket) {
        method.setPath(method.getPath() + "?" + DEFAULT_TICKET_NAME + "=" + ticket);
    }

    /**
     * Checks if the user exists in the Alfresco repository
     * @param username The user name
     * @param adminTicket The ticket used to perform the check
     * @return true if the user exists, otherwise false
     * @throws IOException
     */
    protected boolean userExists(String username, String adminTicket) throws IOException {
        GetMethod get = new GetMethod((getAPIUri(REPOSITORY_API_PEOPLE) + "/" + username));

        this.addTicketParameter(get, adminTicket);
        HttpClient client = new HttpClient();
        boolean exists = client.executeMethod(get) == HttpStatus.SC_OK;
        String userInfo = get.getResponseBodyAsString();
        return (exists);
    }




    protected String saveUser(String username, GoogleProfileInfo userInfo, String adminTicket, boolean newUser) throws IOException {

        EntityEnclosingMethod saveUserMethod;
        if (newUser) {
           saveUserMethod = new PostMethod(getAPIUri(REPOSITORY_API_PEOPLE));
        } else {
            saveUserMethod = new PutMethod(getAPIUri(REPOSITORY_API_PEOPLE) + "/" + username);
        }

        this.addTicketParameter(saveUserMethod, adminTicket);

        String input = "{ " +
                (newUser ? "\"userName\" : \"" + username + "\", " : "") +
                "\"firstName\" : \"" + userInfo.getGiven_name() + "\", " +
                "\"lastName\" : \"" + userInfo.getFamily_name() + "\", " +
                "\"email\" : \"" + userInfo.getEmail() + "\", " +
                (newUser ? "\"password\" : \"" + getConfigurationValue(USER_PASSWORD) + "\"" : "") +
                " }";

        saveUserMethod.setRequestEntity(new StringRequestEntity(input, "application/json", "utf-8"));
        HttpClient client = new HttpClient();

        return client.executeMethod(saveUserMethod) == HttpStatus.SC_OK ? username : null;
    }

    /**
     * Check if the user email is valid.
     * Only emails that match the accepted domains are validated
     * @param userEmail The email to check
     * @return true if the email is valid
     */
    protected boolean isUserValid(String userEmail) {

        if (!EmailValidator.getInstance().isValid(userEmail)) {
            return false;
        }

        String[] emailParts = StringUtils.split(userEmail, '@');

        if (emailParts.length != 2 || StringUtils.isBlank(emailParts[0]) || StringUtils.isBlank(emailParts[1])) {
            return false;
        }

        String domains = getConfigurationValue(USER_DOMAIN);
        if (StringUtils.isBlank(domains)) {
            return true;
        }

        for (String validDomain : StringUtils.split(domains, ',')) {
            if (StringUtils.isBlank(validDomain)) {
                continue;
            }
            if (StringUtils.trim(validDomain).equalsIgnoreCase(emailParts[1])) {
                return true;
            }
        }

        return false;
    }


    /**
     * Process the token received by the oauth authority
     * @param request The request
     * @param response The response
     * @param requestToken The Oauth token
     * @return The user name if correctly identified or null
     * @throws IOException
     * @throws ServletException
     */
    protected String processRequestToken(HttpServletRequest request, HttpServletResponse response, String authCode) throws IOException, ServletException {

        GoogleProfileInfo userInfo = this.getUserProfile(request, response, authCode);
        if (userInfo == null) {
            return null;
        }

        if (!isUserValid(userInfo.getEmail())) {
            return null;
        }

        String username = StringUtils.split(userInfo.getEmail(), '@')[0];

        String adminTicket = this.getAdminAlfrescoTicket();
        boolean newUser = !userExists(username, adminTicket);
        return this.saveUser(username, userInfo, adminTicket, newUser);

    }


    /**
     * Performs the OAuth authentication process
     * If the user has no valid request token, she/he is redirected to the API authorization page
     * Otherwise, the user is authenticated and saved (created if non existing, updated if existing)
     * @param request The initial request
     * @param response The response
     * @return The username if successfulyl authenticated, otherwise null
     * @throws IOException
     * @throws ServletException
     */
    protected String doOAuthAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {


        String authCode = request.getParameter("code");

        //Token requestToken = (Token) request.getSession().getAttribute(ATTR_OAUTH_REQUEST_TOKEN);

        if (authCode == null) {
            try {
                this.processNoRequestToken(request, response);
                return null;
            } catch (Exception e) {
                logger.debug("Authentication failed: " + e.getMessage());
            }
        } else {
            try {
                return this.processRequestToken(request, response, authCode);
            } catch (Exception e) {
                logger.debug("Authentication failed: " + e.getMessage());
            }
        }

        return null;
    }


    /**
     * Called by the web container to indicate to a filter that it is being placed into
     * service. The servlet container calls the init method exactly once after instantiating the
     * filter. The init method must complete successfully before the filter is asked to do any
     * filtering work. <br><br>
     * <p/>
     * The web container cannot place the filter into service if the init method either<br>
     * 1.Throws a ServletException <br>
     * 2.Does not return within a time period defined by the web container
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.servletContext = filterConfig.getServletContext();
    }

    /**
     * Run the filter
     *
     * @param servletRequest  ServletRequest
     * @param servletResponse ServletResponse
     * @param chain           FilterChain
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // Utility parameter to bypass oauth for the session
        if (request.getParameter("bypassOAuth") != null || request.getSession().getAttribute("share.bypassOAuth") != null) {
            request.getSession().setAttribute("share.bypassOAuth", true);
            chain.doFilter(servletRequest, servletResponse);
            return;
        }


        if (AuthenticationUtil.isAuthenticated(request)) {
            // Already authenticated
            chain.doFilter(request, response);
            return;
        }

        String username = this.doOAuthAuthentication(request, response);
        if (username != null) {
            UserFactory userFactory = (UserFactory) getApplicationContext().getBean("user.factory");
            boolean authenticated = userFactory.authenticate(request, username, getConfigurationValue(USER_PASSWORD));
            if (authenticated) {
                AuthenticationUtil.login(request, response, username);
            }
        }
        chain.doFilter(servletRequest, servletResponse);

    }

    /**
     * Called by the web container to indicate to a filter that it is being taken out of service. This
     * method is only called once all threads within the filter's doFilter method have exited or after
     * a timeout period has passed. After the web container calls this method, it will not call the
     * doFilter method again on this instance of the filter. <br><br>
     * <p/>
     * This method gives the filter an opportunity to clean up any resources that are being held (for
     * example, memory, file handles, threads) and make sure that any persistent state is synchronized
     * with the filter's current state in memory.
     */
    @Override
    public void destroy() {

    }


    /**
     * Retrieves the root application context
     *
     * @return application context
     */
    private ApplicationContext getApplicationContext() {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
    }
}
