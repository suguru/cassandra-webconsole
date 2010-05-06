<#setting number_format="#"/>
<form class="float" id="floatForm" method="post" action="${contextPath}/setup">

<p>
	<label for="host">Cassandra Host</label>
	<input class="required" type="text" id="host" name="host" value="${host?html}" />
</p>

<p>
	<label for="thriftPort">Thrift Port</label>
	<input class="required" type="text" id="thriftPort" name="thriftPort" value="${thriftPort?html}" />
</p>

<p>
	<label for="jmxPort">JMX Port</label>
	<input class="required" type="text" id="jmxPort" name="jmxPort" value="${jmxPort?html}" />
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
