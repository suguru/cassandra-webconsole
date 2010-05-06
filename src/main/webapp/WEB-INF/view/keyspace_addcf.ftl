
<form class="float" id="floatForm" method="post" action="addcf">

<p>
	<label for="name">Column Family Name</label>
	<input class="required" type="text" id="name" name="name" value="" />
</p>

<p>
	<label for="comment">Comment</label>
	<input type="text" id="comment" name="comment" value="" />
</p>

<p>
	<label for="type">Column Type</label>
	<select name="type" id="type">
		<option value="Standard">Standard</option>
		<option value="Super">Super</option>
	</select>
</p>

<p>
	<label for="comparator">Comparator</label>
	<select name="comparator" id="comparator">
		<option value="org.apache.cassandra.db.marshal.UTF8Type">UTF8</option>
		<option value="org.apache.cassandra.db.marshal.LongType">Long</option>
		<option value="org.apache.cassandra.db.marshal.LexicalUUIDType">UUID</option>
		<option value="org.apache.cassandra.db.marshal.TimeUUIDType">TimeUUID</option>
		<option value="org.apache.cassandra.db.marshal.AsciiType">Ascii</option>
	</select>
</p>

<p id="subComparators">
	<label for="subComparator">Sub Comparator</label>
	<select name="subComparator" id="subComparator">
		<option value="org.apache.cassandra.db.marshal.UTF8Type">UTF8</option>
		<option value="org.apache.cassandra.db.marshal.LongType">Long</option>
		<option value="org.apache.cassandra.db.marshal.LexicalUUIDType">UUID</option>
		<option value="org.apache.cassandra.db.marshal.TimeUUIDType">TimeUUID</option>
		<option value="org.apache.cassandra.db.marshal.AsciiType">Ascii</option>
	</select>
</p>

<p class="buttons">
	<button type="submit" class="submit">Create</button>
	<button type="button" class="cancel" onclick="$.fn.colorbox.close()">Cancel</button>
</p>

</form>

<script type="text/javascript">
$("#subComparators").hide();
$("#type").change(function() {
	if ($(this).val() == "Super") {
		$("#subComparators").show();
	} else {
		$("#subComparators").hide();
	}
	$.fn.colorbox.resize();
});
$("#floatForm").validate({
	invalidHandler: function(form,validator) {
		$.fn.colorbox.resize();
	}
});
</script>
