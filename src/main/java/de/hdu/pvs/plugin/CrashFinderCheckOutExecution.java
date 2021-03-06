package de.hdu.pvs.plugin;

import java.io.File;
import java.io.IOException;

import org.jenkinsci.remoting.RoleChecker;

import hudson.FilePath.FileCallable;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.remoting.VirtualChannel;
import hudson.tasks.CommandInterpreter;
import hudson.tasks.Shell;

/**
 * 
 * @author Antsa Harinala Andriamboavonjy Created in October 2015
 */
public class CrashFinderCheckOutExecution implements FileCallable<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1973847243457921720L;

	private String commandCheckOut;

	private AbstractBuild<?, ?> build;

	private BuildListener buildListener;

	private Launcher launcher;

	public CrashFinderCheckOutExecution(AbstractBuild<?, ?> build, BuildListener buildListener,
			Launcher launcher, String commandCheckOut) {
		this.commandCheckOut = commandCheckOut;
		this.build = build;
		this.buildListener = buildListener;
		this.launcher = launcher;
	}

	@Override
	public void checkRoles(RoleChecker arg0) throws SecurityException {
		// TODO Auto-generated method stub

	}

	@Override
	public Object invoke(File f, VirtualChannel channel) throws IOException,
			InterruptedException {
		// TODO Auto-generated method stub
		CommandInterpreter runner = new Shell(this.commandCheckOut);
		buildListener.getLogger().println("echo " + commandCheckOut);
		runner.perform(build, launcher, buildListener);

		return null;
	}

}
