
<form class="float" id="floatForm" method="post" action="./move">

<p>
	<label for="name">New Token</label>
	<input class="required" type="text" id="token" name="token" value="" />
</p>

<p class="buttons">
	<button type="submit" class="submit">Move</button>
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
