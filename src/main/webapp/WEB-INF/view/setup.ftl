<#setting number_format="#"/>
<form class="float" id="floatForm" method="post" action="${contextPath}/setup">

<p>
	<label for="host">Cassandra Host</label>
	<input class="required" type="text" id="host" name="host" value="<#if host?exists>${host?html}</#if>" />
</p>

<p>
	<label for="thriftPort">Thrift Port</label>
	<input class="required number" type="text" id="thriftPort" name="thriftPort" value="<#if thriftPort?exists>${thriftPort?html}</#if>" />
</p>

<p>
	<label for="jmxPort">JMX Port</label>
	<input class="required number" type="text" id="jmxPort" name="jmxPort" value="<#if jmxPort?exists>${jmxPort?html}</#if>" />
</p>

<p>
	<label for="framedTransport">Framed Transport</label>
	<input type="checkbox" id="framedTransport" name="framedTransport" <#if framedTransport?exists && framedTransport == "true">checked="true"</#if> value="true" />
</p>

<p class="buttons">
	<button type="submit" class="submit">Save</button>
</p>

</form>

<script type="text/javascript">
$("#floatForm").validate({
	invalidHandler: function(form,validator) {
		$.fn.colorbox.resize();
	}
});
</script>
