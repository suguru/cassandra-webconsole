
<form class="float" id="floatForm" method="post" action="${keyspace?html}/${columnFamily?html}/flush">

<p>
	Are you sure want to flush column families?
</p>

<p class="buttons">
	<button type="submit" class="submit">Flush</button>
	<button type="button" class="cancel" onclick="$.fn.colorbox.close()">Cancel</button>
</p>

</form>
