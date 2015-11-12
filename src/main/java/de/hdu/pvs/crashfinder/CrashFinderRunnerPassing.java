package de.hdu.pvs.crashfinder;

import hudson.model.BuildListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.util.CancelException;

import de.hdu.pvs.crashfinder.analysis.FindPassingSeed;
import de.hdu.pvs.crashfinder.analysis.IRStatement;
import de.hdu.pvs.crashfinder.analysis.Intersection;
import de.hdu.pvs.crashfinder.analysis.ShrikePoint;
import de.hdu.pvs.crashfinder.analysis.Slicing;
import de.hdu.pvs.crashfinder.analysis.SlicingOutput;
import de.hdu.pvs.crashfinder.instrument.InstrumenterMain;
import de.hdu.pvs.crashfinder.util.Files;
import de.hdu.pvs.crashfinder.util.Globals;
import de.hdu.pvs.crashfinder.util.WALAUtils;

/**
 * 
 * @author Antsa Harinala Andriamboavonjy, Dominik Fay Created in October 2015
 * 
 */
public class CrashFinderRunnerPassing implements CrashFinderRunner {

	private final CrashFinderImplementation crashFinderImpl;

	private Statement seedStatement;

	private BuildListener listener;

	private String seed;

	public CrashFinderRunnerPassing(
			CrashFinderImplementation crashFinderImpl,
			BuildListener listener, String seed) {
		this.crashFinderImpl = crashFinderImpl;
		this.listener = listener;
		this.seed = seed;
	}

	public String getSeed() {
		return this.seed;
	}

	public void runner() {

		try {

			String pathToJar = crashFinderImpl.getPathToJarFile();
			//String pathToStackTrace = crashFinderImpl.getPathToStackTrace();
			String pathToDiffFile = crashFinderImpl.getPathToDiffOut();
			String pathToInstrJar = crashFinderImpl
					.getPathToInstrumentedJarFile();
			String pathToLogSlicing = crashFinderImpl.getPathToLogSlicing();
			String pathToExclusionFile = crashFinderImpl.getPathToExclusionFile();
			String pathToWorkspace = crashFinderImpl.getCanonicalPathToWorkspaceDir();

			// 1. Slicing
			listener.getLogger().println("Initializing slicing ....");
			Slicing slicing = crashFinderImpl.initializeSlicing(pathToJar, pathToExclusionFile);

			listener.getLogger().println("Find passing seed statement ....");
			try {

				FindPassingSeed computePassingSeed = new FindPassingSeed();
				String passingSeed = computePassingSeed.computeSeed(this.seed, pathToDiffFile);
				listener.getLogger().println("passing seed string: "+passingSeed);
				this.seedStatement = computePassingSeed.findSeedStatement(passingSeed, slicing);
				listener.getLogger().println("passing seed statement: "+this.seedStatement);
				this.seed = crashFinderImpl.getSeed();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// 3.Backward slicing
			Collection<? extends Statement> slice = slicing.computeSlice(seedStatement);
			WALAUtils.dumpSliceToFile(new ArrayList<Statement>(slice), pathToLogSlicing);

			// 4. Intersection
			listener.getLogger().println("Executing intersection ....");
			Intersection intersection = new Intersection();
			List<String> matchingSet = intersection.matchingSet(pathToDiffFile);
			Collection<Statement> chop = intersection.intersection(matchingSet, slice);
			
			Collection<IRStatement> irs = Slicing.convert(chop);
			SlicingOutput output = new SlicingOutput(irs);

			StringBuilder sb = new StringBuilder();
			for (ShrikePoint po: output.getAllShrikePoints()) {
				sb.append(po);
				sb.append("#"+po.instructionIndex);
				sb.append(Globals.lineSep);
			}
			try {
	            File shrikeDump = new File(pathToWorkspace, "shrikeDump_passing.txt");
				System.out.println("Write to file: " + shrikeDump);
				Files.writeToFile(sb.toString(), shrikeDump);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// 5. Instrument
			listener.getLogger().println("Executing instrumentation ...");
			InstrumenterMain instrumenter = new InstrumenterMain();
			instrumenter.instrument(pathToJar, pathToInstrJar, chop);

		} catch (IOException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CancelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
