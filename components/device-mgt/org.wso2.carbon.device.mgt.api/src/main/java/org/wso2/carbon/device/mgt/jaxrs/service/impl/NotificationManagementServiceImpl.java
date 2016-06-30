/*
 *   Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.wso2.carbon.device.mgt.jaxrs.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.PaginationRequest;
import org.wso2.carbon.device.mgt.common.PaginationResult;
import org.wso2.carbon.device.mgt.common.notification.mgt.Notification;
import org.wso2.carbon.device.mgt.common.notification.mgt.NotificationManagementException;
import org.wso2.carbon.device.mgt.jaxrs.beans.ErrorResponse;
import org.wso2.carbon.device.mgt.jaxrs.beans.NotificationList;
import org.wso2.carbon.device.mgt.jaxrs.service.api.NotificationManagementService;
import org.wso2.carbon.device.mgt.jaxrs.service.impl.util.RequestValidationUtil;
import org.wso2.carbon.device.mgt.jaxrs.util.DeviceMgtAPIUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationManagementServiceImpl implements NotificationManagementService {

    private static final Log log = LogFactory.getLog(NotificationManagementServiceImpl.class);

    @GET
    @Override
    public Response getNotifications(
            @QueryParam("status") String status,
            @HeaderParam("If-Modified-Since") String ifModifiedSince,
            @QueryParam("offset") int offset, @QueryParam("limit") int limit) {

        PaginationRequest request = new PaginationRequest(offset, limit);
        PaginationResult result;

        NotificationList notificationList = new NotificationList();

        String msg;
        try {
            if (status != null) {
                RequestValidationUtil.validateNotificationStatus(status);
                result = DeviceMgtAPIUtils.getNotificationManagementService().getNotificationsByStatus(
                        Notification.Status.valueOf(status), request);
            } else {
                result = DeviceMgtAPIUtils.getNotificationManagementService().getAllNotifications(request);
            }
            notificationList.setCount(result.getRecordsTotal());
            notificationList.setNotifications((List<Notification>) result.getData());
            return Response.status(Response.Status.OK).entity(notificationList).build();
        } catch (NotificationManagementException e) {
            msg = "Error occurred while retrieving notification list";
            log.error(msg, e);
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(msg).build()).build();
        }
    }

}