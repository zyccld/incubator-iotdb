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
package org.apache.iotdb.db.auth.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.apache.iotdb.db.utils.AuthUtils;

/**
 * This class contains all information of a User.
 */
public class User {

  private String name;
  private String password;
  private List<PathPrivilege> privilegeList;
  private List<String> roleList;

  /**
   * The latest time when the user is referenced. Reserved to provide session control or LRU
   * mechanism in the future.
   */
  private long lastActiveTime;

  public User() {
    // empty constructor
  }

  /**
   * construct function for User.
   *
   * @param name -user name
   * @param password -user password
   */
  public User(String name, String password) {
    this.name = name;
    this.password = password;
    this.privilegeList = new ArrayList<>();
    this.roleList = new ArrayList<>();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public List<PathPrivilege> getPrivilegeList() {
    return privilegeList;
  }

  public void setPrivilegeList(List<PathPrivilege> privilegeList) {
    this.privilegeList = privilegeList;
  }

  public List<String> getRoleList() {
    return roleList;
  }

  public void setRoleList(List<String> roleList) {
    this.roleList = roleList;
  }

  public long getLastActiveTime() {
    return lastActiveTime;
  }

  public void setLastActiveTime(long lastActiveTime) {
    this.lastActiveTime = lastActiveTime;
  }

  public boolean hasPrivilege(String path, int privilegeId) {
    return AuthUtils.hasPrivilege(path, privilegeId, privilegeList);
  }

  public void addPrivilege(String path, int privilegeId) {
    AuthUtils.addPrivilege(path, privilegeId, privilegeList);
  }

  public void removePrivilege(String path, int privilegeId) {
    AuthUtils.removePrivilege(path, privilegeId, privilegeList);
  }

  /**
   * set the privilege.
   *
   * @param path -path
   * @param privileges -set of integer to determine privilege
   */
  public void setPrivileges(String path, Set<Integer> privileges) {
    for (PathPrivilege pathPrivilege : privilegeList) {
      if (pathPrivilege.getPath().equals(path)) {
        pathPrivilege.setPrivileges(privileges);
      }
    }
  }

  public boolean hasRole(String roleName) {
    return roleList.contains(roleName);
  }

  public Set<Integer> getPrivileges(String path) {
    return AuthUtils.getPrivileges(path, privilegeList);
  }

  public boolean checkPrivilege(String path, int privilegeId) {
    return AuthUtils.checkPrivilege(path, privilegeId, privilegeList);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;

    return lastActiveTime == user.lastActiveTime && contentEquals(user);
  }

  private boolean contentEquals(User user) {
    return Objects.equals(name, user.name)
            && Objects.equals(password, user.password)
            && Objects.equals(privilegeList, user.privilegeList)
            && Objects.equals(roleList, user.roleList);
  }


  @Override
  public int hashCode() {
    return Objects.hash(name, password, privilegeList, roleList, lastActiveTime);
  }
}
