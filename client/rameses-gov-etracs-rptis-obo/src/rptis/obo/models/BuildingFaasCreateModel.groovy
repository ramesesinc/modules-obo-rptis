import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.common.*
import com.rameses.osiris2.client.*

class BuildingFaasCreateModel {
    @Caller
    def caller;
    
    @Service(value="RptOboPluginService", connection="rpt")
    def svc;

    @FormTitle
    String title = "Occupancy Building FAAS (Create)";
    
    def entity;
    def rpus;
    
    void init() {
        updateTaxpayer();
        rpus = svc.getRpus([bldgappid: entity.bldgappid]);
    }
   
    def createFaas() {
        if (MsgBox.confirm("Submit and create building FAAS?")) {
            def bldgfaas = svc.createFaas(entity);
            entity.state = 1;
            entity.faasid = bldgfaas.objid;
            caller.openFaas(bldgfaas);
            return "_close";
        }
    }

    def viewLandFaas() {
        def invoker = Inv.lookupOpener('faas:open:closedwf', [entity: [objid: entity.landfaas.refid]]);
        invoker.target = "popup";
        return invoker;
    }
    
    def viewBuildingFaas() {
        def wu = entity.state == 0 ? 'faas:open' : 'faas:open:closedwf';
        def invoker = Inv.lookupOpener(wu, [entity: [objid: entity.faasid]]);
        invoker.target = "self";
        return invoker;
    }

    void updateTaxpayer() {
        def applicant = entity.applicant;
        if (applicant.profileid) {
            entity.taxpayer = [
                objid: applicant.profileid,
                name: applicant.name,
                addresstext: applicant.address.text
            ]
        }
    }

}