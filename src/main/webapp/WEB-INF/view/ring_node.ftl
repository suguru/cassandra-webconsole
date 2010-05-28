<#include "header.ftl">

<script type="text/javascript">
function checkAll(target)
{
	var boxes = target.find("input[type=checkbox]");
	if (boxes.attr("checked"))
	{
		boxes.attr("checked", false);
	}
	else
	{
		boxes.attr("checked", true);
	}
}
function control(target, keyspace, method)
{
	var cf = "";
	target.find("input[type=checkbox]").each(function() {
		if ($(this).attr("checked"))
		{
			if (cf != "") cf = cf + ",";
			cf = cf + $(this).val();
		}
	});
	$.fn.colorbox({
		href: keyspace + '/' + cf + '/' + method,
		title: method + ' column families',
		
	});
}
</script>

<nav class="path">
<a href="../../">Top</a>
&gt;
<a href="../">Ring</a>
&gt;
<a href="./"><strong>${address?html}</strong></a>
</nav>

<nav class="control">
<a class="formbox" title="Load Balance" href="loadbalance">Load Balance</a>
<a class="formbox" title="Cleanup" href="cleanup">Cleanup</a>
<a class="formbox" title="Compact" href="compact">Compact</a>
<a class="formbox" title="Move" href="move">Move</a>
<a class="formbox" title="Decomission" href="decomission">Decomission</a>
<a class="formbox" title="Drain" href="drain">Drain</a>
</nav>

<h2>Node Information for ${address}</h2>

<table>
<caption>Node Stats</caption>
<tr>
	<th>Address</th>
	<th>Token</th>
	<th>Mode</th>
	<th>Uptime</th>
	<th>Generation Number</th>
	<th>Compaction Threshold</th>
</tr>
<tr>
	<td>${address?html}</td>
	<td>${token?html}</td>
	<td>${mode?html}</td>
	<td>${uptime?html}</td>
	<td>${currentGenerationNumber}</td>
	<td>${compactionThreshold}</td>
</tr>
</table>

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

<h2>Column Family Store</h2>
<#list cfparent?keys as key>
<#assign cfmap = cfparent[key]/>
<div id="cf${key?html}" class="nodeCfStore">
<table>
<caption>${key?html}</caption>
<tr>
	<th rowspan="2">Column Family</th>
	<th rowspan="2">Disk</th>
	<th rowspan="2">SSTable</th>
	<th rowspan="2">Read</th>
	<th rowspan="2">Write</th>
	<th colspan="3">Memtable</th>
	<th colspan="3">Compacted</th>
	<th rowspan="2">Pending</th>
	<th rowspan="2">Latency Histogram</th>
</tr>
<tr>
	<th>Data</th>
	<th>Column</th>
	<th>Switch</th>
	<th>Max</th>
	<th>Min</th>
	<th>Mean</th>
</tr>
<#list cfmap?keys as cfname>
<#assign cf = cfmap[cfname]/>
<tr>
	<td><input type="checkbox" name="target" id="${key?html}${cfname?html}" value="${cfname?html}" /> <label for="${key?html}${cfname?html}">${cfname?html}</label></td>
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
	<td>
		R:<span class="inlinespark"><#list cf.lifetimeReadLatencyHistogramMicros as v>${v}<#if v_has_next>,</#if></#list></span>
		W:<span class="inlinespark"><#list cf.lifetimeWriteLatencyHistogramMicros as v>${v}<#if v_has_next>,</#if></#list></span>
	</td>
</tr>
</#list>
</table>
<nav>
<button type="button" onclick="checkAll($('#cf${key?html}'))">Check all</button>
<button type="button" onclick="control($('#cf${key?html}'), '${key?html}', 'flush')">Flush</button>
<button type="button" onclick="control($('#cf${key?html}'), '${key?html}', 'repair')">Repair</button>
</nav>
</div>

</#list>

<script type="text/javascript">
$("span.inlinespark").sparkline('html', {type:"bar",barWidth:2,barSpacing:0});
</script>
