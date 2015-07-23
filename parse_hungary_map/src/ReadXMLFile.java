import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ReadXMLFile {

   public static void main(String argv[]) {

	   try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			DefaultHandler handler = new DefaultHandler() {

				boolean getNameAttr = false;

				@Override
                public void startElement(String uri, String localName,String qName,
			                Attributes attributes) throws SAXException {

					if (qName.equalsIgnoreCase("node")) {

						getNameAttr = false;

						System.out.print("NODE       ");
						System.out.print("id: " + attributes.getValue("id"));
						System.out.print(" lat: " + attributes.getValue("lat"));
						System.out.print(" lon: " + attributes.getValue("lon"));
						System.out.println(" changeset: " + attributes.getValue("changeset"));
					}
					else if (qName.equalsIgnoreCase("tag") && attributes.getValue("v").equals("parking")) {
						System.out.print("NODE TAG   ");
						System.out.println("parking");
					}
					else if (qName.equalsIgnoreCase("way")) {
						System.out.print("WAY        ");
						getNameAttr = true;						System.out.print("id: " + attributes.getValue("id"));
						System.out.println(" changeset: " + attributes.getValue("changeset"));					}
					else if (qName.equalsIgnoreCase("nd")) {						System.out.print("WAY ND     ");
						System.out.println("ref: " + attributes.getValue("ref"));
					}
					else if (qName.equalsIgnoreCase("tag") && attributes.getValue("k").equals("name") && getNameAttr) {
						System.out.print("WAY TAG    ");
						System.out.println("street: " + attributes.getValue("v"));
					}
				}
		    };

		    saxParser.parse("d:\\Programs/repo/parse_hungary_map/hungary_map/hungary-latest.osm", handler);

	     } catch (Exception e) {
	    	 e.printStackTrace();
	     }

   }

}
