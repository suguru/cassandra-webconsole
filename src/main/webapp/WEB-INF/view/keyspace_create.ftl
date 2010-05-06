
<form class="float" id="floatForm" method="post" action="${contextPath}/keyspace/create">

<p>
	<label for="name">Keyspace Name</label>
	<input class="required" type="text" id="name" name="name" value="" />
</p>

<p>
	<label for="replicationFactor" class="required">Replication Factor</label>
	<input type="text" id="replicationFactor" name="replicationFactor" value="1" />
</p>

<p>
	<label for="strategy" class="required">Strategy</label>
	<select name="strategy">
		<option value="RackUnaware">Rack Unaware</option>
		<option value="RackAware">Rack Aware</option>
	</select>
</p>

<p class="buttons">
	<button type="submit" class="submit">Create</button>
	<button type="button" class="cancel" onclick="$.fn.colorbox.close()">Cancel</button>
</p>

</form>

<script type="text/javascript">
$("#floatForm").validate({
	invalidHandler: function(form,validator) {
		$.fn.colorbox.resize();
	}
});
</script>
