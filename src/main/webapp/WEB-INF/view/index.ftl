<#include "header.ftl">

<#if setupNeeded?exists>
<script type="text/javascript">
$(document).ready(function() {
	$.fn.colorbox({
		title: "Setup configuration",
		href: "setup"
	});
});
</script>
</#if>

<#include "footer.ftl">