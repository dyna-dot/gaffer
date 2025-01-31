/*
 * Copyright 2018-2019 Crown Copyright
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

package uk.gov.gchq.gaffer.operation.impl.join.methods;

import java.util.ArrayList;
import java.util.List;
import uk.gov.gchq.koryphe.tuple.MapTuple;

/**
 * {@code OuterJoin} is a Join function which returns MapTuples containing the
 * keys which have no matches with the other side.
 */
public class OuterJoin extends JoinFunction {

  /**
   * Returns a list containing a single value if the key does not match. Returns
   * an empty list otherwise.
   * @param key The key
   * @param matches a list containing the matches
   * @param keyName the name of the keyed side (LEFT or RIGHT)
   * @param matchingValuesName the corresponding value side (LEFT or RIGHT)
   * @return a list which may contain a single non matching key
   */
  @Override
  protected List<MapTuple> joinFlattened(final Object key, final List matches,
                                         final String keyName,
                                         final String matchingValuesName) {
    List<MapTuple> resultList = new ArrayList<>();

    if (matches.isEmpty()) {
      MapTuple<String> unMatchedPair = new MapTuple<>();
      unMatchedPair.put(keyName, key);
      unMatchedPair.put(matchingValuesName, null);
      resultList.add(unMatchedPair);
    }

    return resultList;
  }

  /**
   * Returns a {@code MapTuple} if the key has no matches. If it does, null is
   * returned.
   * @param key The key
   * @param matches a list containing the matches
   * @param keyName the name of the keyed side (LEFT or RIGHT)
   * @param matchingValuesName the corresponding value side (LEFT or RIGHT)
   * @return A MapTuple if the key has not matches. If it does, null is returned
   */
  @Override
  protected MapTuple joinAggregated(final Object key, final List matches,
                                    final String keyName,
                                    final String matchingValuesName) {
    if (matches.isEmpty()) {
      MapTuple<String> allMatchingValues = new MapTuple<>();
      allMatchingValues.put(keyName, key);
      allMatchingValues.put(matchingValuesName, matches);
      return allMatchingValues;
    } else {
      return null;
    }
  }
}
