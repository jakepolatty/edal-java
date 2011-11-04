<%@page contentType="text/plain"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.atg.com/taglibs/json" prefix="json"%>
<%@taglib uri="/WEB-INF/taglib/wms/wmsUtils" prefix="utils"%> <%-- tag library for useful utility functions --%>
<%
response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
<%--
     Displays the details of a variable as a JSON object
     See MetadataController.showLayerDetails().
     
     Data (models) passed in to this page:
         layer = Layer object
         datesWithData = Map<Integer, Map<Integer, List<Integer>>>.  Contains
                information about which days contain data for the Layer.  Maps
                years to a map of months to an array of day numbers.
         nearestTimeIso = ISO8601 String representing the point along the time
                axis that is closest to the required date (as passed to the server)
--%>
<json:object>
    <json:property name="units" value="${units}"/>

    <c:set var="bbox" value="${utils:getWmsBoundingBox(feature)}"/>
    <json:array name="bbox">
        <json:property>${bbox.minX}</json:property>
        <json:property>${bbox.minY}</json:property>
        <json:property>${bbox.maxX}</json:property>
        <json:property>${bbox.maxY}</json:property>
    </json:array>

    <json:array name="scaleRange">
        <json:property>${featureMetadata.colorScaleRange.low}</json:property>
        <json:property>${featureMetadata.colorScaleRange.high}</json:property>
    </json:array>

    <json:property name="numColorBands" value="${featureMetadata.numColorBands}"/>

    <c:set var="styles" value="boxfill"/>
    <c:if test="${utils:isVectorLayer(feature.coverage)}">
        <c:set var="styles" value="vector,boxfill"/>
    </c:if>
    <json:array name="supportedStyles" items="${styles}"/>

    <c:if test="${not empty feature.coverage.domain.verticalAxis}">
        <json:object name="zaxis">
            <json:property name="units" value="${feature.coverage.domain.verticalAxis.verticalCrs.units.unitString}"/>
            <json:property name="positive" value="${feature.coverage.domain.verticalAxis.verticalCrs.positiveDirection}"/>
            <json:array name="values" items="${feature.coverage.domain.verticalAxis.coordinateValues}"/>
        </json:object>
    </c:if>

    <c:if test="${not empty feature.coverage.domain.timeAxis}">
        <json:object name="datesWithData">
            <c:forEach var="year" items="${datesWithData}">
                <json:object name="${year.key}">
                    <c:forEach var="month" items="${year.value}">
                        <json:array name="${month.key}" items="${month.value}"/>
                    </c:forEach>
                </json:object>
            </c:forEach>
        </json:object>
        <%-- The nearest time on the time axis to the time that's currently
             selected on the web interface, in ISO8601 format --%>
        <json:property name="nearestTimeIso" value="${nearestTimeIso}"/>
        <%-- The time axis units: "ISO8601" for "normal" axes, "360_day" for
             axes that use the 360-day calendar, etc. --%>
        <json:property name="timeAxisUnits" value="${utils:getTimeAxisUnits(feature.coverage.domain.timeAxis.calendarSystem)}"/>
    </c:if>
    
    <json:property name="moreInfo" value="${dataset.moreInfoUrl}"/>
    <json:property name="copyright" value="${dataset.copyrightStatement}"/>
    <json:array name="palettes" items="${paletteNames}"/>
    <json:property name="defaultPalette" value="${featureMetadata.paletteName}"/>
    <json:property name="logScaling" value="${featureMetadata.logScaling}"/>
</json:object>