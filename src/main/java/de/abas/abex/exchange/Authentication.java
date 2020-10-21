package de.abas.abex.exchange;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;

/**
 * 
 * @author afilz
 *
 */
public final class Authentication {

	private Authentication() {
	}

	/**
	 * 
	 * @param clientId
	 * @param secret
	 * @param authority
	 * @param scope
	 * @return
	 * @throws MalformedURLException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public static IAuthenticationResult getAccessTokenBySecret(String clientId, String secret, String authority,
			String scope) throws MalformedURLException, InterruptedException, ExecutionException {

		ConfidentialClientApplication app = ConfidentialClientApplication
				.builder(clientId, ClientCredentialFactory.createFromSecret(secret)).authority(authority).build();

		ClientCredentialParameters clientCredentialParam = ClientCredentialParameters
				.builder(Collections.singleton(scope)).build();

		CompletableFuture<IAuthenticationResult> future = app.acquireToken(clientCredentialParam);
		return future.get();
	}

	/**
	 * 
	 * @param clientId
	 * @param keyPath
	 * @param certPath
	 * @param authority
	 * @param scope
	 * @return
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public static IAuthenticationResult getAccessTokenByCertificate(String clientId, String keyPath, String certPath,
			String authority, String scope) throws InvalidKeySpecException, NoSuchAlgorithmException,
			CertificateException, IOException, InterruptedException, ExecutionException {

		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Files.readAllBytes(Paths.get(keyPath)));
		PrivateKey key = KeyFactory.getInstance("RSA").generatePrivate(spec);

		InputStream certStream = new ByteArrayInputStream(Files.readAllBytes(Paths.get(certPath)));
		X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509")
				.generateCertificate(certStream);

		ConfidentialClientApplication app = ConfidentialClientApplication
				.builder(clientId, ClientCredentialFactory.createFromCertificate(key, cert)).authority(authority)
				.build();

		ClientCredentialParameters clientCredentialParam = ClientCredentialParameters
				.builder(Collections.singleton(scope)).build();

		CompletableFuture<IAuthenticationResult> future = app.acquireToken(clientCredentialParam);
		return future.get();
	}
}
