<#include "header.ftl">

<nav class="path">
<a href="../../">Top</a>
&gt;
<a href="./"><strong>${keyspaceName?html}</strong></a>
</nav>

<nav class="control">
<a class="formbox" title="Add Column Family" href="addcf">Add Column Family</a>
<a class="formbox" title="Rename Keyspace" href="rename">Rename Keyspace</a>
<a class="formbox" title="Drop Keyspace" href="drop">Drop Keyspace</a>
</nav>

<h2>Column Families</h2>

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

<h2>Token Range</h2>
<#if tokenRanges?exists>
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
</#if>

<#include "footer.ftl">