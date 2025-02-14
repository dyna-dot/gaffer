/*
 * Copyright 2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.gchq.gaffer.federatedstore.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.common.collect.Lists;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.federatedstore.FederatedStoreConstants;
import uk.gov.gchq.gaffer.federatedstore.PredefinedFederatedStore;
import uk.gov.gchq.gaffer.integration.AbstractStoreIT;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;

/**
 * In all of theses tests the Federated graph contains two graphs, one
 * containing a schema with only edges the other with only entities.
 */
public class FederatedViewsIT extends AbstractStoreIT {

  public static final String BASIC_EDGE = "BasicEdge";
  public static final String BASIC_ENTITY = "BasicEntity";

  @Test
  public void shouldBeEmptyAtStart() throws OperationException {

    final CloseableIterable<? extends Element> rtn =
        graph.execute(new GetAllElements.Builder()
                          .view(new View.Builder().edge(BASIC_EDGE).build())
                          .build(),
                      user);

    assertFalse(rtn.iterator().hasNext());
  }

  /**
   * Federation acts as a Edge/Entity graph with a view of Edge
   *
   * @throws OperationException any
   */
  @Test
  public void shouldAddAndGetEdge() throws OperationException {

    addBasicEdge();

    final CloseableIterable<? extends Element> rtn =
        graph.execute(new GetAllElements.Builder()
                          .view(new View.Builder().edge(BASIC_EDGE).build())
                          .build(),
                      user);

    assertTrue(rtn.iterator().hasNext());
  }

  /**
   * Federation acts as a Edge/Entity graph with a view of Entity
   *
   * @throws OperationException any
   */
  @Test
  public void shouldAddAndGetEntity() throws OperationException {

    addBasicEntity();

    final CloseableIterable<? extends Element> rtn =
        graph.execute(new GetAllElements.Builder()
                          .view(new View.Builder().entity(BASIC_ENTITY).build())
                          .build(),
                      user);

    assertTrue(rtn.iterator().hasNext());
  }

  /**
   * Federation acts as a Edge graph with a view of Edge
   *
   * @throws OperationException any
   */
  @Test
  public void shouldAddAndGetEdgeWithEdgeGraph() throws OperationException {

    addBasicEdge();

    final CloseableIterable<? extends Element> rtn = graph.execute(
        new GetAllElements.Builder()
            .view(new View.Builder().edge(BASIC_EDGE).build())
            .option(FederatedStoreConstants.KEY_OPERATION_OPTIONS_GRAPH_IDS,
                    PredefinedFederatedStore.ACCUMULO_GRAPH_WITH_EDGES)
            .build(),
        user);

    assertTrue(rtn.iterator().hasNext());
  }

  /**
   * Federation acts as a Entity graph with a view of Entity
   *
   * @throws OperationException any
   */
  @Test
  public void shouldAddAndGetEntityWithEntityGraph() throws OperationException {

    addBasicEntity();

    final CloseableIterable<? extends Element> rtn = graph.execute(
        new GetAllElements.Builder()
            .view(new View.Builder().entity(BASIC_ENTITY).build())
            .option(FederatedStoreConstants.KEY_OPERATION_OPTIONS_GRAPH_IDS,
                    PredefinedFederatedStore.ACCUMULO_GRAPH_WITH_ENTITIES)
            .build(),
        user);

    assertTrue(rtn.iterator().hasNext());
  }

  /**
   * Federation acts as a Entity graph with a view of Edge
   *
   * @throws OperationException any
   */
  @Test
  public void shouldNotAddAndGetEdgeWithEntityGraph()
      throws OperationException {

    addBasicEdge();

    try {
      final CloseableIterable<? extends Element> rtn = graph.execute(
          new GetAllElements.Builder()
              .view(new View.Builder().edge(BASIC_EDGE).build())
              .option(FederatedStoreConstants.KEY_OPERATION_OPTIONS_GRAPH_IDS,
                      PredefinedFederatedStore.ACCUMULO_GRAPH_WITH_ENTITIES)
              .build(),
          user);

      fail("exception expected");
    } catch (Exception e) {
      assertEquals(
          "Operation chain is invalid. Validation errors: \n"
              +
              "View is not valid for graphIds:[AccumuloStoreContainingEntities]\n"
              +
              "View for operation uk.gov.gchq.gaffer.operation.impl.get.GetAllElements is not valid. \n"
              + "Edge group BasicEdge does not exist in the schema",
          e.getMessage());
    }
  }

  /**
   * Federation acts as a Edge graph with a view of Entity
   *
   * @throws OperationException any
   */
  @Test
  public void shouldNotAddAndGetEntityWithEntityGraph()
      throws OperationException {

    addBasicEntity();

    try {
      final CloseableIterable<? extends Element> rtn = graph.execute(
          new GetAllElements.Builder()
              .view(new View.Builder().entity(BASIC_ENTITY).build())
              .option(FederatedStoreConstants.KEY_OPERATION_OPTIONS_GRAPH_IDS,
                      PredefinedFederatedStore.ACCUMULO_GRAPH_WITH_EDGES)
              .build(),
          user);
      fail("exception expected");
    } catch (Exception e) {
      assertEquals(
          "Operation chain is invalid. Validation errors: \n"
              +
              "View is not valid for graphIds:[AccumuloStoreContainingEdges]\n"
              +
              "View for operation uk.gov.gchq.gaffer.operation.impl.get.GetAllElements is not valid. \n"
              + "Entity group BasicEntity does not exist in the schema",
          e.getMessage());
    }
  }

  /**
   * Federation acts as a Edge/Entity graph with a view of Edge and Entity
   *
   * @throws OperationException any
   */
  @Test
  public void shouldGetEntitiesAndEdgesFromAnEntityAndAnEdgeGraph()
      throws OperationException {
    addBasicEntity();
    addBasicEdge();

    final CloseableIterable<? extends Element> rtn =
        graph.execute(new GetAllElements.Builder()
                          .view(new View.Builder()
                                    .edge(BASIC_EDGE)
                                    .entity(BASIC_ENTITY)
                                    .build())
                          .build(),
                      user);

    assertTrue(rtn.iterator().hasNext());
  }

  protected void addBasicEdge() throws OperationException {
    graph.execute(new AddElements.Builder()
                      .input(Lists.newArrayList(new Edge.Builder()
                                                    .group(BASIC_EDGE)
                                                    .source("a")
                                                    .dest("b")
                                                    .build()))
                      .build(),
                  user);
  }

  protected void addBasicEntity() throws OperationException {
    graph.execute(
        new AddElements.Builder()
            .input(Lists.newArrayList(
                new Entity.Builder().group(BASIC_ENTITY).vertex("a").build()))
            .build(),
        user);
  }
}
