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

/**
 * Created with IntelliJ IDEA.
 * User: guy
 * Date: 11/14/12
 * Time: 2:31 PM
 */
public class TicketInfo {
    public Data data;

    class Data {
         public String ticket;
         public Data() {}
     }
}
