
<form class="float" id="floatForm" method="post" action="${contextPath}/token/remove">

<p>
	<label for="name">Token</label>
	<input class="required" type="text" id="token" name="token" value="" />
</p>

<p class="buttons">
	<button type="submit" class="submit">Remove</button>
	<button type="button" class="cancel" onclick="$.fn.colorbox.close()">Cancel</button>
</p>

<script type="text/javascript">
$("#floatForm").validate({
	invalidHandler: function(form,validator) {
		$.fn.colorbox.resize();
	}
});
</script>

</form>
