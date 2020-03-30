package com.situacaocoleta.ws.endpoint;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.server.endpoint.annotation.SoapHeader;

import com.situacaocoleta.recebersituacao.ObjectFactory;
import com.situacaocoleta.recebersituacao.SecuritySoapHeaders;
import com.situacaocoleta.recebersituacao.TFlightsResponse;
import com.situacaocoleta.recebersituacao.TListFlights;

@Endpoint
public class ReceberSituacaoEndpoint {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReceberSituacaoEndpoint.class);

	@SuppressWarnings("unchecked")
	@PayloadRoot(namespace = "http://situacaocoleta.com/ReceberSituacao.xsd", localPart = "situacaoColetaRequest")
	@ResponsePayload
	public JAXBElement<TFlightsResponse> listFlights(@RequestPayload JAXBElement<TListFlights> request,
			@SoapHeader(value = "{http://situacaocoleta.com/ReceberSituacao.xsd}usernameToken") SoapHeaderElement soapHeaderElement) {

		String username = "";
		String password = "";

		try {
			// create an unmarshaller
			JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();

			// unmarshal the header from the specified source
			JAXBElement<SecuritySoapHeaders> headers = (JAXBElement<SecuritySoapHeaders>) unmarshaller
					.unmarshal(soapHeaderElement.getSource());

			// get the header values
			SecuritySoapHeaders requestSoapHeaders = headers.getValue();
			username = requestSoapHeaders.getUsernameToken2().getUsuario();
			password = requestSoapHeaders.getUsernameToken2().getSenha();

		} catch (Exception e) {
			LOGGER.error("error during unmarshalling of the SOAP headers", e);
		}

		ObjectFactory factory = new ObjectFactory();
		TFlightsResponse response = factory.createTFlightsResponse();

		if ("usuario_xyz".equals(username) && "12345678".equals(password)) {
			response.getCodigo().add("200");
			response.getDescricao().add("Situação da coleta recebida com sucesso!");
			
			LOGGER.info(response.getCodigo().get(0).toString());
			LOGGER.info(response.getDescricao().get(0).toString());
			LOGGER.info(response.getTicket().get(0).toString());
			
		} else {
			response.getCodigo().add("422");
			response.getDescricao().add("Usuário ou senha inválido!");
		}

		return factory.createSituacaoColetaResponse(response);
	}
}
