/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.iotdb.cluster.rpc.response;

import java.util.Set;

public class QueryStorageGroupResponse extends BasicResponse {

  private Set<String> storageGroups;

  public QueryStorageGroupResponse(boolean redirected, boolean success, String leaderStr, String errorMsg) {
    super(redirected, success, leaderStr, errorMsg);
  }

  public QueryStorageGroupResponse(boolean redirected, boolean success, Set<String> storageGroups) {
    super(redirected, success, null, null);
    this.storageGroups = storageGroups;
  }

  public Set<String> getStorageGroups() {
    return storageGroups;
  }

  public void setStorageGroups(Set<String> storageGroups) {
    this.storageGroups = storageGroups;
  }
}