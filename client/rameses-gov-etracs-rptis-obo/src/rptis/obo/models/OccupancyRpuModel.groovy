import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.common.*
import com.rameses.osiris2.client.*
import com.rameses.seti2.models.*;

class OccupancyRpuModel extends CrudFormModel {
    @Service(value="RptOboPluginService", connection="rpt")
    def svc;
    
    @Service(value="QueryService", connection="rpt")
    def qrySvc;
    
    @Service(value="RPTAssessmentNoticeService", connection="rpt")
    def noticeSvc;
    
    @Service("OccupancyRpuService")
    def occSvc;
    
    def faas = [:];
    
    public void afterOpen() {
        if (entity.faasid) {
            faas = svc.openFaas([objid: entity.faasid]);
        }
    }
           
    def fetchOpenFaasEntry( def faasid ) {
        def q = [_schemaname:"faas_task"];
        q.findBy = [refid: faasid ];
        q.where = ["enddate IS NULL"];
        def tsk = qrySvc.findFirst( q );
        def wu = (tsk?.objid)  ? 'faas:open' : 'faas:open:closedwf';
        def invoker = Inv.lookupOpener(wu, [entity: [objid: faasid, taskid: tsk?.objid, taskstate: tsk?.state] ]);
        invoker.target = "self";
        return invoker;        
    }

    def viewFaas() {
        return fetchOpenFaasEntry( entity.faasid  );
    }
    
    def openFaas(bldgfaas) {
        reload();
        afterOpen();
        binding?.refresh();
    }
    
    def generateTruecopy() {
        if(!MsgBox.confirm("You are about to generate the RPT Documents. Proceed?")) return;
        def u = occSvc.generateTruecopy( [objid: entity.objid] );
        entity.putAll( u );
        reloadEntity();
        MsgBox.alert("documents successfully generated");
    }
    
    def issueNOA() {
        if(!MsgBox.confirm("You are about to issue the Notice of Assessment. Make sure this transaction is final. Proceed?")) return;
        def u = occSvc.issueNOA( [objid: entity.objid] );
        entity.putAll( u );
        reloadEntity();
    }
    
    def viewTrueCopy() {
        if(!entity.truecopycertid ) throw new Exception("True copy not yet generated");
        def op =  Inv.lookupOpener( 'tdtruecopy:view', [entity: [objid: entity.truecopycertid ]] );
        op.target ="popup";
        return op;
    }
    
    def viewNoa() {
        if(!entity.noaid ) throw new Exception("NOA not yet generated");
        def data = noticeSvc.getReportData([objid: entity.noaid]);
        def op = Inv.lookupOpener( 'assessmentnotice:report', [entity: data]);
        op.target ="popup";
        return op;
    }
    
    void reloadList() {
        listHandler.reload();
    }
    
    void generateMachineryRPT() {
        if( !MsgBox.confirm("You are about to generate the related RPT machinery entries. Proceed?") ) return;
        occSvc.generateMachineryEntries( [objid: entity.objid] );
        listHandler.reload();
    }
    
    def listHandler = [
        fetchList: { o->
            if(!entity.faasid) return [];
            def m = [_schemaname: "occupancy_rpu_item"]
            m.findBy = [parentid: entity.objid];
            return queryService.getList(m);
        },
        openItem: { o,col->
            return fetchOpenFaasEntry( o.faasid  ) 
        }
    ] as BasicListModel;
    
}