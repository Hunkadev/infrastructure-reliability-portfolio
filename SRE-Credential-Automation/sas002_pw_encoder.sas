* Author: Christian Hunkus;
* Date: 12/24/2020;
* Contact: christian.hunkus.osv@fedex.com;
* Version: 1.0.0;

%let pass = %sysget(pass);

proc pwencode in="&pass" method=sas002;
run;
