<#include "header.ftl">

<nav class="path">
<a href="../../">Top</a>
&gt;
<a href="./"><strong>System</strong></a>
</nav>

<div class="section">
<h3>System Information</h3>

<dl>
	<dt>Cluster Name</dt>
	<dd>${clusterName?html}</dd>
	<dt>Thrift API Version</dt>
	<dd>${version?html}</dt>
</dl>

</div>

<#include "footer.ftl">