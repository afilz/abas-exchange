/******************************************************************************
  Author             : Andreas.Filz@abas.de
  Date of Creation   : 04.09.2020
  Name               : de.abas.abex.Util
  Arbeitspaket       : Final Exam
  Function           : Contains Misc Functions
*****************************************************************************/
package de.abas.abex;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.abas.eks.jfop.remote.FOe;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.exception.DBRuntimeException;
import de.abas.erp.db.util.ContextHelper;

public class Util {

	private static final Util util = new Util();

	private Logger logger = Logger.getLogger(Util.class);

	private Util() {
	}

	public static Util getInstance() {
		return util;
	}

	/**
	 * Returns a localized ResourceBundle according.
	 * @return
	 */
	public ResourceBundle getLanguageBundle() {
		Locale locale = FOe.getFOPSessionContext().getOperatingLangLocale();
		ClassLoader loader = getClass().getClassLoader();

		return ResourceBundle.getBundle("abas-exchange", locale, loader);
	}

	/**
	 * Creates and returns a dbContext for the daemon.
	 * @return
	 */
	public DbContext getDbContext() {
		DbContext dbContext = null;

		Properties properties = new Properties();

		String host = null;
		int port = 0;
		String mandant = null;
		String password = null;
		String appName = null;

		try {
			properties.load(new FileInputStream(
					getClass().getClassLoader().getResource("").getPath() + "application.properties"));

			port = Integer.parseInt(properties.getProperty("PORT"));

			host = properties.getProperty("HOST");
			mandant = properties.getProperty("MANDANT");
			password = properties.getProperty("PASSWORD");
			appName = properties.getProperty("APPNAME");

			dbContext = ContextHelper.createClientContext(host, port, mandant, password, appName);

		} catch (NumberFormatException e) {
			logger.log(Level.ERROR, "Property set as PORT is not a number");
			System.exit(1);
		} catch (FileNotFoundException e) {
			logger.log(Level.ERROR, e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			logger.log(Level.ERROR, e.getMessage());
			System.exit(1);
		} catch (DBRuntimeException e) {
			logger.log(Level.ERROR, e.getMessage());
			System.exit(1);
		}
		return dbContext;
	}
}
