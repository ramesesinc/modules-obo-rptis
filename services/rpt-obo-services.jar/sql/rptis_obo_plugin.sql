[getLinkRpus]
select 
	fl.objid, 
	fl.state,
	fl.rputype, 
	fl.tdno, 
	fl.displaypin as pin,
	fl.totalmv, 
	fl.totalav 
from machrpu mr 
inner join faas_list fl on mr.objid = fl.rpuid 
inner join rpu br on mr.bldgmaster_objid = br.rpumasterid
inner join faas_list flb on br.objid = flb.rpuid 
where flb.objid = $P{faasid}
and fl.state = 'CURRENT';