<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>Cassandra Web Console</title>
<script type="text/javascript" src="${contextPath}/js/jquery.js"></script>
<script type="text/javascript" src="${contextPath}/js/jquery.colorbox.js"></script>
<script type="text/javascript" src="${contextPath}/js/jquery.validation.js"></script>
<link rel="stylesheet" href="${contextPath}/css/base.css" type="text/css" />
<link rel="stylesheet" href="${contextPath}/css/colorbox.css" type="text/css" />
<script type="text/javascript">
$(function() {
	$("a.formbox").colorbox();
	$("#keyspaces").load(
		"${contextPath}/keyspaces",
		{keyspace:'${keyspaceName?default("")?html}', columnFamily:'${columnFamilyName?default("")?html}'}
	);
});
</script>
</head>
<body>

<header>
<h1>Cassandra Web Console<#if title?exists> - ${title?html}</#if></h1>
</header>

<div id="container">

<div id="menu">

<h2>Keyspaces</h2>
<ul id="keyspaces">
</ul>

<h2>Administration</h2>
<ul>
<li><a href="${contextPath}/keyspace/create" title="Create keyspace" class="formbox">Create keyspace</a></li>
<li><a href="${contextPath}/token/remove" title="Remove token" class="formbox">Remove token</a></li>
</ul>
<h2>Information</h2>
<ul>
<li <#if menu_info?exists>class="active"</#if>><a href="${contextPath}/info/">System</a></li>
<li <#if menu_ring?exists>class="active"</#if>><a href="${contextPath}/ring/">Ring</a></li>
<#if address?exists>
<li class="cf active last"><a href="${contextPath}/ring/${address?html}">${address?html}</a></li>
</#if>
</ul>
<h2>Other</h2>
<ul>
<li><a href="${contextPath}/setup" title="Setup Configuration" class="formbox">Configuration</a></li>
</ul>
</div>

<div id="content">
