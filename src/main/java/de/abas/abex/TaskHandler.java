/******************************************************************************
  Author             : Andreas.Filz@abas.de
  Date of Creation   : 04.09.2020
  Name               : de.abas.abex.TaskHandler
  Arbeitspaket       : Final Exam
  Function           : Contains the functionality to export abas ERP tasks into the users calendar  
*****************************************************************************/
package de.abas.abex;

import java.util.LinkedList;

import com.microsoft.graph.models.extensions.Attendee;
import com.microsoft.graph.models.extensions.DateTimeTimeZone;
import com.microsoft.graph.models.extensions.EmailAddress;
import com.microsoft.graph.models.extensions.Event;
import com.microsoft.graph.models.extensions.ItemBody;
import com.microsoft.graph.models.extensions.Location;
import com.microsoft.graph.models.generated.AttendeeType;
import com.microsoft.graph.models.generated.BodyType;

import de.abas.abex.erp.CompanyDataDAO;
import de.abas.abex.exchange.Credentials;
import de.abas.abex.exchange.Exchange;
import de.abas.erp.api.session.OperatorInformation;
import de.abas.erp.common.type.AbasDate;
import de.abas.erp.common.type.AbasTime;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.schema.company.CompanyData;
import de.abas.erp.db.schema.transaction.TaskHeader;

public class TaskHandler {

	private DbContext dbContext;

	public TaskHandler(DbContext dbContext) {
		this.dbContext = dbContext;
	}

	/**
	 * Creates and exports a Exchange event from a given abas event
	 * @param abasEvent
	 * @throws Exception
	 */
	public void exportTask(TaskHeader abasEvent) throws Exception {

		OperatorInformation opInfo = new OperatorInformation(dbContext);

		Event event = new Event();

		event.subject = abasEvent.getDescrOperLang();

		ItemBody body = new ItemBody();
		body.contentType = BodyType.HTML;
		body.content = abasEvent.getDescrTextModuleOperLang();
		event.body = body;

		AbasDate abasDate = abasEvent.getStartDateProcess();
		AbasTime abasTime = abasEvent.getStartingTime();
		DateTimeTimeZone start = new DateTimeTimeZone();
		start.dateTime = Converter.abasDateTimeToISO8601(abasDate, abasTime);
		start.timeZone = "Europe/Berlin";
		event.start = start;

		abasDate = abasEvent.getEndDate();
		abasTime = abasEvent.getEndTime();
		DateTimeTimeZone end = new DateTimeTimeZone();
		end.dateTime = Converter.abasDateTimeToISO8601(abasDate, abasTime);
		end.timeZone = "Europe/Berlin";
		event.end = end;

		Location location = new Location();
		location.displayName = abasEvent.getBusinessPartnerExt().getDescrOperLang();
		event.location = location;

		LinkedList<Attendee> attendeesList = new LinkedList<Attendee>();
		Attendee attendees = new Attendee();
		EmailAddress emailAddress = new EmailAddress();
		emailAddress.address = abasEvent.getEditorExt().getEmailAddr();
		emailAddress.name = abasEvent.getEditorExt().getAddr();
		attendees.emailAddress = emailAddress;
		attendees.type = AttendeeType.REQUIRED;
		attendeesList.add(attendees);

		if (!emailAddress.address.equals(opInfo.getEmailAddr())) {
			attendees = new Attendee();
			emailAddress = new EmailAddress();
			emailAddress.address = opInfo.getEmailAddr();
			emailAddress.name = opInfo.getExternName();
			attendees.emailAddress = emailAddress;
			attendees.type = AttendeeType.REQUIRED;
			attendeesList.add(attendees);
		}
		event.attendees = attendeesList;
		event.allowNewTimeProposals = true;

		executeExport(event);
	}

	/**
	 * Executes the export from a Exchange Event to the server
	 * @param event
	 * @throws Exception
	 */
	private void executeExport(Event event) throws Exception {
		CompanyDataDAO dao = new CompanyDataDAO();

		CompanyData cData = dao.getCompanyData(dbContext);

		Credentials credentials = new Credentials();

		credentials.setAuthority(cData.getYexchgauth());
		credentials.setClientId(cData.getYexchgclientid());
		credentials.setSecret(cData.getYexchgsecret());

		Exchange exchangeConnection = new Exchange();
		exchangeConnection.doAuthentication(credentials);

		String userPrincipalName = new OperatorInformation(dbContext).getEmailAddr();

		exchangeConnection.exportEvent(event, userPrincipalName);
	}
}
