package org.gallew.casstop;

import java.lang.Math;
import java.lang.String;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.gallew.casstop.Util;
import javax.management.openmbean.CompositeDataSupport;

/*
 * In theory, this class is unnecessary because a CompositeDataSupport should mostly
 * DTRT.  In practice, it's either voodoo, too complicated for me, or not actually possible.
 * So, now we have a class that undoubtedly duplicates some Cassandra class somewhere which
 * easily exposes a bunch of attributed and makes producing a nicely-formatted table easy.
 */
public class CompactionHistoryDatum implements Comparable<CompactionHistoryDatum> {
    String keyspace_name;
    String columnfamily_name;
    Long compacted_at;
    Long bytes_in;
    Long bytes_out;
    String rows_merged;
    SimpleDateFormat timeFormatter = new SimpleDateFormat("yy-MM-dd'T'HH:mm:ss.SS");
    
    CompactionHistoryDatum() {
        keyspace_name = "Keyspace Name";
        columnfamily_name = "Column Family";
        compacted_at = Long.MAX_VALUE;
        bytes_in = 0L;
        bytes_out = 0L;
        rows_merged = "Rows Merged";
    }

    CompactionHistoryDatum(CompositeDataSupport cds) {
        keyspace_name = (String)cds.get("keyspace_name");
        columnfamily_name = (String)cds.get("columnfamily_name");
        compacted_at = (Long)cds.get("compacted_at");
        bytes_in = (Long)cds.get("bytes_in");
        bytes_out = (Long)cds.get("bytes_out");
        rows_merged = (String)cds.get("rows_merged");
    }

    public int keyLength() {
        return keyspace_name.length();
    }

    public int colLength() {
        return columnfamily_name.length();
    }

    public int compareTo(CompactionHistoryDatum otherDatum) {
        return (int)(otherDatum.compacted_at - compacted_at);
    }

    public String format(Integer keyWidth, Integer colWidth, Integer maxWidth) {
        String formatter = String.format("%%%ds %%%ds %%-21s %%12s %%s", keyWidth, colWidth);
        String result;
        if (compacted_at == Long.MAX_VALUE) {
            result =  String.format(formatter, keyspace_name, columnfamily_name,
                                    "Completion time",
                                    "Size Reduced",
                                    rows_merged);
        } else {
            result =  String.format(formatter, keyspace_name, columnfamily_name,
                                    timeFormatter.format(new Date(compacted_at)),
                                    Util.Humanize((double)bytes_in - (double)bytes_out, "B"),
                                    rows_merged);
        };
        maxWidth -= 3;
        if (result.length() > maxWidth)
            return result.substring(0, maxWidth);
        return result;
    }
}
