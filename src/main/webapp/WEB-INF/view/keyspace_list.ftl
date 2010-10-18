<#list keyspaces as keyspace>
<#if activeKeyspace == keyspace.name>
<li class ="active"><a href="${contextPath}/keyspace/${keyspace.name?html}/">${keyspace.name?html}</a></li>
<#if columnFamilies?exists>
<#list columnFamilies as columnFamily>
<#if activeColumnFamily == columnFamily.name>
<li class="cf active<#if !columnFamily_has_next> last</#if>"><a href="${contextPath}/keyspace/${keyspace.name?html}/${columnFamily.name?html}/">${columnFamily.name?html}</a></li>
<#else>
<li class="cf<#if !columnFamily_has_next> last</#if>"><a href="${contextPath}/keyspace/${keyspace.name?html}/${columnFamily.name?html}/">${columnFamily.name?html}</a></li>
</#if>
</#list>
</#if>
<#else>
<li><a href="${contextPath}/keyspace/${keyspace.name?html}/">${keyspace.name?html}</a></li>
</#if>
</#list>
