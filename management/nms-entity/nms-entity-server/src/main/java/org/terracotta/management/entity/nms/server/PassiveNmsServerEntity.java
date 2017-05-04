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
package org.terracotta.management.entity.nms.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.entity.IEntityMessenger;
import org.terracotta.management.entity.nms.Nms;
import org.terracotta.management.model.call.ContextualCall;
import org.terracotta.management.model.call.ContextualReturn;
import org.terracotta.management.model.call.Parameter;
import org.terracotta.management.model.cluster.Cluster;
import org.terracotta.management.model.cluster.Server;
import org.terracotta.management.model.context.Context;
import org.terracotta.management.service.monitoring.ConsumerManagementRegistry;
import org.terracotta.management.service.monitoring.EntityMonitoringService;
import org.terracotta.management.service.monitoring.SharedManagementRegistry;
import org.terracotta.voltron.proxy.ClientId;
import org.terracotta.voltron.proxy.server.PassiveProxiedServerEntity;

import java.util.Objects;
import java.util.concurrent.Future;

/**
 * @author Mathieu Carbou
 */
class PassiveNmsServerEntity extends PassiveProxiedServerEntity implements Nms, NmsCallback {

  private static final Logger LOGGER = LoggerFactory.getLogger(PassiveNmsServerEntity.class);

  private final ConsumerManagementRegistry consumerManagementRegistry;
  private final EntityMonitoringService entityMonitoringService;
  private final SharedManagementRegistry sharedManagementRegistry;

  PassiveNmsServerEntity(ConsumerManagementRegistry consumerManagementRegistry, EntityMonitoringService entityMonitoringService, SharedManagementRegistry sharedManagementRegistry) {
    this.consumerManagementRegistry = Objects.requireNonNull(consumerManagementRegistry);
    this.entityMonitoringService = Objects.requireNonNull(entityMonitoringService);
    this.sharedManagementRegistry = Objects.requireNonNull(sharedManagementRegistry);
  }

  // PassiveProxiedServerEntity
  
  @Override
  public void createNew() {
    super.createNew();
    LOGGER.trace("[{}] createNew()", entityMonitoringService.getConsumerId());
    consumerManagementRegistry.refresh();
  }

  // NmsCallback
  
  @Override
  public void entityCallbackToExecuteManagementCall(String managementCallIdentifier, ContextualCall<?> call) {
    String serverName = call.getContext().get(Server.NAME_KEY);
    if (serverName == null) {
      throw new IllegalArgumentException("Bad context: " + call.getContext());
    }
    if (entityMonitoringService.getServerName().equals(serverName)) {
      LOGGER.trace("[{}] entityCallbackToExecuteManagementCall({}, {}, {}, {})", entityMonitoringService.getConsumerId(), managementCallIdentifier, call.getContext(), call.getCapability(), call.getMethodName());
      ContextualReturn<?> contextualReturn = sharedManagementRegistry.withCapability(call.getCapability())
          .call(call.getMethodName(), call.getReturnType(), call.getParameters())
          .on(call.getContext())
          .build()
          .execute()
          .getSingleResult();
      entityMonitoringService.answerManagementCall(managementCallIdentifier, contextualReturn);
    }
  }

  @Override
  public void unSchedule() {
    throw new UnsupportedOperationException("Cannot be called on a passive server");
  }

  @Override
  public IEntityMessenger.ScheduledToken entityCallbackToSendMessagesToClients() {
    throw new UnsupportedOperationException("Cannot be called on a passive server");
  }

  // Nms
  
  @Override
  public Future<Cluster> readTopology() {
    throw new UnsupportedOperationException("Cannot be called on a passive server");
  }

  @Override
  public Future<String> call(@ClientId Object callerDescriptor, Context context, String capabilityName, String methodName, Class<?> returnType, Parameter... parameters) {
    throw new UnsupportedOperationException("Cannot be called on a passive server");
  }

}
