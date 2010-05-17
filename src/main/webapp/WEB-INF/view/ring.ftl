<#include "header.ftl">

<nav class="path">
<a href="../">Top</a>
&gt;
<a href="./"><strong>Ring</strong></a>
</nav>

<table>
<caption>Ring</caption>
<tr>
	<th>Address</th>
	<th>Token</th>
	<th>Mode</th>
	<th>Status</th>
	<th>Load</th>
	<th>Heap</th>
	<th>Uptime</th>
</tr>
<#list nodes as node>
<tr>
	
	<td>
	<#if node.jmx>
	<a href="${node.address?html}/">${node.address?html}</a>
	<#else>
	${node.address?html}
	</#if>
	</td>
	<td>${node.token?html}</td>
	<td>${node.operationMode?html}</td>
	<#if node.up == "UP">
	<td class="status up">UP</td>
	<#elseif node.up == "DOWN">
	<td class="status down">DOWN</td>
	<#else>
	<td class="status">?</td>
	</#if>
	<td class="bytes">${node.load?html}</td>
	<td class="bytes">${node.memoryUsed} / ${node.memoryMax}</td>
	<td class="date">${node.uptime}</td>
</tr>
</#list>
</table>

<#include "footer.ftl">