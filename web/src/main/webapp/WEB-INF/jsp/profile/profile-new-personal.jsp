<%@ include file="/WEB-INF/jsp/tiles/common/taglib.jsp" %>
<%@ page session="false" contentType="text/html; charset=UTF-8" %>

<tiles:insertDefinition name="tiles-default" flush="true">
<tiles:putAttribute name="id" type="string">dbs-page-profile-new</tiles:putAttribute>
<tiles:putAttribute name="title" type="string">nowy profil</tiles:putAttribute>
<tiles:putAttribute name="css" type="string">
<link rel="stylesheet" href="css/compiled/new-user.css" type="text/css" media="screen" />
<link rel="stylesheet" href="css/compiled/form-wizard.css" type="text/css" media="screen" />
<link rel="stylesheet" href="css/dbs/dbs-profile-new.css" type="text/css" media="screen" />
</tiles:putAttribute>
<tiles:putAttribute name="content" type="string">

        <div id="pad-wrapper" class="new-user">
            <div class="row header">
                <div class="col-md-12">
                    <h3>Dodaj profil użytkownika</h3>
                </div>                
            </div>
            
            <div class="row form-wrapper">
                <!-- left column -->
                <div class="col-md-9 with-sidebar">
                
                    <div id="fuelux-wizard" class="wizard row">
                        <ul class="wizard-steps">
                            <li data-target="#step1" class="active">
                                <span class="step">1</span>
                                <span class="title">Wprowadź dane profilu</span>
                            </li>
                            <li data-target="#step2">
                                <span class="step">2</span>
                                <span class="title">Zapisz profil</span>
                            </li>
                        </ul>                            
                    </div>                 
                
                    <div class="step-content">
                    <div class="step-pane active" id="step1">
                    <div class="row form-wrapper">
                    <div class="col-md-12">                  

                    	<form:form method="post" modelAttribute="profileNewForm" action="profile/new/form" class="new_user_form" enctype="multipart/form-data">
							<input type="hidden" name="page" value="1"/>
							
							
                    		<spring:bind path="login">
                    		<c:set var="classes"><c:choose><c:when test="${status.error}">error</c:when></c:choose></c:set>
                            <div class="col-md-12 field-box ${classes}">
                                <label>Login:</label>
                                <form:input path="login" cssClass="form-control" />
                                <c:if test="${status.error}"><span class="alert-msg"><i class="icon-remove-sign"></i> <c:out value="${status.errorMessage}" escapeXml="false"/></span></c:if>
                            </div>
                            </spring:bind>
                            
                    		<spring:bind path="passwd">
                    		<c:set var="classes"><c:choose><c:when test="${status.error}">error</c:when></c:choose></c:set>
                            <div class="col-md-12 field-box ${classes}">
                                <label>Hasło:</label>
                                <input name="passwd" class="form-control" type="password" />
                                <c:if test="${status.error}"><span class="alert-msg"><i class="icon-remove-sign"></i> <c:out value="${status.errorMessage}" escapeXml="false"/></span></c:if>
                            </div>
                            </spring:bind>                            
                    	
                    		<spring:bind path="firstName">
                    		<c:set var="classes"><c:choose><c:when test="${status.error}">error</c:when></c:choose></c:set>
                            <div class="col-md-12 field-box ${classes}">
                                <label>Imię:</label>
                                <form:input path="firstName" cssClass="form-control" />
                                <c:if test="${status.error}"><span class="alert-msg"><i class="icon-remove-sign"></i> <c:out value="${status.errorMessage}" escapeXml="false"/></span></c:if>
                            </div>
                            </spring:bind>
                            
                    		<spring:bind path="lastName">
                    		<c:set var="classes"><c:choose><c:when test="${status.error}">error</c:when></c:choose></c:set>
                            <div class="col-md-12 field-box ${classes}">
                                <label>Nazwisko:</label>
                                <form:input path="lastName" cssClass="form-control" />
                                <c:if test="${status.error}"><span class="alert-msg"><i class="icon-remove-sign"></i> <c:out value="${status.errorMessage}" escapeXml="false"/></span></c:if>
                            </div>
                            </spring:bind>
                            
                    		<spring:bind path="email">
                            <c:set var="classes"><c:choose><c:when test="${status.error}">error</c:when></c:choose></c:set>
                    		<c:if test="${status.error}"><c:set var="classes">error</c:set></c:if>
                            <div class="col-md-12 field-box ${classes}">
                                <label>Email:</label>
                                <form:input path="email" cssClass="form-control" />
                                <c:if test="${status.error}"><span class="alert-msg"><i class="icon-remove-sign"></i> <c:out value="${status.errorMessage}" escapeXml="false"/></span></c:if>
                            </div>
                            </spring:bind>        
                            
                    		<spring:bind path="phone">
                    		<c:set var="classes"><c:choose><c:when test="${status.error}">error</c:when></c:choose></c:set>
                            <div class="col-md-12 field-box ${classes}">
                                <label>Telefon:</label>
                                <form:input path="phone" cssClass="form-control" />
                                <c:if test="${status.error}"><span class="alert-msg"><i class="icon-remove-sign"></i> <c:out value="${status.errorMessage}" escapeXml="false"/></span></c:if>
                            </div>
                            </spring:bind>                                                  
                            

                            <div class="col-md-12 field-box">
                                <label>Adres:</label>
                                <div class="address-fields">
		                    		<spring:bind path="street">
		                    		<c:set var="classes"><c:choose><c:when test="${status.error}">error</c:when></c:choose></c:set>
		                    		<div class="field-box ${classes}">
		                                <form:input path="street" cssClass="form-control" placeholder="Ulica"/>
		                                <c:if test="${status.error}"><span class="alert-msg"><i class="icon-remove-sign"></i> <c:out value="${status.errorMessage}" escapeXml="false"/></span></c:if>
		                            </div>
		                            </spring:bind>     
		                    		<spring:bind path="city">
		                    		<c:set var="classes"><c:choose><c:when test="${status.error}">error</c:when></c:choose></c:set>
		                    		<div class="field-box ${classes}">
		                                <form:input path="city" cssClass="small form-control" placeholder="Miasto"/>
		                                <c:if test="${status.error}"><span class="alert-msg"><i class="icon-remove-sign"></i> <c:out value="${status.errorMessage}" escapeXml="false"/></span></c:if>
		                            </div>
		                            </spring:bind>		                            
		                    		<spring:bind path="state">
		                    		<c:set var="classes"><c:choose><c:when test="${status.error}">error</c:when></c:choose></c:set>
		                    		<div class="field-box ${classes}">
		                                <form:input path="state" cssClass="small form-control" placeholder="Województwo"/>
		                                <c:if test="${status.error}"><span class="alert-msg"><i class="icon-remove-sign"></i> <c:out value="${status.errorMessage}" escapeXml="false"/></span></c:if>
		                            </div>
		                            </spring:bind>		                            
		                    		<spring:bind path="zipCode">
		                    		<c:set var="classes"><c:choose><c:when test="${status.error}">error</c:when></c:choose></c:set>
		                    		<div class="field-box ${classes}">
		                                <form:input path="zipCode" cssClass="small form-control" placeholder="Kod pocztowy"/>
		                                <c:if test="${status.error}"><span class="alert-msg"><i class="icon-remove-sign"></i> <c:out value="${status.errorMessage}" escapeXml="false"/></span></c:if>
		                            </div>
		                            </spring:bind>		                                                         
                                </div>
                            </div>
                            
                            <div class="col-md-12 field-box textarea personal-image">
                    		<spring:bind path="file">
                    		<c:set var="classes"><c:choose><c:when test="${status.error}">error</c:when></c:choose></c:set>
                            <div class="col-md-12 field-box ${classes}">
	                        	<label>Zdjęcie:</label>
	                        	<input type="file" name="file" class="small form-control" accept=".jpg,.jpeg,.gif"/>
                                <c:if test="${status.error}"><span class="alert-msg"><i class="icon-remove-sign"></i> <c:out value="${status.errorMessage}" escapeXml="false"/></span></c:if>
                            </div>
                            </spring:bind>                            
                            </div>
                            
                            <div class="col-md-11 field-box actions">
                            	<button type="button" class="btn-glow" onclick="location.href='profile/new'"><i class="icon-refresh"></i>&nbsp;Wyczyść</button><span>&nbsp;</span>
		                        <button type="submit" class="btn-glow primary btn-next" data-last="Finish">Dalej&nbsp;&nbsp;<i class="icon-chevron-right"></i></button><span>&nbsp;</span>
		                        <!-- button type="button" class="btn-glow success btn-finish">
		                            Zapisz
		                        </button-->                            
                            </div>
                        </form:form>
                    </div>
                    </div>
                    </div>
                    </div>
                    
                </div>

                <!-- side right column -->
                <div class="col-md-3 form-sidebar pull-right">
                    <h6>Sidebar text for instructions</h6>
                    <p>Add multiple users at once</p>
                    <p>Choose one of the following file types:</p>
                    <ul>
                        <li><a href="#">Upload a vCard file</a></li>
                        <li><a href="#">Import from a CSV file</a></li>
                        <li><a href="#">Import from an Excel file</a></li>
                    </ul>
                </div>
            </div>
        </div>
        
</tiles:putAttribute>
</tiles:insertDefinition>        