package com.situacaocoleta.ws.client;

import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;

import com.situacaocoleta.recebersituacao.ObjectFactory;
import com.situacaocoleta.recebersituacao.SecuritySoapHeaders;
import com.situacaocoleta.recebersituacao.TFlightsResponse;
import com.situacaocoleta.recebersituacao.TListFlights;

@Component
public class ReceberSituacaoClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReceberSituacaoClient.class);

  @Autowired
  private WebServiceTemplate webServiceTemplate;

  @SuppressWarnings("unchecked")
  public List<String> listFlights() {
    ObjectFactory factory = new ObjectFactory();
    TListFlights tListFlights = factory.createTListFlights();

    JAXBElement<TListFlights> request = factory.createSituacaoColetaRequest(tListFlights);

    JAXBElement<TFlightsResponse> response = (JAXBElement<TFlightsResponse>) webServiceTemplate
        .marshalSendAndReceive(request, new WebServiceMessageCallback() {

          @Override
          public void doWithMessage(WebServiceMessage message) {
            try {
              // get the header from the SOAP message
              SoapHeader soapHeader = ((SoapMessage) message).getSoapHeader();

              // create the header element
              ObjectFactory factory = new ObjectFactory();
              SecuritySoapHeaders listFlightsSoapHeaders =
                  factory.createSecuritySoapHeaders();

              JAXBElement<SecuritySoapHeaders> headers =
                  factory.createSecurity(listFlightsSoapHeaders);

              // create a marshaller
              JAXBContext context = JAXBContext.newInstance(SecuritySoapHeaders.class);
              Marshaller marshaller = context.createMarshaller();

              // marshal the headers into the specified result
              marshaller.marshal(headers, soapHeader.getResult());
            } catch (Exception e) {
              LOGGER.error("error during marshalling of the SOAP headers", e);
            }
          }
        });

    return response.getValue().getCodigo();
  }
}
