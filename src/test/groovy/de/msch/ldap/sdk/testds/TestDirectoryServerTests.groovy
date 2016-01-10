package de.msch.ldap.sdk.testds
import spock.lang.*

import com.unboundid.ldap.sdk.Entry
import com.unboundid.ldap.sdk.LDAPConnection
import com.unboundid.ldap.sdk.SearchScope

class TestDirectoryServerTests extends Specification {

	def "some simple searches"() {

		setup:
		TestDirectoryServer testDs = new TestDirectoryServer()
		LDAPConnection conn = new LDAPConnection('localhost', testDs.listenPort)

		when:
		def entryHornblower = searchPirate('Horatio Hornblower', conn)
		def entryLeChuck = searchPirate('LeChuck', conn)

		then:
		entryHornblower != null
		entryHornblower.getAttributeValue('givenname') == 'Horatio'

		entryLeChuck == null
	}


	def "a simple add"() {

		setup:
		TestDirectoryServer testDs = new TestDirectoryServer()
		LDAPConnection conn = new LDAPConnection('localhost', testDs.listenPort)

		when:
		def noLeChuck = searchPirate('LeChuck', conn)
		addLeChuck(conn)
		def leChuck = searchPirate('LeChuck', conn)

		then:
		noLeChuck == null
		leChuck != null
	}


	def "reset test directory"() {

		setup:
		TestDirectoryServer testDs = new TestDirectoryServer()
		LDAPConnection conn = new LDAPConnection('localhost', testDs.listenPort)

		when:
		def initialContent = conn.search('o=sevenSeas', SearchScope.SUB, '(objectClass=*)')
		addLeChuck(conn)
		def changedContent = conn.search('o=sevenSeas', SearchScope.SUB, '(objectClass=*)')
		testDs.reset()
		def resetContent = conn.search('o=sevenSeas', SearchScope.SUB, '(objectClass=*)')

		then:
		initialContent.searchEntries != changedContent.searchEntries
		initialContent.searchEntries == resetContent.searchEntries
	}


	private Entry searchPirate(String rdn, LDAPConnection conn) {
		conn.searchForEntry("cn=$rdn,ou=people,o=sevenSeas", SearchScope.BASE, '(objectClass=*)')
	}


	private addLeChuck(LDAPConnection conn) {
		conn.add 'dn: cn=LeChuck,ou=people,o=sevenSeas',
				'objectclass: person',
				'objectclass: inetOrgPerson',
				'objectclass: top',
				'cn: LeChuck',
				'description: Undead pirate and Guybrushs arch-enemy',
				'sn: LeChuck'
	}
}
