Alfresco Share OAuth SSO Support
================================

The Alfresco OAuth SSO module lets users login in Alfresco Share using their Google Apps account. The module creates
automatically accounts which do not exist to make Share and Google Apps integrate transparently. Configuration uses
standard Share Surf configuration files. The jar is packaged with all dependencies so that it can be deployed as a single jar.

Installation
============

I. Create a Google API Access

Go to Google API Console and create your API (https://code.google.com/apis/console)

II. Deploy the library

Copy the oauth-login-module jar in your Share webapp lib folder ([$WEBAPP_FOLDER]/share/WEB-INF/lib).

III. Add the filter to web.xml

Add the following snippet to your Share web.xml before the filter named "Authentication filter"

    <filter>
        <description>Oauth Authentication Support</description>
        <filter-name>OAuthAuthenticationFilter</filter-name>
        <filter-class>ch.gadp.alfresco.OAuthSSOAuthenticationFilter</filter-class>
    </filter>

and add the following filter-mappings before the "Authentication Filter" mappings

    <filter-mapping>
        <filter-name>OAuthAuthenticationFilter</filter-name>
        <url-pattern>/page/*</url-pattern>
    </filter-mapping>
    <filter-mapping>
        <filter-name>OAuthAuthenticationFilter</filter-name>
        <url-pattern>/p/*</url-pattern>
    </filter-mapping>


IV. Configure the OAuth filter
Add the following configuration to the share-config-custom.xml file. This file generally stands in the external
configuration directory of Share. In the standard tomcat installation, the Share external configuration is located in
tomcat/shared/classes/alfresco/web-extension directory.

Example of configuration

    <alfresco-config>
        <config evaluator="string-compare" condition="OAuthFilter"> <!-- the condition must always be OAuthFilter -->
            <repository>
                <!-- The host of the Alfresco repository webapp -->
                <host>localhost</host>
                <!-- The port of the Alfresco repository webapp. Put 80 for standard HTTP-->
                <port>8080</port>
                <!-- The protocol to access the Alfresco repository -->
                <protocol>http</protocol>
                  <!-- The API access URI. If you use standard Alfresco, this should not change -->
                <api>/alfresco/service/api</api>
                   <!-- The admin user who is able to create new users -->
                <admin>admin</admin>
                <!-- The password of the admin user -->
                <password>admin</password>
                <!-- The unique password for all users authenticated with OAuth. Choose one very complicated :) -->
                <user-password>gotpxdKFjA_uxzG5SdGu</user-password>
                <!-- The domains for which incoming user should be restricted to. If blank, any valid email will be accepted -->
                <user-domains>nxc.no</user-domains>
            </repository>
            <oauth-api>
                <!-- The key of your API application -->
                <key>562961831703.apps.googleusercontent.com</key>
                <!-- The URI from which get the user profile informations -->
                <uri>https://www.googleapis.com/oauth2/v1/userinfo</uri>
                <!-- The secret of your API application -->
                <secret>sgYrMLRbElLNfL4Q7oxVVwby</secret>
                <!-- The scope(s) for getting data -->
                <scope>https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email</scope>
                <!-- This is a constant and my be different in future releases -->
                <name>GoogleApi</name>
            </oauth-api>
        </config>
    </alfresco-config>

Build notes
===========
The standard package build generates a shaded jar with dependencies included for easier deployment.

The Maven has a webapp profile which builds an exploded share war. Running the "integration-test" launches the embedded
jetty server with a custom web.xml and the filter deployed.

License
=======

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
