/*
 * Copyright (C) 2015 shofmann.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package de.shofmann.maven.debian;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Mojo for building Debian Packages
 *
 * @author shofmann
 */
@Mojo(
        name = "debian-package",
        defaultPhase = LifecyclePhase.PACKAGE,
        requiresDependencyResolution = ResolutionScope.COMPILE
)
public class DebianPackageMojo extends AbstractMojo {

    @Parameter(property = "workingDirectory", required = true)
    private File workingDirectory;

    @Parameter(property = "packageName", required = true)
    private String packageName;

    @Parameter(property = "packageArchitecture", required = true)
    private String packageArchitecture;

    @Parameter(property = "packageVersion", required = true)
    private String packageVersion;

    @Parameter(property = "packageDistribution", required = true)
    private String packageDistribution;

    @Parameter(property = "packageSection", required = true)
    private String packageSection;

    @Parameter(property = "packagePriority", required = true)
    private String packagePriority;

    @Parameter(property = "dpkgBuildPackageExecutable", defaultValue = "/usr/bin/dpkg-buildpackage", required = true)
    private File dpkgBuildPackageExecutable;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        
        super.getLog().info("DebianPackageMojo starting execution...");

        if (super.getLog().isDebugEnabled()) {
            super.getLog().debug("workingDirectory: " + workingDirectory);
            super.getLog().debug("packageName: " + packageName);
            super.getLog().debug("packageArchitecture: " + packageArchitecture);
            super.getLog().debug("packageVersion: " + packageVersion);
            super.getLog().debug("packageDistribution: " + packageDistribution);
            super.getLog().debug("packageSection: " + packageSection);
            super.getLog().debug("packagePriority: " + packagePriority);
            super.getLog().debug("workingDirectory: " + workingDirectory);
        }

        if (!dpkgBuildPackageExecutable.exists() && !dpkgBuildPackageExecutable.canExecute()) {
            throw new MojoExecutionException(String.format("dpkg-buildpackage executable not found at %s or unable to execute.", dpkgBuildPackageExecutable));
        }

        if (!workingDirectory.exists()) {
            if (!workingDirectory.mkdirs()) {
                throw new MojoExecutionException(String.format("Unable to create non existing working directory: %s", workingDirectory));
            }
        }

        List<String> dpkgBuildPackageCommand = new ArrayList<String>(Arrays.asList(dpkgBuildPackageExecutable.getAbsolutePath(), "-uc", "-us", "-r"));

        if (!packageArchitecture.equalsIgnoreCase("ALL")) {
            dpkgBuildPackageCommand.add("-a " + packageArchitecture);
        }

        super.getLog().info(String.format("Excuting command %s", dpkgBuildPackageCommand.toString()));

        ProcessBuilder pb = new ProcessBuilder(dpkgBuildPackageCommand);
        pb.directory(workingDirectory);

        Process p;
        try {
            p = pb.start();
        } catch (IOException ex) {
            throw new MojoExecutionException(String.format("Error executing command %s", dpkgBuildPackageCommand.toString()), ex);
        }

        StreamHandlerThread stdout = new StreamHandlerThread(p.getInputStream(), LogLevel.INFO);
        StreamHandlerThread stderr = new StreamHandlerThread((p.getErrorStream()), LogLevel.ERROR);
        stdout.start();
        stderr.start();

        int retcode = 1;
        try {
            retcode = p.waitFor();
        } catch (InterruptedException ex) {
            //nothing to do here
        }
        stdout.stopThread();
        stderr.stopThread();
        if (retcode == 0) {
            super.getLog().info("DebianPackageMojo executed successfully.");
        } else {
            throw new MojoExecutionException("dpkg-buildpackage Unexpected process return code :" + retcode);
        }
        
    }

    /**
     * Thread for reading the input streams
     */
    private class StreamHandlerThread extends Thread {

        private boolean isRunning = false;

        private InputStream inputStream;

        private LogLevel logLevel;

        public StreamHandlerThread(InputStream inputStream, LogLevel logLevel) {
            this.inputStream = inputStream;
            this.logLevel = logLevel;
        }

        @Override
        public void run() {

            isRunning = true;
            String line = null;
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            try {
                while ((line = br.readLine()) != null && isRunning) {
                    if (logLevel.equals(LogLevel.INFO)) {
                        getLog().info(line);
                    } else {
                        getLog().error(line);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Error reading stream", e);
            }

        }

        public void stopThread() {
            isRunning = false;
        }

    }

    /**
     * Enum defining the loglevels
     */
    private enum LogLevel {
        INFO,
        ERROR
    }

}
