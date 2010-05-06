<#include "header.ftl">

<nav class="path">
<a href="../../../">Top</a>
&gt;
<a href="../../${keyspaceName?html}/">${keyspaceName?html}</a>
&gt;
<a href="./"><strong>${columnFamilyName?html}</strong></a>
</nav>

<div class="section">
<h3>Control</h3>
<nav class="control">
<a class="formbox" title="Rename Column Family" href="rename">Rename Column Family</a>
<a class="formbox" title="Drop Column Family" href="drop">Drop Column Family</a>
</nav>
</div>

<div class="section">
</div>

<#include "footer.ftl">