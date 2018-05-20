package org.apache.solr.search.facet;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// base class for facet functions that can be used in a sort
public abstract class FacetSortableMerger extends FacetMerger {
  public void prepareSort() {
  }

  @Override
  public void finish(Context mcontext) {
    // nothing to do for simple stats...
  }

  /**
   * Return the normal comparison sort order.  The sort direction is only to be used in special circumstances (such as making NaN sort
   * last regardless of sort order.)  Normal sorters do not need to pay attention to direction.
   */
  public abstract int compareTo(FacetSortableMerger other, FacetRequest.SortDirection direction);
}
