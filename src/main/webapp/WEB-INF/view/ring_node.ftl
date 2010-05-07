<#include "header.ftl">

<nav class="path">
<a href="../../">Top</a>
&gt;
<a href="../">Ring</a>
&gt;
<a href="./"><strong>${address?html}</strong></a>
</nav>

<h2>Node Information for ${address}</h2>

<table>
<caption>Thread Pools</caption>
<tr>
	<th>Pool Name</th>
	<th>Active</th>
	<th>Pending</th>
	<th>Completed</th>
</tr>
<#list tpmap?keys as key>
<#assign tp = tpmap[key]/>
<tr>
	<td>${key?html}</td>
	<td class="number">${tp.activeCount}</td>
	<td class="number">${tp.pendingTasks}</td>
	<td class="number">${tp.completedTasks}</td>
</tr>
</#list>
</table>

<table>
<caption>Column Family Store</caption>
<tr>
	<th rowspan="2">Column Family</th>
	<th rowspan="2">Disk</th>
	<th rowspan="2">SSTable</th>
	<th rowspan="2">Read</th>
	<th rowspan="2">Write</th>
	<th colspan="3">Memtable</th>
	<th colspan="3">Compacted</th>
	<th rowspan="2">Pending</th>
</tr>
<tr>
	<th>Data</th>
	<th>Column</th>
	<th>Switch</th>
	<th>Max</th>
	<th>Min</th>
	<th>Mean</th>
</tr>
<#list cfmap?keys as key>
<#assign cf = cfmap[key]/>
<tr>
	<td>${key?html}</td>
	<td class="bytes">${cf.liveDiskSpaceUsed / 1024 / 1024} MB</td>
	<td class="number">${cf.liveSSTableCount}</td>
	<td class="number">${cf.readCount}</td>
	<td class="number">${cf.writeCount}</td>
	<td class="number">${cf.memtableDataSize / 1024 / 1024} MB</td>
	<td class="number">${cf.memtableColumnsCount}</td>
	<td class="number">${cf.memtableSwitchCount}</td>
	<td class="bytes">${cf.maxRowCompactedSize / 1024 / 1024} MB</td>
	<td class="bytes">${cf.minRowCompactedSize / 1024 / 1024} MB</td>
	<td class="bytes">${cf.meanRowCompactedSize / 1024 / 1024} MB</td>
	<td class="number">${cf.pendingTasks}</td>
</tr>
</#list>
</table>
</div>
