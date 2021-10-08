import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.common.*
import com.rameses.osiris2.client.*

class BuildingFaasCreateOptionModel {
    @Binding
    def binding;
    
    @Service(value="RptOboPluginService", connection="rpt")
    def svc;
    
    @Caller
    def caller;
    
    def entity;
    
    def option = "bldg";
    def linkedfaas;
    
    void init() {
        
    }
    
    def afterCreate = {faas ->
        caller.openFaas(faas);
        binding.fireNavigation("_close");
    }
    
    def doOk() {
        def params = [
            entity: entity, 
            afterCreate: afterCreate,
            option: option,
        ];
        
        if (option != 'link') {
            return Inv.lookupOpener("obo_building_faas:create", params);
        } else {
            if( !linkedfaas.rputype.matches('bldg|mach') )
                throw new Exception("Please link to a building or machinery rpu type faas");
            entity.faas = linkedfaas;
            entity.state = 1;
            entity.faasid = linkedfaas.objid;
            def occupancy = [:]
            occupancy.objid = entity.objid;
            occupancy.faasid = entity.faasid;
            occupancy.permitno = entity.bldgpermitno;
            occupancy.permitdate = entity.bldgpermitdtissued;
            occupancy.occpermitno = entity.permitno;
            occupancy.dtcertoccupancy = entity.permitdtissued;
            svc.updateOccupancyFaas(occupancy);
            caller.openFaas(linkedfaas);
            return "_close";
        }
    }
    
    def doCancel() {
        return "_close";
    }

}