package cn.pmj.lucene.chapter2;

/**
 * Copyright Manning Publications Co.
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
 * See the License for the specific lan      
*/

import cn.pmj.lucene.common.TestUtil;
import junit.framework.TestCase;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;

import java.io.File;
import java.io.IOException;

// From chapter 2
public class LockTest extends TestCase {

  private Directory dir;
  private File indexDir;

  protected void setUp() throws IOException {
    indexDir = new File(
      System.getProperty("java.io.tmpdir", "tmp") +
      System.getProperty("file.separator") + "index");
    dir = FSDirectory.open(indexDir);
  }

  /**
   * 一个索引只能同时被一个writer打开,保证这点的基础是会有一个write.lock文件
   * @throws IOException
   */
  public void testWriteLock() throws IOException {

    IndexWriter writer1 = new IndexWriter(dir, new SimpleAnalyzer(),
                                          IndexWriter.MaxFieldLength.UNLIMITED);
    IndexWriter writer2 = null;
    try {
      writer2 = new IndexWriter(dir, new SimpleAnalyzer(),
                                IndexWriter.MaxFieldLength.UNLIMITED);
      fail("We should never reach this point");
    }
    catch (LockObtainFailedException e) {
      e.printStackTrace();  // #A
    }
    finally {
      writer1.close();
      assertNull(writer2);
      TestUtil.rmDir(indexDir);
    }
  }
}

/*
#A Expected exception: only one IndexWriter allowed at once
*/
