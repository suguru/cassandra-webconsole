
<form class="float" id="floatForm" method="post" action="cleanup">

<p>
	Are you sure want to cleanup <strong>&quot;${address?html}&quot;</strong> ?
</p>

<p class="buttons">
	<button type="submit" class="submit">Cleanup</button>
	<button type="button" class="cancel" onclick="$.fn.colorbox.close()">Cancel</button>
</p>

</form>
