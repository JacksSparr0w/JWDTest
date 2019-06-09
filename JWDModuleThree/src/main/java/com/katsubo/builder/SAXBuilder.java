package com.katsubo.builder;

import com.katsubo.bean.Device;
import com.katsubo.bean.Group;
import com.katsubo.bean.Port;
import com.katsubo.bean.Type;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SAXBuilder extends Builder {
    @Override
    public void buildDevices() throws IOException, SAXException, ParserConfigurationException {
        SAXParserFactory factory;
        factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        DefaultHandler handler = new DefaultHandler() {
            Device device;
            Type type;
            List<Port> ports;

            String lastElement;
            @Override
            public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
                switch (qName){
                    case "device":
                        device = new Device();
                        device.setId(Integer.valueOf(atts.getValue("id")));
                        device.setCritical(Boolean.getBoolean(atts.getValue("critical")));

                        break;
                    case "type":
                        type = new Type();
                        type.setPeripheral(Boolean.getBoolean(atts.getValue("peripheral")));
                        type.setCooler(Boolean.getBoolean(atts.getValue("cooler")));

                        break;
                    case "ports":
                        ports = new ArrayList<>();

                }
                lastElement = qName;
            }

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                String data = new String(ch, start, length);
                switch (lastElement){
                    case "name":
                        device.setName(data);
                        break;
                    case "origin":
                        device.setOrigin(data);
                        break;
                    case "price":
                        device.setPrice(Integer.valueOf(data));
                        break;
                    case "deviceType":
                         type.setName(data);
                         break;
                    case "port":
                        ports.add(Port.of(data).get());
                        break;
                    case "energy":
                        type.setEnergyUse(Integer.valueOf(data));
                        break;
                    case "group":
                        type.setGroup(Group.of(data).get());
                        //break;
                }
                lastElement = "";
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                switch (qName){
                    case "type":
                        device.setType(type);
                        break;
                    case "device":
                        devices.add(device);
                        break;
                    case "ports":
                        type.setPorts(ports);
                }
            }
        };

        saxParser.parse("src/main/resources/data.xml", handler);
    }
}
