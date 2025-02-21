/*
 *
 *  Copyright (c) 2025, WSO2 LLC. (https://www.wso2.com).
 *
 *  * WSO2 LLC. licenses this file to you under the Apache License,
 *  * Version 2.0 (the "License"); you may not use this file except
 *  * in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.wso2.carbon.si.metrics.icp.reporter.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.wso2.carbon.analytics.msf4j.interceptor.common.JWTAuthenticationInterceptor;
import org.wso2.carbon.si.metrics.icp.reporter.utiils.Utils;
import org.wso2.carbon.si.metrics.prometheus.reporter.util.HttpUtils;
import org.wso2.msf4j.Microservice;
import org.wso2.msf4j.Request;
import org.wso2.msf4j.interceptor.annotation.RequestInterceptor;
import org.wso2.msf4j.internal.mime.MimeMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import javax.activation.DataHandler;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component(
        name = "si-icp-services",
        service = Microservice.class,
        immediate = true
)
@Path("/management")
@RequestInterceptor(JWTAuthenticationInterceptor.class)
public class ICPReporterService implements Microservice {
//    private final MetricRegistry metricRegistry;

//    public ICPReporterService(MetricRegistry metricRegistry) {
//        this.metricRegistry = metricRegistry;
//    }

    public ICPReporterService() {
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response serverInfo(@Context Request request) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("carbonHome", System.getProperty("carbon.home"));
        jsonObject.put("javaHome", System.getProperty("java.home"));
        jsonObject.put("javaVersion", System.getProperty("java.version"));
        jsonObject.put("javaVendor", System.getProperty("java.vendor"));
        jsonObject.put("osName", System.getProperty("os.name"));
        jsonObject.put("osVersion", System.getProperty("os.version"));
        jsonObject.put("productName", "WSO2 Streaming Integrator");
        jsonObject.put("productVersion", "4.2.0");
        return Response.ok().entity(jsonObject).build();
    }

    @GET
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Context Request request) {
        return Response.ok().entity(new AuthToken((String)request.getProperty("Access_Token"))).build();
    }

    @GET
    @Path("/siddhi-applications")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSiddhiApplications(@Context Request request, @QueryParam("siddhiApp") String appName) {
        if (appName != null && !appName.isEmpty()) {
            CloseableHttpResponse artifactDetails = HttpUtils.doGet((String)request.getProperty("Access_Token"), "https://localhost:9442/siddhi-apps/" + appName);
            JsonObject jsonResponse = HttpUtils.getJsonResponse(artifactDetails);
            JsonObject object = new JsonObject();
            object.addProperty("configuration", jsonResponse.get("content").getAsString());
            return Response.ok(object).build();
        }
        CloseableHttpResponse artifactDetails = HttpUtils.doGet((String)request.getProperty("Access_Token"), "https://localhost:9442/siddhi-apps/statistics");
        JsonArray jsonResponse = HttpUtils.getJsonArray(artifactDetails);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("list", Converter.convertSiddhiAppDetails(jsonResponse));
        return Response.ok(responseMap).build();
    }

    @POST
    @Path("/siddhi-applications")
    @Produces(MediaType.APPLICATION_JSON)
    public Response patchSiddhiApplication(@Context Request request, @ApiParam(value = "Siddhi Application", required = true) JsonObject body) {
        String appName = body.get("name").getAsString();
        boolean shouldActivate = body.get("activate").getAsBoolean();
        CloseableHttpResponse response;

        if (shouldActivate) {
            response = HttpUtils.doPut((String)request.getProperty("Access_Token"), "https://localhost:9442/siddhi-apps/activate/" + appName);
        } else {
            response = HttpUtils.doPut((String)request.getProperty("Access_Token"), "https://localhost:9442/siddhi-apps/deactivate/" + appName);
        }
        if (response.getStatusLine().getStatusCode() == 200) {
            return Response.ok().build();
        }
        return Response.serverError().build();
    }

    @GET
    @Path("/sources")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSource(@Context Request request) {
        CloseableHttpResponse artifactDetails = HttpUtils.doGet((String)request.getProperty("Access_Token"), "https://localhost:9442/siddhi-apps/sources");
        JsonArray jsonResponse = HttpUtils.getJsonArray(artifactDetails);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("list", Converter.convertSourceDetails(jsonResponse));
        return Response.ok(responseMap).build();
    }

    @GET
    @Path("/sinks")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSink(@Context Request request) {
        CloseableHttpResponse artifactDetails = HttpUtils.doGet((String)request.getProperty("Access_Token"), "https://localhost:9442/siddhi-apps/sinks");
        JsonArray jsonResponse = HttpUtils.getJsonArray(artifactDetails);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("list", Converter.convertSinkDetails(jsonResponse));
        return Response.ok(responseMap).build();
    }

    @GET
    @Path("/queries")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuery(@Context Request request) {
        CloseableHttpResponse artifactDetails = HttpUtils.doGet((String)request.getProperty("Access_Token"), "https://localhost:9442/siddhi-apps/queries");
        JsonArray jsonResponse = HttpUtils.getJsonArray(artifactDetails);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("list", Converter.convertQueryDetails(jsonResponse));
        return Response.ok(responseMap).build();
    }

    @GET
    @Path("/stores")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStores(@Context Request request) {
        CloseableHttpResponse artifactDetails = HttpUtils.doGet((String)request.getProperty("Access_Token"), "https://localhost:9442/siddhi-apps/tables");
        JsonArray jsonResponse = HttpUtils.getJsonArray(artifactDetails);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("list", Converter.convertTablesDetails(jsonResponse));
        return Response.ok(responseMap).build();
    }

    @GET
    @Path("/triggers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTriggers(@Context Request request) {
        CloseableHttpResponse artifactDetails = HttpUtils.doGet((String)request.getProperty("Access_Token"), "https://localhost:9442/siddhi-apps/aggregations");
        JsonArray jsonResponse = HttpUtils.getJsonArray(artifactDetails);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("list", Converter.convertAggregationsDetails(jsonResponse));
        return Response.ok(responseMap).build();
    }

    @GET
    @Path("/windows")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWindow(@Context Request request) {
        CloseableHttpResponse artifactDetails = HttpUtils.doGet((String)request.getProperty("Access_Token"), "https://localhost:9442/siddhi-apps/windows");
        JsonArray jsonResponse = HttpUtils.getJsonArray(artifactDetails);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("list", Converter.convertWindowsDetails(jsonResponse));
        return Response.ok(responseMap).build();
    }

    @GET
    @Path("/aggregations")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAggregation(@Context Request request) {
        CloseableHttpResponse artifactDetails = HttpUtils.doGet((String)request.getProperty("Access_Token"), "https://localhost:9442/siddhi-apps/aggregations");
        JsonArray jsonResponse = HttpUtils.getJsonArray(artifactDetails);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("list", Converter.convertAggregationsDetails(jsonResponse));
        return Response.ok(responseMap).build();
    }

    @GET
    @Path("/logs")
//    @Produces(MediaType.APPLICATION_JSON)
    public Response getLogs(@Context Request request, @QueryParam("file") String fileName)
            throws IOException {
        if (fileName != null) {
//            DataHandler dataHandler = Utils.getLogFiles(fileName);
////        if (dataHandler != null) {
////            InputStream fileInput = dataHandler.getInputStream();
////            StreamingOnRequestDataSource ds = new StreamingOnRequestDataSource(fileInput);
////        }
////        SOAPFactory factory = OMAbstractFactory.getSOAP12Factory();
////        SOAPEnvelope env = factory.getDefaultEnvelope();
////        OMNamespace ns =
////                factory.createOMNamespace(RelayConstants.BINARY_CONTENT_QNAME.getNamespaceURI(), "ns");
////        OMElement omEle = factory.createOMElement(RelayConstants.BINARY_CONTENT_QNAME.getLocalPart(), ns);
////
////        dataHandler = new DataHandler(ds);
//            return Response.ok(dataHandler).build();
            String carbonHome = System.getProperty("carbon.home");
            if (carbonHome == null) {
                return null;
            }

//            String mimeType = MimeMapper.getMimeType(FilenameUtils.getExtension(fileName));
            File file = Paths.get(carbonHome, "wso2", "server", "logs", fileName).toFile();
            return Response.ok(Files.newInputStream(file.toPath())).type(MediaType.TEXT_PLAIN).build();
        }
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("list", Utils.getLogFileList());
        return Response.ok(responseMap).build();
    }

//    @GET
//    @Path("/logs")
//    @Produces(MediaType.APPLICATION_OCTET_STREAM)
//    public Response getLogFile(@Context Request request, @QueryParam("file") String fileName) throws IOException {
//        DataHandler dataHandler = Utils.getLogFiles(fileName);
////        if (dataHandler != null) {
////            InputStream fileInput = dataHandler.getInputStream();
////            StreamingOnRequestDataSource ds = new StreamingOnRequestDataSource(fileInput);
////        }
////        SOAPFactory factory = OMAbstractFactory.getSOAP12Factory();
////        SOAPEnvelope env = factory.getDefaultEnvelope();
////        OMNamespace ns =
////                factory.createOMNamespace(RelayConstants.BINARY_CONTENT_QNAME.getNamespaceURI(), "ns");
////        OMElement omEle = factory.createOMElement(RelayConstants.BINARY_CONTENT_QNAME.getLocalPart(), ns);
////
////        dataHandler = new DataHandler(ds);
//        return Response.ok(dataHandler).build();
//    }

    private class AuthToken {
        public  String AccessToken;

        public AuthToken(String accessToken) {
            AccessToken = accessToken;
        }
    }

}
