<#include "header.ftl">

<nav class="path">
<a href="../../../">Top</a>
&gt;
<a href="../../${keyspaceName?html}/">${keyspaceName?html}</a>
&gt;
<a href="./"><strong>${columnFamilyName?html}</strong></a>
</nav>

<nav class="control">
<a class="formbox" title="Rename Column Family" href="rename">Rename Column Family</a>
<a class="formbox" title="Drop Column Family" href="drop">Drop Column Family</a>
</nav>

<#if system?exists>
<h2>Browsing not available</h2>
<#else>
<h2>Data</h2>
<div id="browse">
</div>

<script type="text/javascript">
$(document).ready(function() {
	$("#browse").load("browse");
});
</script>
</#if>

<#include "footer.ftl">