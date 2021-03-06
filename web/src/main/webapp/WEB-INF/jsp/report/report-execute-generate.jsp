<%@ include file="/WEB-INF/jsp/tiles/common/taglib.jsp" %>
<%@ page session="false" contentType="text/html; charset=UTF-8" %>

<tiles:insertDefinition name="tiles-wizard" flush="true">
<tiles:putAttribute name="id" type="string">dbs-page-report-execute-generate</tiles:putAttribute>
<tiles:putAttribute name="title" type="string">Generowanie raportu</tiles:putAttribute>
<tiles:putAttribute name="steps" type="string">Ustaw parametry raportu;Wygeneruj raport</tiles:putAttribute>
<tiles:putAttribute name="step" type="string">2</tiles:putAttribute>
<tiles:putAttribute name="css" type="string">
<link rel="stylesheet" href="css/compiled/new-user.css" type="text/css" media="screen" />
<link rel="stylesheet" href="css/compiled/form-wizard.css" type="text/css" media="screen" />
<link rel="stylesheet" type="text/css" href="css/bootstrap/bootstrap-select.min.css">
<link rel="stylesheet" href="css/dbs/dbs-wizard.css" type="text/css" media="screen" />
</tiles:putAttribute>

<tiles:putAttribute name="js" type="string">
<script type="text/javascript" src="js/bootstrap-select.min.js"></script>
<script src="js/dbs/dbs-multiselect.js"></script>
</tiles:putAttribute>

<tiles:putAttribute name="content" type="string">

                    	<form:form method="post" modelAttribute="reportGenerationForm" action="report/execute/generate" class="dbs-form"  enctype="multipart/form-data">
                    		<input type="hidden" name="page" value="2">
                    		
                            <div class="field-box">
                            	<label>Definicja:</label>
                            	<input class="form-control inline-input" type="text" readonly="readonly" value="${pattern.name} ${pattern.version}"/>
                            </div>
                            
                    	 	<div class="field-box">
                            	<label>Format:</label>
	                            	<input class="form-control inline-input" type="text" readonly="readonly" value="${reportGenerationForm.format.reportExtension}"/>
                        	</div>                        	

                    	 	<div class="field-box">
                            	<label>Nazwa:</label>
	                            	<input class="form-control inline-input" type="text" readonly="readonly" value="${reportGenerationForm.fullname}"/>
                        	</div> 
                    		
                            <c:if test="${reportGenerationForm.fieldfull}">
                            <c:forEach var="field" items="${reportGenerationForm.fields}" varStatus="fstatus">
								<tiles:insertDefinition name="${field.tile}">
                                <c:set var="field" value="${field}" scope="request" />
                                <c:set var="fieldname" value="fields[${fstatus.index}]" scope="request" />
                            	<tiles:putAttribute name="inputclass" type="string">form-control inline-input</tiles:putAttribute>
                            	<tiles:putAttribute name="attributes" type="string">readonly="readonly"</tiles:putAttribute>
                            	<tiles:putAttribute name="disabled" type="string">disabled</tiles:putAttribute>
                            	<tiles:putAttribute name="tooltips" type="string">false</tiles:putAttribute>
                            	</tiles:insertDefinition>
                            </c:forEach>
                            </c:if>                     		

                    		
                            <div class="wizard-actions">
                            	<button type="button" class="btn-glow primary btn-prev" onclick="location.href='report/execute/form'"><i class="icon-chevron-left"></i>&nbsp;Popraw</button><span>&nbsp;</span>
		                        <button type="submit" class="btn-glow success btn-finish" style="display: inline-block;"><i class="icon-cogs"></i>&nbsp;Generuj</button><span>&nbsp;</span>
                            </div>
                        </form:form>
        
</tiles:putAttribute>
</tiles:insertDefinition>        