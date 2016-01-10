package de.msch.ldap.sdk.testds

import com.unboundid.ldap.listener.InMemoryDirectoryServer
import com.unboundid.ldap.listener.InMemoryDirectoryServerSnapshot
import com.unboundid.ldif.LDIFReader

class TestDirectoryServer {

	private InMemoryDirectoryServer ds
	private InMemoryDirectoryServerSnapshot initialState

	public TestDirectoryServer() {
		ds = new InMemoryDirectoryServer('o=sevenSeas')

		ds.add	'dn: o=sevenSeas',
				'objectClass: top',
				'objectClass: organization',
				'o: sevenSeas'

		InputStream is = this.getClass().getClassLoader().getResourceAsStream("apache-ds-tutorial.ldif")
		ds.importFromLDIF(false, new LDIFReader(is))

		initialState = ds.createSnapshot()

		ds.startListening()
	}

	public int getListenPort() {
		ds.getListenPort()
	}

	public void reset() {
		ds.restoreSnapshot(initialState)
	}
}
