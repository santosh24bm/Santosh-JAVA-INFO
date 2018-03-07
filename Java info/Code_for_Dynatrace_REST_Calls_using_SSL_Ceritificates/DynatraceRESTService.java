package cgt.dop.dynatrace.service;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

import cgt.dop.dynatrace.bean.Dashboardreport;
import cgt.dop.dynatrace.bean.Data;
import cgt.dop.dynatrace.bean.DynatraceBean;
import cgt.dop.dynatrace.bean.Taggedwebrequest;
import cgt.dop.dynatrace.bean.Taggedwebrequests;
import cgt.dop.dynatrace.bean.Taggedwebrequestsdashlet;

public class DynatraceRESTService {
	
	private static final String SSL_PROTOCOL = "https";
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin";
	private static final String COLON = ":";
	private static final String PROTOCOL = "SSL";
	private static final String CONTENT_TYPE = "application/json";
	private static final String AUTH_TYPE = "Basic ";
	private static final String AUTHORIZATION = "Authorization";
	public static final String ENCODING_FORMAT = "UTF-8";
	
	//public static final String URL = "https://172.16.28.51:8021";
	
	//public static final String URL = "https://172.16.28.51:8021/rest/management/dashboard/Cigniti_Demo?purePathDetails=ALL";
	
	//public static final String URL = "https://172.16.28.51:8021/rest/management/reports/create/Cigniti_Testing?type=XML&format=XML+Export";
	
	public static final String URL = "https://172.16.28.51:8021/rest/management/reports/create/Cigniti_Testing?filter=tf%3ALast7d&type=XML&format=XML+Export";
	//public static final String URL = "https://172.16.28.51:8021/rest/management/reports/create/Cigniti_Demo";
	//public static final String URL = "https://localhost:8021/rest/management/profiles.json";
	//public static final String URL = "https://172.16.28.51:9911/index.jsp#pure-paths";
	//http://localhost:8030/rest/integration/openpurepath
	static {
		try {
			TrustManager[] trustAllCerts = { new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} };
			SSLContext sc = SSLContext.getInstance(PROTOCOL);

			HostnameVerifier hv = new HostnameVerifier() {
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}
			};
			sc.init(null, trustAllCerts, new SecureRandom());

			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(hv);
		} catch (Exception localException) {
		}
	}
	
	public static void main(String args[]) {
		String xmlStringResp = null;
		try {
			
			URL url = new URL(URL);
			
			if(SSL_PROTOCOL.equals(url.getProtocol())){
			
			xmlStringResp = getDynatraceRESTService(URL,USERNAME, PASSWORD);
			System.out.println(xmlStringResp);
			
			JAXBContext jaxbContext = JAXBContext.newInstance(Dashboardreport.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			//JSONObject obj=new JSONObject(xmlStringResp);
			JSONObject xmlJSONObj = XML.toJSONObject(xmlStringResp);
            String jsonPrettyPrintString = xmlJSONObj.toString(4);
            System.out.println(jsonPrettyPrintString);
			StringReader reader = new StringReader(xmlStringResp);
			Dashboardreport dashboardreport = (Dashboardreport) unmarshaller.unmarshal(reader);
			ObjectMapper mapper = new ObjectMapper();
			DynatraceBean dashboardreport1 = mapper.readValue(xmlJSONObj.toString(), DynatraceBean.class);
			System.out.println("dashboardreport->"+dashboardreport1.getDashboardreport().getData().getTaggedwebrequestsdashlet().getTaggedwebrequests().getTaggedwebrequest());
			
			//Dashboardreport dashboardreport = JAXB.unmarshal(new StringReader(xmlStringResp), Dashboardreport.class);
			
			
			
			Taggedwebrequest[] taggedwebrequestList = dashboardreport1.getDashboardreport().getData().getTaggedwebrequestsdashlet().getTaggedwebrequests().getTaggedwebrequest();
			
			System.out.println("TimerName"+"\t"+"Count"+"\t"+"Total Avg [ms]"+"\t"+"Total Sum [ms]"+"\t"+"CPU Avg [ms]"+"\t"+"CPU Sum [ms]");
			
			for(Taggedwebrequest Taggedwebrequest:taggedwebrequestList){
				System.out.println(Taggedwebrequest.getTimername()+"\t"+Taggedwebrequest.getCount()+"\t"+Taggedwebrequest.getAvg()+"\t"
						+Taggedwebrequest.getSum()+"\t"+Taggedwebrequest.getCpu_avg()+"\t"+Taggedwebrequest.getCpu_sum());
			}
			
			}else{
				String httpResponse = getDynatraceConnection(URL,USERNAME, PASSWORD);
				System.out.println(httpResponse);
			}
			
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	
	
	public static String getDynatraceRESTService(String url,String userName,String password)
	{
		String jsonResponse = null;
		try {

			DefaultClientConfig defaultClientConfig = new DefaultClientConfig();
			SSLContext ctx = SSLContext.getInstance(PROTOCOL);
			ServerTrustManager serverTrustManager = new ServerTrustManager();
			ctx.init(null, new TrustManager[] { serverTrustManager }, null);
			defaultClientConfig.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
					new HTTPSProperties(null, ctx));
			Client client = Client.create(defaultClientConfig);

			byte[] message = (userName + COLON + password).getBytes(ENCODING_FORMAT);
			String auth = DatatypeConverter.printBase64Binary(message);

			WebResource webResource = client.resource(url);
			System.out.println(url);
			ClientResponse response = webResource.header(AUTHORIZATION, AUTH_TYPE + auth).type(CONTENT_TYPE)
					.accept(CONTENT_TYPE).get(ClientResponse.class);
			System.out.println("Retrieving response:->" + response);

			jsonResponse = response.getEntity(String.class);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonResponse;
	}
	
	
	/**
	 * This method will return Dynatrace connection for specific user. 
	 * 
	 * @param url
	 * @param userName
	 * @param password
	 * @return
	 */
	public static String getDynatraceConnection(String userName, String password, String url) {
		String httpResponse=null;
		Client client = null;
		WebResource webResource = null;
		try {
			byte[] message = (userName + COLON + password).getBytes(ENCODING_FORMAT);
			String auth = DatatypeConverter.printBase64Binary(message);

			client = Client.create();
			webResource = client.resource(url);
			System.out.println(url);
			ClientResponse response = webResource.header("Authorization", "Basic " + auth).type(CONTENT_TYPE)
					.accept(CONTENT_TYPE).get(ClientResponse.class);
			httpResponse = response.getEntity(String.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return httpResponse;
	}
	
	private static String convertDocumentToString(Document doc) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            // below code to remove XML declaration
            // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String output = writer.getBuffer().toString();
            return output;
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        
        return null;
    }
	
	private static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        DocumentBuilder builder;  
        try  
        {  
            builder = factory.newDocumentBuilder();  
            Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) ); 
            return doc;
        } catch (Exception e) {  
            e.printStackTrace();  
        } 
        return null;
    }
}
