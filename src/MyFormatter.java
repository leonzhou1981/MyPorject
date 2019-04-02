import java.util.ArrayList;
import java.util.List;

public class MyFormatter {

    public static void main(String[] args) {
        String sql = "update REVENUE set sno=?,sortorder=?,chrgcode=?,chrgdesc=?,localdesc=?,rate=?,quantity=?,quantityunit=?,quantity1=?,quantity1unit=?,currcode=?,exrate=?,amtfc=?,amtlc=?,ppcc=?,vat=?,co_partyid=?,billing_partyid=?,duecarragt=?,ismin=?,isprogressive=?,issupress=?,invdocno=?,isown=?,csind=?,actualamtlc=?,actualvatamtlc=?,remark=?,createby=?,createdate=?,updateby=?,updatedate=?,doctype=?,isica=?,profitcentre=?,costcentre=?,timezone=?,shortpaid=?,source_unid=?,sourcetype=?,invsts=?,parentsno=?,actualcurrcode=?,actualexrate=?,actualamtbc=?,approveby1=?,approvedate1=?,approveby2=?,approvedate2=?,job_unid=?,isfincommpaid=?,fincommpaidby=?,fincommpaiddate=?,icapost_unid=?,postby=?,postdate=?,quantitydesc=?,originsourceunid=?,originsourcetype=?,originsno=?,category=?,taxable=?,declpartfrt=?,percenttype=?,vatamtlc=?,datasource=?,refno=?,icarefno=?,isica2=?,isdisbursement=?,source_sno=?,batchno=?,passthrough=?,uniqueno=?,sourceuniqueno=?,islock=?,ivhdrunid=?,isorgchrg=?,iscopchrg=?,printan=?,uniquerefno=?,contno=?,contnolist=?,edirefno=?,intfstatus=?,intfpostdate=?,intfpostby=?,haspassthrough=?,cri=?,arap=?,combinejobunid=?,combinesno=?,combinesourcetype=?,payeepartyid=?,recognitionyear=?,recognitionperiod=?,recognitiondate=?,rmsourceunid=?,rmsourcetype=?,basechg=?,vatincrate=?,vatincamtfc=?,vatincamtlc=?,tariffunid=?,tariffno=?,tariffjobunid=?,tariffsno=?,tarifffromtype=?,sourcerefno=?,isfrommaster=?,vatamt=?,susadvdocno=?,susadvdoctype=?,susadvhdrunid=?,validfrom=?,validto=?,calset=?,icaconfirmed=?,isvoid=?,rmdelegationstatus=?,rmoriginalrate=?,minchgfromtariff=?,rmratetype=?,chrgcodesortorder=? where SNO=? and SOURCETYPE=? and SOURCE_UNID=? and  1=1";
        String parameters = "19, 1, H0001B, HANDLING CHARGE, HANDLING CHARGE, 55.0, 1.0, SHP, null, null, USD, 1.0, 55.00, 55.00, C, null, null, CNWCNCTUA,  , 0, 0, 0, null, 1, CR, null, null, null, VROZUL, 2018-09-20 00:03:14.0, VROZUL, 2018-10-09 13:05:08.394, IV, 0, null, null, PST, null, 122000000002341449, JB, null, 3, null, null, null, null, null, null, null, 122000000002341449, null, null, null, null, null, null, SHIPMENT, null, null, null, null, null, 1, null, null, 0, null, null, 0, 0, null, null, null, 503A2DE119E781AF631B845F94F11C8B, null, 0, null, null, null, 1, null, null, null, 122000000002341449|JB|0009, null, null, null, 0, null, null, null, null, null, null, null, null, null, null, null, 0.00, null, null, null, null, null, null, null, null, null, 0, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 19, JB, 122000000002341449";

        List lstParam = new ArrayList();
        String piece = null;
        while (parameters != null && parameters.length() > 0) {
            int pos = parameters.indexOf(", ");
            if (pos > 0) {
                piece = parameters.substring(0, pos);
                lstParam.add(piece);
                parameters = parameters.substring(pos + 2);
            } else {
                lstParam.add(parameters);
                break;
            }
        }

        if (sql != null && sql.length() > 0) {
            int i = 0;
            while (sql.indexOf("?") > 0) {
                int pos = sql.indexOf("?");
                String first = sql.substring(0, pos);
                String second = sql.substring(pos + 1);
                sql = first + "'" + lstParam.get(i) + "'" + second;
                i++;
            }
        }
        System.out.println(sql);
    }
}
