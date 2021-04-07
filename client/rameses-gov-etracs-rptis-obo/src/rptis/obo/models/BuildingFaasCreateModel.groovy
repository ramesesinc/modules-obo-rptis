import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.common.*
import com.rameses.osiris2.client.*

class BuildingFaasCreateModel {
    @Service(value="RptOboPluginService", connection="rpt")
    def svc;

    @FormTitle
    String title = "Occupancy Building FAAS (Create)";
    
    def entity;
    def option;
    def rpus;
    def afterCreate = {};
    
    void init() {
        updateTaxpayer();
        rpus = svc.getRpus([bldgappid: entity.bldgappid]);
        entity.suffix = option == 'bldg' ? 1001 : 2001;
        entity.rputype = option;
    }
   
    def createFaas() {
        validateSuffix();
        if (MsgBox.confirm("Submit and create FAAS?")) {
            def bldgfaas = svc.createFaas(entity);
            entity.state = 1;
            entity.faasid = bldgfaas.objid;
            afterCreate(bldgfaas);
            return "_close";
        }
    }
    
    void validateSuffix() {
        if (option == 'bldg' && (entity.suffix < 1001 || entity.suffix > 1999))
            throw new Exception('Building suffix must be between 1001 and 1999');
            
        if (option == 'mach' && (entity.suffix < 2001 || entity.suffix > 2999))
            throw new Exception('Machinery suffix must be between 2001 and 2999');
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