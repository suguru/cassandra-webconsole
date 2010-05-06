
<form class="float" id="floatForm" method="post" action="rename">

<p>
	<label for="name" class="required">Keyspace Name</label>
	<input type="text" class="required" id="name" name="name" value="${name?html}" />
</p>

<p class="buttons">
	<button type="submit" class="submit">Rename</button>
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
