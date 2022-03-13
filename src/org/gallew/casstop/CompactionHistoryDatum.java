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
    String id;
    String keyspace_name;
    String columnfamily_name;
    Long compacted_at;
    Long bytes_in;
    Long bytes_out;
    String rows_merged;
    SimpleDateFormat timeFormatter = new SimpleDateFormat("yy-MM-dd'T'HH:mm:ss.SS");
    
    CompactionHistoryDatum(CompositeDataSupport cds) {
        id = (String)cds.get("id");
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
        return (int)(compacted_at - otherDatum.compacted_at);
    }

    public String format(Integer keyWidth, Integer colWidth, Integer maxWidth) {
        String formatter = String.format("%%s %%%ds %%%ds %%s %%s %%s %%s", keyWidth, colWidth);
        String result =  String.format(formatter, id, keyspace_name, columnfamily_name,
                                       timeFormatter.format(new Date(compacted_at)), Util.Humanize((double)bytes_in, "B"), 
                                       Util.Humanize((double)bytes_out, "B"), rows_merged);
        maxWidth -= 3;
        if (result.length() > maxWidth)
            return result.substring(0, maxWidth);
        return result;
    }
}
