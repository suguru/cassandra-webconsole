
<form class="float" id="floatForm" method="post" action="drop">

<p>
	Are you sure want to drop keyspace <strong>&quot;${name?html}&quot;</strong> ?
</p>

<p class="buttons">
	<button type="submit" class="submit">Drop</button>
	<button type="button" class="cancel" onclick="$.fn.colorbox.close()">Cancel</button>
</p>

</form>
