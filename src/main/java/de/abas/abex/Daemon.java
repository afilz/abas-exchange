/******************************************************************************
  Author             : Andreas.Filz@abas.de
  Date of Creation   : 04.09.2020
  Name               : de.abas.abex.Daemon
  Arbeitspaket       : Final Exam
  Function           : Class which should be called by a Cronjob  
*****************************************************************************/
package de.abas.abex;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import de.abas.abex.erp.CompanyDataDAO;
import de.abas.abex.erp.ContactsDAO;
import de.abas.abex.exchange.Credentials;
import de.abas.abex.exchange.Exchange;
import de.abas.abex.exchange.ExchangeContact;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.schema.company.CompanyData;
import de.abas.erp.db.schema.customer.CustomerContact;
import de.abas.erp.db.schema.customer.ProspectContact;
import de.abas.erp.db.schema.vendor.VendorContact;

public class Daemon {

	private Logger logger;
	private Exchange exchange;
	private DbContext dbContext;

	/**
	 * 
	 */
	public Daemon() {
		init();
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new Daemon().run();
	}

	/**
	 * Created the connections to abas ERP and exchange
	 */
	private void init() {
		try {
			logger = Logger.getLogger(this.getClass());
			Properties properties = new Properties();
			properties.load(new FileInputStream(
					getClass().getClassLoader().getResource("").getPath() + "logging.custom.properties"));

			PropertyConfigurator.configure(properties);
			logger.info("Logging has started");

			dbContext = Util.getInstance().getDbContext();

			CompanyDataDAO dao = new CompanyDataDAO();
			CompanyData cData = dao.getCompanyData(dbContext);

			Credentials credentials = new Credentials();
			credentials.setAuthority(cData.getYexchgauth());
			credentials.setClientId(cData.getYexchgclientid());
			credentials.setSecret(cData.getYexchgsecret());

			Exchange exchangeConnection = new Exchange();
			exchangeConnection.doAuthentication(credentials);
		} catch (Exception e) {
			logger.log(Level.ERROR, e.getMessage());
		}
	}

	private void run() {
		deleteContacts();
		//add more if needed
	}

	/**
	 * Function to delete deactivated abas ERP contacts from all exchange Email Accounts
	 */
	private void deleteContacts() {
		ContactsDAO dao = new ContactsDAO(dbContext);

		logger.info("Deleting from contacts has started");
		try {
			for (CustomerContact cContact : dao.getCustomersContactList(true)) {
				ExchangeContact contact = new ExchangeContact();
				contact.nickName = ExchangeContact.PREFIX_CUSTOMER + cContact.getIdno().toString();

				if (exchange.deleteContact(contact)) {
					dao.confirmCustomerContactExported(cContact);
				}
			}
			for (ProspectContact cContact : dao.getProspectsContactList(true)) {
				ExchangeContact contact = new ExchangeContact();
				contact.nickName = ExchangeContact.PREFIX_CUSTOMER + cContact.getIdno().toString();

				if (exchange.deleteContact(contact)) {
					dao.confirmProspectContactExported(cContact);
				}
			}
			for (VendorContact cContact : dao.getVendorsContactList(true)) {
				ExchangeContact contact = new ExchangeContact();
				contact.nickName = ExchangeContact.PREFIX_CUSTOMER + cContact.getIdno().toString();

				if (exchange.deleteContact(contact)) {
					dao.confirmVendorContactExported(cContact);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		logger.info("Deleting from contacts has ended");
	}
}