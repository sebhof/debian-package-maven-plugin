#
# Simple makefile for installing 
# helloworld-deb-sample
#
# shofmann, 25.01.2015

# empty - required by dh_buildpackage to install in any dir
DESTDIR = 

# nothing to do here...
build:


# nothing to do here...
clean:


install:
#	/bin/mkdir $(DESTDIR)/usr/share/${deb.package.name}
	/bin/cp ../${deb.package.name}-${project.version}.jar $(DESTDIR)/usr/share/${deb.package.name}/${deb.package.name}-${deb.package.version}.jar
	/bin/cp helloworld $(DESTDIR)/usr/bin	
	/bin/chmod 755 $(DESTDIR)/usr/bin/helloworld

uninstall:
	/bin/rm -f $(DESTDIR)/usr/bin/helloworld
	/bin/rm -rf $(DESTDIR)/usr/share/${deb.package.name}



