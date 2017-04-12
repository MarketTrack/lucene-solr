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


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.solr.common.params.FacetParams;
import org.apache.solr.common.util.SimpleOrderedMap;

public class FacetRangeMerger extends FacetBucketMerger<FacetRange> {
  FacetBucket beforeBucket;
  FacetBucket afterBucket;
  FacetBucket betweenBucket;

  LinkedHashMap<Object, FacetBucket> buckets = new LinkedHashMap<Object, FacetBucket>();


  public FacetRangeMerger(FacetRange freq) {
    super(freq);
  }

  @Override
  FacetMerger createFacetMerger(String key, Object val) {
    return super.createFacetMerger(key, val);
  }

  @Override
  public void merge(Object facetResult, Context mcontext) {
    merge((SimpleOrderedMap) facetResult , mcontext);
  }

  public void merge(SimpleOrderedMap facetResult, Context mcontext) {
    boolean all = freq.others.contains(FacetParams.FacetRangeOther.ALL);

    if (all || freq.others.contains(FacetParams.FacetRangeOther.BEFORE)) {
      Object o = facetResult.get("before");
      if (o != null) {
        if (beforeBucket == null) {
          beforeBucket = newBucket(null);
        }
        beforeBucket.mergeBucket((SimpleOrderedMap)o, mcontext);
      }
    }

    if (all || freq.others.contains(FacetParams.FacetRangeOther.AFTER)) {
      Object o = facetResult.get("after");
      if (o != null) {
        if (afterBucket == null) {
          afterBucket = newBucket(null);
        }
        afterBucket.mergeBucket((SimpleOrderedMap)o , mcontext);
      }
    }

    if (all || freq.others.contains(FacetParams.FacetRangeOther.BETWEEN)) {
      Object o = facetResult.get("between");
      if (o != null) {
        if (betweenBucket == null) {
          betweenBucket = newBucket(null);
        }
        betweenBucket.mergeBucket((SimpleOrderedMap)o , mcontext);
      }
    }

    List<SimpleOrderedMap> bucketList = (List<SimpleOrderedMap>) facetResult.get("buckets");
    mergeBucketList(bucketList , mcontext);
  }

  // TODO: share more merging with field faceting
  public void mergeBucketList(List<SimpleOrderedMap> bucketList, Context mcontext) {
    for (SimpleOrderedMap bucketRes : bucketList) {
      Comparable bucketVal = (Comparable)bucketRes.get("val");
      FacetBucket bucket = buckets.get(bucketVal);
      if (bucket == null) {
        bucket = newBucket(bucketVal);
        buckets.put(bucketVal, bucket);
      }
      bucket.mergeBucket( bucketRes , mcontext );
    }
  }

  @Override
  public Object getMergedResult() {
    SimpleOrderedMap result = new SimpleOrderedMap(4);

    List<SimpleOrderedMap> resultBuckets = new ArrayList<>(buckets.size());

    for (FacetBucket bucket : buckets.values()) {
      if (bucket.getCount() < freq.mincount) {
        continue;
      }
      resultBuckets.add( bucket.getMergedBucket() );
    }

    result.add("buckets", resultBuckets);

    if (beforeBucket != null) {
      result.add("before", beforeBucket.getMergedBucket());
    }
    if (afterBucket != null) {
      result.add("after", afterBucket.getMergedBucket());
    }
    if (betweenBucket != null) {
      result.add("between", betweenBucket.getMergedBucket());
    }
    return result;

  }
}
