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

package org.apache.iotdb.db.tools;

import static org.apache.iotdb.db.writelog.node.ExclusiveWriteLogNode.WAL_FILE_NAME;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.iotdb.db.exception.SysCheckException;
import org.apache.iotdb.db.writelog.io.RAFLogReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WalChecker verifies that whether all write ahead logs in the WAL folder are recognizable.
 */
public class WalChecker {

  private static final Logger LOGGER = LoggerFactory.getLogger(WalChecker.class);

  /**
   * the root dir of wals, which should have wal directories of storage groups as its children.
   */
  private String walFolder;

  public WalChecker(String walFolder) {
    this.walFolder = walFolder;
  }

  /**
   * check the root wal dir and find the damaged files
   * @return a list of damaged files.
   * @throws SysCheckException if the root wal dir does not exist.
   */
  public List<File> doCheck() throws SysCheckException {
    File walFolderFile = new File(walFolder);
    if(!walFolderFile.exists() || !walFolderFile.isDirectory()) {
      throw new SysCheckException(String.format("%s is not a directory", walFolder));
    }

    File[] storageWalFolders = walFolderFile.listFiles();
    if (storageWalFolders == null || storageWalFolders.length == 0) {
      LOGGER.info("No sub-directories under the given directory, check ends");
      return Collections.emptyList();
    }

    List<File> failedFiles = new ArrayList<>();
    for (int dirIndex = 0; dirIndex < storageWalFolders.length; dirIndex++) {
      File storageWalFolder = storageWalFolders[dirIndex];
      LOGGER.debug("Checking the No.{} directory {}", dirIndex, storageWalFolder.getName());
      File walFile = new File(storageWalFolder, WAL_FILE_NAME);
      if (!walFile.exists()) {
        LOGGER.debug("No wal file in this dir, skipping");
        continue;
      }

      RAFLogReader logReader = null;
      try {
        logReader = new RAFLogReader(walFile);
        while (logReader.hasNext()) {
          logReader.next();
        }
      } catch (IOException e) {
        failedFiles.add(walFile);
        LOGGER.error("{} fails the check because", walFile.getAbsoluteFile(), e);
      } finally {
        if( logReader != null) {
          logReader.close();
        }
      }
    }
    return failedFiles;
  }

  // a temporary method which should be in the integrated self-check module in the future
  public static void report(List<File> failedFiles) {
    if (failedFiles.isEmpty()) {
      LOGGER.info("Check finished. There is no damaged file");
    } else {
      LOGGER.error("There are {} failed files. They are {}", failedFiles.size(), failedFiles);
    }
  }

  /**
   *
   * @param args walRootDirectory
   */
  public static void main(String[] args) throws SysCheckException {
    if (args.length < 1) {
      LOGGER.error("No enough args: require the walRootDirectory");
      return;
    }

    WalChecker checker = new WalChecker(args[0]);
    List<File> files = checker.doCheck();
    report(files);
  }
}
