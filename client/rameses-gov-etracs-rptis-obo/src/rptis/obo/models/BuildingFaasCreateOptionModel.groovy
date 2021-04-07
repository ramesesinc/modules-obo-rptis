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
            entity.faas = linkedfaas;
            entity.state = 1;
            entity.faasid = linkedfaas.objid;
            svc.updateOccupancyFaas([objid: entity.objid, faasid: entity.faasid]);
            caller.openFaas(linkedfaas);
            return "_close";
        }
    }
    
    def doCancel() {
        return "_close";
    }

}