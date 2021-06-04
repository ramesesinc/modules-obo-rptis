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
and fl.state = 'CURRENT'

[findFaasInfo]
select 
	f.objid,
	f.state,
	f.titleno,
	f.titledate,
	b.objid as barangay_objid,
	b.name as barangay_name,
	rp.cadastrallotno as lotno,
	rp.blockno,
	rp.street,
	pc.code as class_code, 
	pc.name as class_name
from faas f 
inner join realproperty rp on f.realpropertyid = rp.objid 
inner join rpu r on f.rpuid = r.objid 
inner join propertyclassification pc on r.classification_objid = pc.objid 
inner join barangay b on rp.barangayid = b.objid 
where f.objid = $P{objid}


