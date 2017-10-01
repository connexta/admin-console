/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.admin.graphql.test;

import java.util.Arrays;
import org.codice.ddf.admin.common.fields.test.TestFieldProvider;
import org.codice.ddf.admin.graphql.servlet.ExtendedOsgiGraphQLServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * This class will bind the field provider to the graphql servlet and run a server to host the
 * schema. The schema can be found at http://localhost:8994/schema.json. To navigate the schema more
 * easily, use a graphql schema navigator such as graphiql. This will help validate queries and
 * create readable documentation.
 */
public class RunTestServlet {

  public static final int TEST_SERVLET_PORT = 8994;

  public static void main(String[] args) throws Exception {
    Server server = new Server(TEST_SERVLET_PORT);
    ServletContextHandler context = new ServletContextHandler(server, "/");
    context.addServlet(TestServlet.class, "/*");
    System.out.println(
        "Running servlet. Graphql endpoint at http://localhost:" + TEST_SERVLET_PORT + "/graphql");
    server.start();
  }

  public static class TestServlet extends ExtendedOsgiGraphQLServlet {
    public TestServlet() {
      super();
      setFieldProviders(Arrays.asList(new TestFieldProvider()));
      bindFieldProvider(new TestFieldProvider());
    }
  }
}
