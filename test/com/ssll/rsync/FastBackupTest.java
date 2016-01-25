package com.ssll.rsync;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class FastBackupTest {

	@Test
	public void testLastFileModified() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTheNewestFile() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTheNewestFileByPattern() {
		fail("Not yet implemented");
	}

	@Test
	public void testRun() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetNewFilename() {
		
		FastBackup fb = new FastBackup(true, System.out);
		
		String pattern="(\\d{8})";
		
		File f = new File("/home/xingwx/test/frsync/test-19990102.gz");
		
		String dateFormat ="yyyyMMdd";
		
		String res = fb.getNewFilename(f , pattern, dateFormat );
		
		assertEquals("test-19990103.gz", res);
		//System.out.println(res);
	}

	@Test
	public void testRsync() throws IOException {
		FastBackup fb = new FastBackup(true, System.out);
		String local  = "/home/xingwx/test/frsync/test-19990102.gz";
		File f = new File(local);
		int res = fb.rsync("user@192.168.1.18:/home/xingwx/test", "123456", local);
		
		
		    
		    
		System.out.println("rsync result = "+res);
	}

}
