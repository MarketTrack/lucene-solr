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

public abstract class FacetDoubleMerger extends FacetSortableMerger {
  @Override
  public abstract void merge(Object facetResult, Context mcontext);

  protected abstract double getDouble();

  @Override
  public Object getMergedResult() {
    return getDouble();
  }


  @Override
  public int compareTo(FacetSortableMerger other, FacetRequest.SortDirection direction) {
    return compare(getDouble(), ((FacetDoubleMerger) other).getDouble(), direction);
  }


  public static int compare(double a, double b, FacetRequest.SortDirection direction) {
    if (a < b) return -1;
    if (a > b) return 1;

    if (a != a) {  // a==NaN
      if (b != b) {
        return 0;  // both NaN
      }
      return -1 * direction.getMultiplier();  // asc==-1, so this will put NaN at end of sort
    }

    if (b != b) { // b is NaN so a is greater
      return 1 * direction.getMultiplier();  // if sorting asc, make a less so NaN is at end
    }

    // consider +-0 to be equal
    return 0;
  }
}
