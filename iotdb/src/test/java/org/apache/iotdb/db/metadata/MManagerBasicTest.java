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
package org.apache.iotdb.db.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.iotdb.db.exception.MetadataArgsErrorException;
import org.apache.iotdb.db.exception.PathErrorException;
import org.apache.iotdb.db.utils.EnvironmentUtils;
import org.apache.iotdb.tsfile.common.conf.TSFileConfig;
import org.apache.iotdb.tsfile.file.metadata.enums.CompressionType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSEncoding;
import org.apache.iotdb.tsfile.read.common.Path;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MManagerBasicTest {

  CompressionType compressionType;

  @Before
  public void setUp() throws Exception {
    compressionType = CompressionType.valueOf(TSFileConfig.compressor);
  }

  @After
  public void tearDown() throws Exception {
    EnvironmentUtils.cleanEnv();
  }

  @Test
  public void testAddPathAndExist() {

    MManager manager = MManager.getInstance();
    assertEquals(manager.pathExist("root"), true);

    assertEquals(manager.pathExist("root.laptop"), false);

    try {
      manager.setStorageLevelToMTree("root.laptop.d1");
    } catch (PathErrorException | IOException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }

    try {
      manager.setStorageLevelToMTree("root.laptop");
    } catch (PathErrorException | IOException e) {
      Assert.assertEquals(
          "The seriesPath of root.laptop already exist, it can't be set to the storage group",
          e.getMessage());
    }

    try {
      manager.addPathToMTree("root.laptop.d1.s0", TSDataType.valueOf("INT32"),
          TSEncoding.valueOf("RLE"), compressionType, Collections
              .emptyMap());
    } catch (PathErrorException | MetadataArgsErrorException | IOException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
    assertEquals(manager.pathExist("root.laptop"), true);
    assertEquals(manager.pathExist("root.laptop.d1"), true);
    assertEquals(manager.pathExist("root.laptop.d1.s0"), true);
    assertEquals(manager.pathExist("root.laptop.d1.s1"), false);
    try {
      manager.addPathToMTree("root.laptop.d1.s1", TSDataType.valueOf("INT32"),
          TSEncoding.valueOf("RLE"), compressionType, Collections.emptyMap());
    } catch (PathErrorException | MetadataArgsErrorException | IOException e1) {
      e1.printStackTrace();
      fail(e1.getMessage());
    }
    assertEquals(manager.pathExist("root.laptop.d1.s1"), true);
    try {
      manager.deletePathFromMTree("root.laptop.d1.s1");
    } catch (PathErrorException | IOException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
    // just delete s0, and don't delete root.laptop.d1??
    // delete storage group or not
    assertEquals(manager.pathExist("root.laptop.d1.s1"), false);
    try {
      manager.deletePathFromMTree("root.laptop.d1.s0");
    } catch (PathErrorException | IOException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
    assertEquals(manager.pathExist("root.laptop.d1.s0"), false);
    assertEquals(manager.pathExist("root.laptop.d1"), true);
    assertEquals(manager.pathExist("root.laptop"), true);
    assertEquals(manager.pathExist("root"), true);

    // can't delete the storage group

    // try {
    // manager.setStorageLevelToMTree("root.laptop");
    // } catch (PathErrorException | IOException e) {
    // fail(e.getMessage());
    // }
    try {
      manager.addPathToMTree("root.laptop.d1.s1", TSDataType.valueOf("INT32"),
          TSEncoding.valueOf("RLE"), compressionType, Collections.emptyMap());
    } catch (PathErrorException | MetadataArgsErrorException | IOException e1) {
      e1.printStackTrace();
      fail(e1.getMessage());
    }

    try {
      manager.addPathToMTree("root.laptop.d1.s0", TSDataType.valueOf("INT32"),
          TSEncoding.valueOf("RLE"), compressionType, Collections.emptyMap());
    } catch (PathErrorException | MetadataArgsErrorException | IOException e1) {
      e1.printStackTrace();
      fail(e1.getMessage());
    }

    assertEquals(false, manager.pathExist("root.laptop.d2"));
    assertEquals(false, manager.checkFileNameByPath("root.laptop.d2"));

    try {
      manager.deletePathFromMTree("root.laptop.d1.s0");
    } catch (PathErrorException | IOException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
    try {
      manager.deletePathFromMTree("root.laptop.d1.s1");
    } catch (PathErrorException | IOException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }

    try {
      manager.setStorageLevelToMTree("root.laptop.d2");
    } catch (PathErrorException | IOException e) {
      Assert.assertEquals(
          String.format("The seriesPath of %s already exist, it can't be set to the storage group",
              "root.laptop.d2"),
          e.getMessage());
    }
    /*
     * check file level
     */
    assertEquals(manager.pathExist("root.laptop.d2.s1"), false);
    List<Path> paths = new ArrayList<>();
    paths.add(new Path("root.laptop.d2.s1"));
    try {
      manager.checkFileLevel(paths);
    } catch (PathErrorException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }

    try {
      manager.addPathToMTree("root.laptop.d2.s1", TSDataType.valueOf("INT32"),
          TSEncoding.valueOf("RLE"), compressionType, Collections.emptyMap());
    } catch (PathErrorException | MetadataArgsErrorException | IOException e1) {
      e1.printStackTrace();
      fail(e1.getMessage());
    }

    try {
      manager.addPathToMTree("root.laptop.d2.s0", TSDataType.valueOf("INT32"),
          TSEncoding.valueOf("RLE"), compressionType, Collections.emptyMap());
    } catch (PathErrorException | MetadataArgsErrorException | IOException e1) {
      e1.printStackTrace();
      fail(e1.getMessage());
    }

    try {
      manager.deletePathFromMTree("root.laptop.d2.s0");
    } catch (PathErrorException | IOException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
    try {
      manager.deletePathFromMTree("root.laptop.d2.s1");
    } catch (PathErrorException | IOException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }

    try {
      manager.addPathToMTree("root.laptop.d1.s0", TSDataType.valueOf("INT32"),
          TSEncoding.valueOf("RLE"), compressionType, Collections.emptyMap());
    } catch (PathErrorException | MetadataArgsErrorException | IOException e1) {
      e1.printStackTrace();
      fail(e1.getMessage());
    }

    try {
      manager.addPathToMTree("root.laptop.d1.s1", TSDataType.valueOf("INT32"),
          TSEncoding.valueOf("RLE"), compressionType, Collections.emptyMap());
    } catch (PathErrorException | MetadataArgsErrorException | IOException e1) {
      e1.printStackTrace();
      fail(e1.getMessage());
    }

    paths = new ArrayList<>();
    paths.add(new Path("root.laptop.d1.s0"));
    try {
      manager.checkFileLevel(paths);
    } catch (PathErrorException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }

    try {
      manager.addPathToMTree("root.laptop.d1.s2", TSDataType.valueOf("INT32"),
          TSEncoding.valueOf("RLE"), compressionType, Collections.emptyMap());
    } catch (PathErrorException | MetadataArgsErrorException | IOException e1) {
      e1.printStackTrace();
      fail(e1.getMessage());
    }
    paths = new ArrayList<>();
    paths.add(new Path("root.laptop.d1.s2"));
    try {
      manager.checkFileLevel(paths);
    } catch (PathErrorException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }

    try {
      manager.addPathToMTree("root.laptop.d1.s3", TSDataType.valueOf("INT32"),
          TSEncoding.valueOf("RLE"), compressionType, Collections.emptyMap());
    } catch (PathErrorException | MetadataArgsErrorException | IOException e1) {
      e1.printStackTrace();
      fail(e1.getMessage());
    }
    paths = new ArrayList<>();
    paths.add(new Path("root.laptop.d1.s3"));
    try {
      manager.checkFileLevel(paths);
    } catch (PathErrorException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testSetStorageLevelAndExist() {

    MManager manager = MManager.getInstance();

    try {
      assertEquals(false, manager.checkStorageLevelOfMTree("root"));
      assertEquals(false, manager.checkStorageLevelOfMTree("root1.laptop.d2"));

      manager.setStorageLevelToMTree("root.laptop.d1");
      assertEquals(true, manager.checkStorageLevelOfMTree("root.laptop.d1"));
      assertEquals(false, manager.checkStorageLevelOfMTree("root.laptop.d2"));
      assertEquals(false, manager.checkStorageLevelOfMTree("root.laptop"));
      assertEquals(false, manager.checkStorageLevelOfMTree("root.laptop.d1.s1"));

      manager.setStorageLevelToMTree("root.laptop.d2");
      assertEquals(true, manager.checkStorageLevelOfMTree("root.laptop.d1"));
      assertEquals(true, manager.checkStorageLevelOfMTree("root.laptop.d2"));
      assertEquals(false, manager.checkStorageLevelOfMTree("root.laptop.d3"));
      assertEquals(false, manager.checkStorageLevelOfMTree("root.laptop"));
    } catch (PathErrorException | IOException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }
}
