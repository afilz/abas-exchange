package de.abas.abex.exchange;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.models.extensions.Contact;
import com.microsoft.graph.models.extensions.Event;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.models.extensions.User;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.extensions.GraphServiceClient;
import com.microsoft.graph.requests.extensions.IContactCollectionPage;

public class Exchange {
	private ResourceBundle config = null;
	private Logger logger = Logger.getLogger(Exchange.class);
	private AuthenticationProvider authProvider;

	public Exchange() {
		config = ResourceBundle.getBundle("application", Locale.ROOT);
	}

	public void doAuthentication(Credentials credentials) throws AuthenticationException {
		try {
			String authority = config.getString("AUTHORITY_BASE_URL") + credentials.getAuthority() + "/";
			String scope = config.getString("SCOPE");
			String clientId = credentials.getClientId();
			String secret = credentials.getSecret();

			IAuthenticationResult auth = Authentication.getAccessTokenBySecret(clientId, secret, authority, scope);
			authProvider = new AuthenticationProvider(auth.accessToken());
		} catch (Exception e) {
			logger.log(Level.ERROR, e.getMessage());
			throw new AuthenticationException();
		}
	}

	/**
	 * 
	 * @param contact
	 * @param userPrincipalName
	 * @return
	 */
	public void exportContact(Contact contact, String userPrincipalName) {
		IGraphServiceClient graphClient = GraphServiceClient.builder().authenticationProvider(authProvider)
				.buildClient();

		graphClient.users(userPrincipalName).contacts().buildRequest().post(contact);
	}

	/**
	 * 
	 * @param contact
	 * @param userPrincipalName
	 * @return
	 */
	public void updateContact(Contact contact, String userPrincipalName) {
		IGraphServiceClient graphClient = GraphServiceClient.builder().authenticationProvider(authProvider)
				.buildClient();

		graphClient.users(userPrincipalName).contacts(contact.id).buildRequest().patch(contact);
	}

	/**
	 * 
	 * @param contact
	 * @return
	 * @throws Exception
	 */
	public boolean deleteContact(Contact contact) throws Exception {
		IGraphServiceClient graphClient = GraphServiceClient.builder().authenticationProvider(authProvider)
				.buildClient();

		try {
			List<User> users = graphClient.users().buildRequest().get().getCurrentPage();
			for (User user : users) {
				String id = getContactId(contact, user.userPrincipalName);

				if (id != null) {
					graphClient.users(user.userPrincipalName).contacts(id).buildRequest().delete();
				}
			}
		} catch (ClientException e) {
			logger.log(Level.ERROR, e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param contact
	 * @param userPrincipalName
	 * @return Returns either the MS-Exchange-ID for a given contact, or null, if no
	 *         contact with the given criteria was found.
	 * @throws Exception
	 */
	public String getContactId(Contact contact, String userPrincipalName) throws Exception {
		try {
			List<Option> requestOptions = new ArrayList<Option>();
			requestOptions.add(new QueryOption("$filter", "(nickname eq '" + contact.nickName + "')"));

			IGraphServiceClient graphClient = GraphServiceClient.builder().authenticationProvider(authProvider)
					.buildClient();
			IContactCollectionPage contactCollection = graphClient.users(userPrincipalName).contacts()
					.buildRequest(requestOptions).get();
			Iterator<Contact> it = contactCollection.getCurrentPage().iterator();

			if (it.hasNext()) {
				return it.next().id;
			} else {
				return null;
			}
		} catch (ClientException e) {
			if (e.getMessage().contains("ErrorInvalidUser")) {
				throw new Exception("ERROR_INVALID_USER");
			}
		}
		return null;
	}

	public void exportEvent(Event event, String userPrincipalName) throws Exception {
		IGraphServiceClient graphClient = GraphServiceClient.builder().authenticationProvider(authProvider)
				.buildClient();

		try {
			graphClient.users(userPrincipalName).calendar().events().buildRequest().post(event);
		} catch (ClientException e) {
			if (e.getMessage().contains("ErrorInvalidUser")) {
				throw new Exception("ERROR_INVALID_USER");
			}
		}
	}

}
