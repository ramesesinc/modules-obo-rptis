import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.common.*
import com.rameses.osiris2.client.*
import com.rameses.seti2.models.*;

class OccupancyPermitModel extends CrudFormModel {
    @Service(value="RptOboPluginService", connection="rpt")
    def svc;
    
    @Service(value="QueryService", connection="rpt")
    def qrySvc;
    
    @Service("OccupancyRpuService")
    def occSvc;
    
    def faas = [:];
    
    public void afterOpen() {
        if (entity.faasid) {
            faas = svc.openFaas([objid: entity.faasid]);
        }
    }
           
    def viewBldgFaas() {
        def q = [_schemaname:"faas_task"];
        q.findBy = [refid: entity.faasid ];
        q.where = ["enddate IS NULL"];
        def tsk = qrySvc.findFirst( q );
        def wu = "faas:open";
        def e = [:];
        e.objid = entity.faasid;
        e.taskid = tsk.objid;
        def invoker = Inv.lookupOpener(wu, [entity:e] );
        invoker.target = "self";
        return invoker;
    }
    
    def openFaas(bldgfaas) {
        reload();
        afterOpen();
        binding?.refresh();
    }
    
    def generateDocs() {
        if(!MsgBox.confirm("You are about to issue the RPT Documents. Proceed?")) return;
        def u = occSvc.generateDocs( [objid: entity.objid] );
        entity.putAll( u );
        reloadEntity();
        MsgBox.alert("documents successfully generated");
    }
    
}