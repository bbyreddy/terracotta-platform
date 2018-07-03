/*
 * Copyright Terracotta, Inc.
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
package org.terracotta.runnel.encoding.dataholders;

import org.junit.jupiter.api.Test;
import org.terracotta.runnel.utils.ReadBuffer;
import org.terracotta.runnel.utils.WriteBuffer;

import java.nio.ByteBuffer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Ludovic Orban
 */
public class FloatingPoint64Test {

  @Test
  public void testWithIndex() throws Exception {
    FloatingPoint64DataHolder dataHolder = new FloatingPoint64DataHolder(Double.NaN, 1);

    assertThat(dataHolder.size(true), is(10));

    ByteBuffer bb = ByteBuffer.allocate(dataHolder.size(true));
    dataHolder.encode(new WriteBuffer(bb), true);
    assertThat(bb.position(), is(10));
    bb.rewind();
    ReadBuffer readBuffer = new ReadBuffer(bb);
    assertThat(readBuffer.getVlqInt(), is(1));
    assertThat(readBuffer.getVlqInt(), is(8));
    assertThat(readBuffer.getDouble(), is(Double.NaN));
  }

  @Test
  public void testWithoutIndex() throws Exception {
    FloatingPoint64DataHolder dataHolder = new FloatingPoint64DataHolder(5.5, 1);

    assertThat(dataHolder.size(false), is(9));

    ByteBuffer bb = ByteBuffer.allocate(dataHolder.size(false));
    dataHolder.encode(new WriteBuffer(bb), false);
    assertThat(bb.position(), is(9));
    bb.rewind();
    ReadBuffer readBuffer = new ReadBuffer(bb);
    assertThat(readBuffer.getVlqInt(), is(8));
    assertThat(readBuffer.getDouble(), is(5.5));
  }


}
