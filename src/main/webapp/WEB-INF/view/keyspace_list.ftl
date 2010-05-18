<#list keyspaces as keyspace>
<#if activeKeyspace == keyspace>
<li class ="active"><a href="${contextPath}/keyspace/${keyspace?html}/">${keyspace?html}</a></li>
<#if columnFamilies?exists>
<#list columnFamilies as columnFamily>
<#if activeColumnFamily == columnFamily>
<li class="cf active<#if !columnFamily_has_next> last</#if>"><a href="${contextPath}/keyspace/${keyspace?html}/${columnFamily?html}/">${columnFamily?html}</a></li>
<#else>
<li class="cf<#if !columnFamily_has_next> last</#if>"><a href="${contextPath}/keyspace/${keyspace?html}/${columnFamily?html}/">${columnFamily?html}</a></li>
</#if>
</#list>
</#if>
<#else>
<li><a href="${contextPath}/keyspace/${keyspace?html}/">${keyspace?html}</a></li>
</#if>
</#list>
