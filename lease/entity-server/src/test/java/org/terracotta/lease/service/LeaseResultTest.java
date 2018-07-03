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
package org.terracotta.lease.service;

import org.junit.jupiter.api.Test;

import com.tc.classloader.CommonComponent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LeaseResultTest {
  @Test
  public void leaseGranted() {
    LeaseResult result = LeaseResult.leaseGranted(5L);
    assertTrue(result.isLeaseGranted());
    assertEquals(5L, result.getLeaseLength());
  }

  @Test
  public void leaseNotGranted() {
    LeaseResult result = LeaseResult.leaseNotGranted();
    assertFalse(result.isLeaseGranted());
  }

  @Test
  public void leaseNotGrantedDoesNotAllowGettingTheLength() {
    assertThrows(IllegalStateException.class, () -> {
      LeaseResult result = LeaseResult.leaseNotGranted();
      result.getLeaseLength();
    });
  }

  @Test
  public void noZeroLengthLeases() {
    assertThrows(IllegalArgumentException.class, () -> {
      LeaseResult.leaseGranted(0L);
    });
  }

  @Test
  public void noNegativeLengthLeases() {
    assertThrows(IllegalArgumentException.class, () -> {
      LeaseResult.leaseGranted(-1L);
    });
  }

  @Test
  public void isCommonComponent() {
    assertNotNull(LeaseResult.class.getAnnotation(CommonComponent.class));
  }
}
