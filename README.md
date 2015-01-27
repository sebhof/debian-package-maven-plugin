# debian-package-maven-plugin

This plugin allows you to create a Debian package for your Maven project from within the buildprocess.

This plugin is a small wrapper for _dpkg-buildpackage_, which has to be installed. Also _dh_make_ command is recommended. 

To get started, create your Maven Java project as usual and create an additional directory _/src/main/package_. Move to that directory an run the following command to create the basic structure and files for the Debian package:

`dh_make -n -s -p "<project_name>_1.0-SNAPSHOT" -y`

For better reading and reuse create some properties for your Debian package in the pom.xml:

```
<deb.distribution>unstable</deb.distribution>
<deb.package.architecture>all</deb.package.architecture>
<deb.package.name>${project.artifactId}</deb.package.name>
<deb.package.version>${project.version}-${buildNumber}</deb.package.version>
<deb.package.section>web</deb.package.section>
<deb.package.priority>optional</deb.package.priority>
<deb.working.directory>${project.build.directory}/${project.artifactId}-${project.version}-deb</deb.working.directory>
```

Configure the plugin in your pom.xml:

```
<plugin>
  <groupId>de.shofmann.maven</groupId>
  <artifactId>debian-package-maven-plugin</artifactId>
  <version>1.0-SNAPSHOT</version>
  <executions>
      <execution>
          <id>package</id>
          <goals>
              <goal>debian-package</goal>
          </goals>                                       
          <configuration>
              <workingDirectory>${deb.working.directory}</workingDirectory>
              <packageName>${deb.package.name}</packageName>
              <packageVersion>${deb.package.version}</packageVersion>
              <packageArchitecture>${deb.architecture}</packageArchitecture>
              <packageDistribution>${deb.distribution}</packageDistribution>
              <packageSection>${deb.package.section}</packageSection>
              <packagePriority>${deb.package.priority}</packagePriority>
              <!--dpkgBuildPackageExecutable>/some/other/location/dpkg-buildpackage</dpkgBuildPackageExecutable-->
          </configuration>
      </execution> 
  </executions>                
</plugin>
```

Configure Maven resource filtering for the _/src/main/package_ directory in order to use maven variables in the Debian package scripts:

```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-resources-plugin</artifactId>
    <version>2.7</version>	
    <executions>
        <execution>
            <id>copy-resources</id>
            <goals>
                <goal>copy-resources</goal>
            </goals>
            <phase>process-resources</phase>
            <configuration>
                <outputDirectory>${project.build.directory}/${project.artifactId}-${project.version}-deb</outputDirectory>
                <resources>
                    <resource>
                        <directory>src/main/package</directory>
                        <filtering>true</filtering>
                    </resource>
                </resources>
            </configuration>
        </execution>
    </executions>
</plugin>
```

Since the debian-package-maven-plugin is executed in the target directory after the filtering of resources, one may use maven variables in the Debian script files, so the _control_ file could be something like that:

```
Source: ${deb.package.name}
Section: ${deb.package.section}
Priority: ${deb.package.priority}
Maintainer: shofmann <shofmann@unknown>
Build-Depends: debhelper (>= 8.0.0)
Standards-Version: 3.9.4
Package: ${deb.package.name}
Architecture: ${deb.package.architecture}
Depends: ${shlibs:Depends}, ${misc:Depends}, java7-runtime
Description: Sample for usage of debian-package-maven-plugin
 Sample for usage of debian-package-maven-plugin
```

Configure the Debian scripts to meet your requirements, run `mvn install` and you are done. The Debian package is created in the _target_ directory. See the example _helloworld-deb-sample_ for details.
