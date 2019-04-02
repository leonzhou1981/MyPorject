import java.sql.Timestamp;
import java.util.Date;
import java.util.TimeZone;

public class TimezoneChecker {

    public static void main(String[] args) {
        String fromTimeZoneID = java.util.TimeZone.getDefault().getID();
        String toTimeZoneID = "Asia/Singapore";
        Date originalDate = new Date();
        if (originalDate != null && fromTimeZoneID != null && toTimeZoneID != null) {
            long lngDate = originalDate.getTime();
            TimeZone fromTimeZone = TimeZone.getTimeZone(fromTimeZoneID);
            TimeZone toTimeZone = TimeZone.getTimeZone(toTimeZoneID);

            int fromOffset = fromTimeZone.getOffset(lngDate);
            int toOffset = toTimeZone.getOffset(lngDate);

            long currUTCTime = lngDate - fromOffset;
            long clientTime = currUTCTime + toOffset;

            if (toOffset != toTimeZone.getOffset(clientTime)) {
                clientTime += toTimeZone.getOffset(clientTime) - toOffset;
            }

            if (originalDate instanceof Timestamp) {
                System.out.println(new Timestamp(clientTime));
            } else {
                System.out.println(new Date(clientTime));
            }
        }
        System.out.println(originalDate);
    }

}
