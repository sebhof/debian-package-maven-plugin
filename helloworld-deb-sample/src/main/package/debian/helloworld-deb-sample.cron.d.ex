#
# Regular cron jobs for the helloworld-deb-sample package
#
0 4	* * *	root	[ -x /usr/bin/helloworld-deb-sample_maintenance ] && /usr/bin/helloworld-deb-sample_maintenance
