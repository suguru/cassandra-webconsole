<#include "header.ftl">

<nav class="path">
<a href="../../">Top</a>
&gt;
<a href="./"><strong>${keyspace.name?html}</strong></a>
</nav>

<nav class="control">
<a class="formbox" title="Add Column Family" href="addcf">Add Column Family</a>
<a class="formbox" title="Rename Keyspace" href="rename">Rename Keyspace</a>
<a class="formbox" title="Drop Keyspace" href="drop">Drop Keyspace</a>
</nav>

<h2>Column Families</h2>

<#list keyspace.cf_defs as cf>
<table class="keyspace">
<caption><a href="./${cf.name?html}/">${cf.name?html}</a></caption>
<tr>
<th>Id</th>
<th>ColumnType</th>
<th>Comparator</th>
</tr>
<tr>
<td>${cf.id}</td>
<td>${cf.column_type}</td>
<td>${cf.comparator_type}</td>
</tr>
<tr>
<th colspan="3">Comment</th>
</tr>
<tr>
<td colspan="3">${cf.comment?html}</td>
</tr>
<#list cf.column_metadata as cmeta>
<tr>
<th>${cmeta.index_name?html}</th>
<td>${cmeta.index_type?html}</td>
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