package com.ssll.rsync;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public class FastBackup {
	boolean verbose = false;
	OutputStream out = System.out;

	public FastBackup(boolean verbose, OutputStream out) {
		this.verbose = verbose;
		this.out = out;
	}

	private void log(String str) {
		System.out.println(str);
	}

	public static File lastFileModified(String dir) {
		File fl = new File(dir);
		File[] files = fl.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isFile();
			}
		});
		long lastMod = Long.MIN_VALUE;
		File choice = null;
		for (File file : files) {
			if (file.lastModified() > lastMod) {
				choice = file;
				lastMod = file.lastModified();
			}
		}
		return choice;
	}

	/* Get the newest file for a specific extension */
	public File getTheNewestFile(String filePath, String ext) {
		File theNewestFile = null;
		File dir = new File(filePath);
		FileFilter fileFilter = new WildcardFileFilter("*." + ext);
		File[] files = dir.listFiles(fileFilter);

		if (files.length > 0) {
			/** The newest file comes first **/
			Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
			theNewestFile = files[0];
		}

		return theNewestFile;
	}

	/* Get the newest file for a specific extension */
	public File getTheNewestFileByPattern(String filePath, String pattern) {
		File theNewestFile = null;
		File dir = new File(filePath);
		// FileFilter fileFilter = new WildcardFileFilter(pattern);
		FileFilter fileFilter = new RegexFileFilter(pattern);
		File[] files = dir.listFiles(fileFilter);

		if (files.length > 0) {
			/** The newest file comes first **/
			Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
			theNewestFile = files[0];
		}

		return theNewestFile;
	}

	private void cp(File from, String to) throws IOException {
		// FileAccess.Copy(src, dest);
		log("cp " + from.toPath() + " to " + new File(to).toPath());
		Files.copy(from.toPath(), new File(to).toPath());

	}

	public void run(String remote, String password, String localPath,
			String pattern, String dateFormat) throws IOException {
		File f = getTheNewestFileByPattern(localPath, pattern);
		if (f.isFile()) {
			
			do {
				
				String local = getNewFilename(f, pattern, dateFormat);
				if (local == null) {
					log("Done.");
					break;
				} else {
					String fileName =f.getPath() + File.separator + local; 
					cp(f, fileName);
					int res = rsync(remote, password, local);
					log("rsync result=" + res);
					if (res != 0)
						break;
					f = new File(fileName);
				}
				
			} while (true);
		}
	}

	protected String getNewFilename(File f, String pattern, String dateFormat) {
		String oldName = f.getName();

		// System.out.println(oldName);

		Pattern ptn = Pattern.compile(pattern);
		Matcher m = ptn.matcher(oldName);
		m.matches();
		m.find();
		String oldDate = m.group(0);

		// System.out.println(oldDate);

		int year = Integer.parseInt(oldDate.substring(0, 4));
		int month = Integer.parseInt(oldDate.substring(4, 6));
		int date = Integer.parseInt(oldDate.substring(6, 8));
		Calendar cal = Calendar.getInstance();
		Calendar today = Calendar.getInstance();
		cal.set(year, month - 1, date);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		if (cal.after(today)) {
			return null;
		} else {
			SimpleDateFormat sf = new SimpleDateFormat(dateFormat);
			String newDate = sf.format(cal.getTime());
			return oldName.replaceFirst(pattern, newDate);
		}
	}

	protected int rsync(String remote, String password, String local)
			throws IOException {
		String command = "echo " + password + " | rsync " + remote + " "
				+ local;
		log(command);

		String[] cmd = { "/bin/sh", "-c", command };

		Process p = Runtime.getRuntime().exec(cmd);

		int result = p.exitValue();

		String stdout = IOUtils.toString(p.getInputStream());
		String stderr = IOUtils.toString(p.getErrorStream());

		log(stdout);

		if (stderr != null && stderr.length() > 0)
			System.err.println(stderr);

		return result;

	}

}
