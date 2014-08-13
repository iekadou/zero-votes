package com.zero.votes.ldap;

import com.zero.votes.persistence.OrganizerFacade;
import com.zero.votes.persistence.entities.Organizer;
import javax.ejb.EJB;
import javax.ejb.Stateless;
/**
 *
 * @author riediger
 */
@Stateless
public class LdapLogic {

	@EJB
	private OrganizerFacade of;

	public LdapUser lookupUser(String uid) {
            return UnikoLdapLookup.lookupPerson(uid);
	}

	public LdapUser getUser(String uid) {
            return getUserEntity(uid).createLdapUser();
	}

	private Organizer getUserEntity(String uid) {
            LdapUser ldapUser = lookupUser(uid);
            if (ldapUser == null) {
                    return null;
            }
            Organizer p = of.findByUsername(uid);
            if (p == null) {
                p = new Organizer();
                p.setUsername(ldapUser.getName());
                p.setForename(ldapUser.getFirstName());
                p.setSurname(ldapUser.getLastName());
                p.setEmail(ldapUser.getEmail());
                of.create(p);
                return p;
            } else {
                p.setUsername(ldapUser.getName());
                p.setForename(ldapUser.getFirstName());
                p.setSurname(ldapUser.getLastName());
                p.setEmail(ldapUser.getEmail());
                return of.edit(p);
            }
	}

}
