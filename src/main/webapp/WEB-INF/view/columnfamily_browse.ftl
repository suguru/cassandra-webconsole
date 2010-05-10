<table>

<tr>
<th>Key</th>
<th>Columns</th>
</tr>

<#list slices as slice>
<#if !slice_has_next><#assign lastSlice = slice /><#break /></#if>
<tr>
<td>0x${slice.keyHex}</td>
<#--<td>0x${slice.key}</td>-->
<td>
<#list slice.columns as column>
0x${column}<#if column_has_next>,</#if>
<#--${column}<#if column_has_next>,</#if>-->
</#list>
<#if slice.hasMoreColumn>...</#if>
</td>
</tr>
</#list>
<#if slices?size &gt; count>
<tr>
<td colspan="2">
<a href="browse?start=${lastSlice.keyHex}" onclick="$('#browse').load('browse?start=${lastSlice.keyHex}');return false">Next</a>
</td>
</tr>
</#if>

</table>