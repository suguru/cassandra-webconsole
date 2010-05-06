<#include "header.ftl">

<nav class="path">
<a href="../../">Top</a>
&gt;
<a href="./"><strong>${keyspaceName?html}</strong></a>
</nav>

<div class="section">

<h3>Control</h3>
<nav class="control">
<a class="formbox" title="Add Column Family" href="addcf">Add Column Family</a>
<a class="formbox" title="Rename Keyspace" href="rename">Rename Keyspace</a>
<a class="formbox" title="Drop Keyspace" href="drop">Drop Keyspace</a>
</nav>
</div>

<div class="section">
<h3>Column Families</h3>

<#list describeMap?keys as key>
<#assign map = describeMap[key]>
<table class="keyspace">
<caption><a href="./${key?html}/">${key?html}</a></caption>
<#list map?keys as key>
<tr>
<th>${key?html}</th>
<td>${map[key]?html}</td>
</tr>
</#list>
</table>
</#list>

</div>

<#if tokenRanges?exists>
<div class="section">
<h3>Token Range</h3>
<table>
<tr>
<th>Endpoints</th>
<th>Start Token</th>
<th>End Token</th>
</tr>
<#list tokenRanges as tokenRange>
<tr>
<td>
<#list tokenRange.endpoints as endpoint>
${endpoint?html}<#if endpoint_has_next>,</#if>
</#list>
</td>
<td>${tokenRange.start_token}</td>
<td>${tokenRange.end_token}</td>
</tr>
</#list>
</table>
</div>
</#if>

<#include "footer.ftl">