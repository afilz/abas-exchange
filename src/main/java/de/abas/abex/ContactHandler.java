/******************************************************************************
  Author             : Andreas.Filz@abas.de
  Date of Creation   : 04.09.2020
  Name               : de.abas.abex.ContactHandler
  Arbeitspaket       : Final Exam
  Function           : Layer between abas ERP and MS Exchange
*****************************************************************************/
package de.abas.abex;

import java.util.LinkedList;
import com.microsoft.graph.models.extensions.EmailAddress;

import de.abas.abex.erp.CompanyDataDAO;
import de.abas.abex.exchange.Credentials;
import de.abas.abex.exchange.Exchange;
import de.abas.abex.exchange.ExchangeContact;
import de.abas.erp.api.session.OperatorInformation;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.schema.company.CompanyData;
import de.abas.erp.db.schema.customer.Customer;
import de.abas.erp.db.schema.customer.CustomerContact;
import de.abas.erp.db.schema.customer.Prospect;
import de.abas.erp.db.schema.customer.ProspectContact;
import de.abas.erp.db.schema.vendor.Vendor;
import de.abas.erp.db.schema.vendor.VendorContact;


public class ContactHandler {

	private DbContext dbContext;

	
	public ContactHandler(DbContext dbContext) {
		this.dbContext = dbContext;
	}

	
	/**
	 * Converts CustomerContact to a ExchangeContact
	 * @param customerContact The abas customer contact you'd like to export.
	 * @throws Exception Feedback to the GUI in case something went wrong.
	 */
	public void exportContact(CustomerContact customerContact) throws Exception {
		ExchangeContact contact = new ExchangeContact();

		String name[] = customerContact.getContactPerson().split(" ", 2);
		if (name.length > 0) {
			contact.givenName = name[0];
			if (name.length > 1) {
				contact.surname = name[1];
			}
		}

		contact.companyName = ((Customer) customerContact.getCompanyARAPDescr()).getDescrOperLang();
		contact.businessHomePage = customerContact.getWebSiteURL();

		LinkedList<EmailAddress> emailAddressesList = new LinkedList<EmailAddress>();
		EmailAddress emailAddress = new EmailAddress();
		emailAddress.name = customerContact.getContactPerson();
		emailAddress.address = customerContact.getEmailAddr();
		emailAddressesList.add(emailAddress);

		if (!customerContact.getEmailAddr2().isEmpty()) {
			emailAddress = new EmailAddress();
			emailAddress.name = customerContact.getContactPerson();
			emailAddress.address = customerContact.getEmailAddr2();
			emailAddressesList.add(emailAddress);
		}

		LinkedList<String> businessPhoneList = new LinkedList<String>();
		businessPhoneList.add(customerContact.getPhoneNo());
		businessPhoneList.add(customerContact.getPhoneNo2());
		contact.businessPhones = businessPhoneList;

		contact.nickName = ExchangeContact.PREFIX_CUSTOMER + customerContact.getIdno().toString();

		exportContact(contact);
	}

	/**
	 * Converts ProspectContact to a ExchangeContact
	 * @param prospectContact The abas prospect contact you'd like to export.
	 * @throws Exception Feedback to the GUI in case something went wrong.
	 */
	public void exportContact(ProspectContact prospectContact) throws Exception {
		ExchangeContact contact = new ExchangeContact();

		String name[] = prospectContact.getContactPerson().split(" ", 2);
		if (name.length > 0) {
			contact.givenName = name[0];
			if (name.length > 1) {
				contact.surname = name[1];
			}
		}

		contact.companyName = ((Prospect) prospectContact.getCompanyARAPDescr()).getDescrOperLang();
		contact.businessHomePage = prospectContact.getWebSiteURL();

		LinkedList<EmailAddress> emailAddressesList = new LinkedList<EmailAddress>();
		EmailAddress emailAddress = new EmailAddress();
		emailAddress.name = prospectContact.getContactPerson();
		emailAddress.address = prospectContact.getEmailAddr();
		emailAddressesList.add(emailAddress);

		if (!prospectContact.getEmailAddr2().isEmpty()) {
			emailAddress = new EmailAddress();
			emailAddress.name = prospectContact.getContactPerson();
			emailAddress.address = prospectContact.getEmailAddr2();
			emailAddressesList.add(emailAddress);
		}

		LinkedList<String> businessPhoneList = new LinkedList<String>();
		businessPhoneList.add(prospectContact.getPhoneNo());
		businessPhoneList.add(prospectContact.getPhoneNo2());
		contact.businessPhones = businessPhoneList;

		contact.nickName = ExchangeContact.PREFIX_PROSPECT + prospectContact.getIdno().toString();

		exportContact(contact);
	}

	/**
	 * Converts VendorContact to a ExchangeContact
	 * @param vendorContact The abas vendor contact you'd like to export.
	 * @throws Exception Feedback to the GUI in case something went wrong.
	 */
	public void exportContact(VendorContact vendorContact) throws Exception {
		ExchangeContact contact = new ExchangeContact();

		String name[] = vendorContact.getContactPerson().split(" ", 2);
		if (name.length > 0) {
			contact.givenName = name[0];
			if (name.length > 1) {
				contact.surname = name[1];
			}
		}

		contact.companyName = ((Vendor) vendorContact.getCompanyARAPDescr()).getDescrOperLang();
		contact.businessHomePage = vendorContact.getWebSiteURL();

		LinkedList<EmailAddress> emailAddressesList = new LinkedList<EmailAddress>();
		EmailAddress emailAddress = new EmailAddress();
		emailAddress.name = vendorContact.getContactPerson();
		emailAddress.address = vendorContact.getEmailAddr();
		emailAddressesList.add(emailAddress);

		if (!vendorContact.getEmailAddr2().isEmpty()) {
			emailAddress = new EmailAddress();
			emailAddress.name = vendorContact.getContactPerson();
			emailAddress.address = vendorContact.getEmailAddr2();
			emailAddressesList.add(emailAddress);
		}

		LinkedList<String> businessPhoneList = new LinkedList<String>();
		businessPhoneList.add(vendorContact.getPhoneNo());
		businessPhoneList.add(vendorContact.getPhoneNo2());
		contact.businessPhones = businessPhoneList;

		contact.nickName = ExchangeContact.PREFIX_VENDOR + vendorContact.getIdno().toString();

		exportContact(contact);
	}

	/**
	 * Function to either save or update a contact in MS Exchange 
	 * @param exportContact The MS-Exchange contact you'd like to export
	 * @throws Exception Feedback to the GUI in case something went wrong.
	 */
	private void exportContact(ExchangeContact contact) throws Exception {
		CompanyDataDAO dao = new CompanyDataDAO();

		CompanyData cData = dao.getCompanyData(dbContext);

		Credentials credentials = new Credentials();

		credentials.setAuthority(cData.getYexchgauth());
		credentials.setClientId(cData.getYexchgclientid());
		credentials.setSecret(cData.getYexchgsecret());

		Exchange exchangeConnection = new Exchange();
		exchangeConnection.doAuthentication(credentials);

		String userPrincipalName = new OperatorInformation(dbContext).getEmailAddr();
		contact.id = exchangeConnection.getContactId(contact, userPrincipalName);

		if (contact.id == null) {
			exchangeConnection.exportContact(contact, userPrincipalName);
		} else {
			exchangeConnection.updateContact(contact, userPrincipalName);
		}
	}
}
