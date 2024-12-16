package com.cs.on.icamera.cctv.util;


import com.cs.on.icamera.cctv.model.OnvifAuth;
import jakarta.xml.soap.*;

import javax.xml.namespace.QName;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class Token {
	private static final String WSS_TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	private static String created() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern(WSS_TIMESTAMP_FORMAT));
	}

	private static String expires() {
		return LocalDateTime.now().plusMinutes(15).format(DateTimeFormatter.ofPattern(WSS_TIMESTAMP_FORMAT));
	}

	private static String generateNonce() {
		SecureRandom random = new SecureRandom();
		byte[] nonce = new byte[16];  // 16-byte random nonce
		random.nextBytes(nonce);
		return Base64.getEncoder().encodeToString(nonce);
	}

	private static String generatePasswordDigest(String nonce, String created, String password) throws NoSuchAlgorithmException {
		String toHash = nonce + created + password;
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		byte[] hash = digest.digest(toHash.getBytes());
		return Base64.getEncoder().encodeToString(hash);
	}

	public static OnvifAuth generate(String username, String password) throws NoSuchAlgorithmException {
		String nonce = generateNonce();
		String create = created();
		String expire = expires();
		String passwordDigest = generatePasswordDigest(nonce, create, password);

		return new OnvifAuth.Builder().username(username).password(passwordDigest).nonce(nonce).created(create)
				.expires(expire).build();
	}

	public static SOAPMessage buildSoapRequest(OnvifAuth auth) throws SOAPException {
		// Create SOAP Message
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		// Create the SOAP Envelope
		SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();
		SOAPHeader header = envelope.getHeader();
		SOAPBody body = envelope.getBody();

		// Create the security header with username token
		String ns = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
		header.addNamespaceDeclaration("wsu", ns);

		// Security header
		SOAPElement security = header.addChildElement("Security", "wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd").addAttribute(
				javax.xml.namespace.QName.valueOf("SOAP-ENV:mustUnderstand"), "1");

		// Username Token element
		SOAPElement usernameToken = security.addChildElement("UsernameToken", "wsse");
		usernameToken.addChildElement("Nonce", "wsse").addTextNode(auth.getNonce());
		usernameToken.addChildElement("Created", "wsu").addTextNode(auth.getCreated());
		usernameToken.addChildElement("Username", "wsse").addTextNode(auth.getUsername());
		usernameToken.addChildElement("Password", "wsse").addAttribute(QName.valueOf("Type"), "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd#PasswordDigest").addTextNode(auth.getPassword());

		// Add an empty body (this is a placeholder for the actual ONVIF request)
		body.addChildElement("GetProfiles");

		soapMessage.saveChanges();

		return soapMessage;
	}


}
