package org.gallew;
public class Util {

    static String[] unit_list = {
	"", 
	"K", 
	"M", 
	"G",
	"P"
    };

    static public String Humanize(float value) {
	int unit = 0;
	while (value > 1023) {
	    unit = unit + 1;
	    value = value / 1024;
	}
	return String.format("%1$03.1f%2$s", value, unit_list[unit]);
    }

    static public String Humanize(float value, String tag) {
	int unit = 0;
	while (value > 1023) {
	    unit = unit + 1;
	    value = value / 1024;
	}
	return String.format("%1$03.1f%2$s%s", value, unit_list[unit], tag);
    }

    static public String Humanize(double value) {
	int unit = 0;
	while (value > 1023) {
	    unit = unit + 1;
	    value = value / 1024;
	}
	return String.format("%01.1f%s", value, unit_list[unit]);
    }

    static public String Humanize(double value, String tag) {
	int unit = 0;
	while (value > 1023) {
	    unit = unit + 1;
	    value = value / 1024;
	}
	return String.format("%01.1f%s%s", value, unit_list[unit], tag);
    }

}
    
