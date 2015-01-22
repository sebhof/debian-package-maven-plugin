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

import java.io.File;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

/**
 * Mojo for building Debian Packages
 * @author shofmann
 */
@Mojo(
        name = "debian-package", 
        defaultPhase = LifecyclePhase.GENERATE_SOURCES, 
        requiresDependencyResolution = ResolutionScope.COMPILE
)
public class DebianPackageMojo extends AbstractMojo {

    @Parameter( defaultValue = "${project.basedir}", readonly = true )
    private File baseDir;

    @Parameter( defaultValue = "${project.build.directory}", readonly = true )
    private File targetDir;
    
    @Parameter( defaultValue = "${project}", readonly = true )
    private MavenProject project;
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        
    }
    
}
