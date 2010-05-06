<#include "header.ftl">

<nav class="path">
<a href="../../">Top</a>
&gt;
<a href="./"><strong>Ring</strong></a>
</nav>

<div class="section">
<h3>Ring</h3>

<table>
<tr>
	<th>Address</th>
	<th>Status</th>
	<th>Load</th>
	<th>Control</th>
</tr>
<#list nodes as node>
<tr>
	
	<td>${node.address?html}</td>
	<#if node.up>
	<td class="up status">UP</td>
	<#else>
	<td class="down status">DOWN</td>
	</#if>
	<td class="bytes">${node.load?html}</td>
	<td><a class="button">Statistics</a></td>
</tr>
</#list>
</table>

</div>


<#include "footer.ftl">