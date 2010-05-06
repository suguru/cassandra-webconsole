<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>Cassandra Web Console</title>
<script type="text/javascript" src="${contextPath}/js/jquery.js"></script>
<script type="text/javascript" src="${contextPath}/js/jquery.colorbox.js"></script>
<script type="text/javascript" src="${contextPath}/js/jquery.validation.js"></script>
<script type="text/javascript" src="${contextPath}/js/webconsole.js"></script>
<link rel="stylesheet" href="${contextPath}/css/base.css" type="text/css" />
<link rel="stylesheet" href="${contextPath}/css/colorbox.css" type="text/css" />
</head>
<body>

<header>
<h1>Cassandra Web Console<#if title?exists> - ${title?html}</#if></h1>
</header>

<div id="container">

<div id="menu">
<h2>Keyspaces</h2>
<ul>
<#list keyspaces as keyspace>
<#if keyspaceName?exists && keyspaceName == keyspace>
<li class ="active"><a href="${contextPath}/keyspace/${keyspace?html}/">${keyspace?html}</a></li>
<#if columnFamilies?exists>
<#list columnFamilies as columnFamily>
<#if columnFamilyName?exists && columnFamilyName == columnFamily>
<li class="cf active<#if !columnFamily_has_next> last</#if>"><a href="${contextPath}/keyspace/${keyspace?html}/${columnFamily?html}/">${columnFamily?html}</a></li>
<#else>
<li class="cf<#if !columnFamily_has_next> last</#if>"><a href="${contextPath}/keyspace/${keyspace?html}/${columnFamily?html}/">${columnFamily?html}</a></li>
</#if>
</#list>
</#if>
<#else>
<li><a href="${contextPath}/keyspace/${keyspace?html}/">${keyspace?html}</a></li>
</#if>
</#list>
</ul>
<h2>Administration</h2>
<ul>
<li><a href="${contextPath}/keyspace/create" title="Create a Keyspace" class="formbox">Create a keyspace</a></li>
</ul>
<h2>Information</h2>
<ul>
<li <#if menu_info?exists>class="active"</#if>><a href="${contextPath}/info">System</a></li>
<li <#if menu_ring?exists>class="active"</#if>><a href="${contextPath}/ring">Ring</a></li>
</ul>
<h2>Other</h2>
<ul>
<li><a href="${contextPath}/setup" title="Setup Configuration" class="formbox">Configuration</a></li>
</ul>
</div>

<div id="content">
