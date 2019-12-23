package kyLinkWaveTool.DataUnit;

import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
import java.util.Iterator;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
//import org.dom4j.DocumentHelper;
import org.dom4j.Element;
//import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
//import org.dom4j.io.XMLWriter;

final public class DataCollecter {
//	private String cfgFilePath = null;
	private File cfgFile = null;
	private ArrayList<kyLinkGroup> GroupList = null;
	public DataCollecter() {
		GroupList = new ArrayList<kyLinkGroup>();
//		String root_path = System.getProperty("user.dir");
//		root_path = root_path + File.separator + "cfgFile";
//		cfgFilePath = root_path + File.separator + "Packages.xml";
//		cfgFile = new File(cfgFilePath);
//		if(!cfgFile.getParentFile().exists()) {
//			cfgFile.getParentFile().mkdirs();
//		}
//		if(!cfgFile.exists()) {
//			cfgFile.createNewFile();
//			initPackList();
//			saveXML(initXML(), cfgFile);
//		} else {
//			readXML(cfgFile);
//		}
	}

	public boolean setConfigFile(String path) {
		File f = new File(path);
		if(f.getParentFile().exists() && f.exists()) {
			cfgFile = f;
			return true;
		}
		return false;
	}

	public ArrayList<kyLinkGroup> decode() throws DocumentException {
		if(cfgFile != null) {
			readXML(cfgFile);
		} else {
			GroupList.clear();
		}
		return GroupList;
	}

	public int getGroupIndexByName(String name) {
		if(GroupList != null) {
			for(kyLinkGroup g : GroupList) {
				if(g.groupName.equals(name)) {
					return GroupList.indexOf(g);
				}
			}
		}
		return -1;
	}

//	private void initPackList() {
//		/* add default packages */
//		kyLinkGroup group = null;
//		group = new kyLinkGroup("Heartbeat", "0x01");
//		group.addMember(new kyLinkMember("FrameIndex", "uint8_t"));
//		GroupList.add(group);
//		group = new kyLinkGroup("VersionReq", "0x02");
//		group.addMember(new kyLinkMember("VersionReq", "uint8_t"));
//		GroupList.add(group);
//		group = new kyLinkGroup("VersionAck", "0x03");
//		group.addMember(new kyLinkMember("Version", "uint16_t"));
//		GroupList.add(group);
//	}
//	private void addElement(Element RootElement, kyLinkGroup group) {
//		Element fTypeElement = RootElement.addElement("fType");
//		fTypeElement.addAttribute("name", group.groupName);
//		fTypeElement.addAttribute("id", group.groupId);
//		ArrayList<kyLinkMember> list = group.getMemberList();
//		for(kyLinkMember m : list) {
//			Element mbrElement = fTypeElement.addElement("member");
//			mbrElement.addAttribute("type", m.mbrType);
//			mbrElement.setText(m.mbrName);
//		}
//	}
//	private void addAllToXML(Element RootElement, ArrayList<kyLinkGroup> list) {
//		for(kyLinkGroup l : list) {
//			addElement(RootElement, l);
//		}
//	}
//	private Document initXML() {
//		Document doc = DocumentHelper.createDocument();
//		// Processing Instruction
//		Map<String, String> inMap = new HashMap<String, String>();
//		inMap.put("type", "text/xsl");
//		inMap.put("file", "Packages.xml");
//		doc.addProcessingInstruction("xml-stylesheet", inMap);
//		// root element.
//		Element PackageElement = doc.addElement("Packages");
//		PackageElement.addAttribute("MaxSize", "80Bytes");
//		PackageElement.addComment("Package type catalog");
//		addAllToXML(PackageElement, GroupList);
//
//		return doc;
//	}
//	private void saveXML(Document doc, File file) throws IOException {
//		OutputFormat fmt = OutputFormat.createPrettyPrint();
//		XMLWriter output;
//		output = new XMLWriter(new FileWriter(file), fmt);
//		output.write(doc);
//		output.close();
//	}
	private void readXML(File cfgFile) throws DocumentException {
		Document document = null;
		GroupList.clear();
		SAXReader reader = new SAXReader();
		document = reader.read(cfgFile);
		Element root = document.getRootElement();

		for (Iterator<Element> ie = root.elementIterator(); ie.hasNext();) {
            Element element = (Element) ie.next();

            String name = null; String id = null;
            for (Iterator<Attribute> ia = element.attributeIterator(); ia.hasNext();) {
               Attribute attribute = (Attribute) ia.next();
               if(attribute.getName().equals("name")) {
            	   name = (String) attribute.getData();
               } else if(attribute.getName().equals("id")) {
            	   id = (String) attribute.getData();
               }
            }
            kyLinkGroup group = new kyLinkGroup(name, id);
            IterateMemberElement(element, group);
            GroupList.add(group);
        }
	}

	private void IterateMemberElement(Element element, kyLinkGroup group) {
		int Offset = 0;
		for(Iterator<Element> ie = element.elementIterator(); ie.hasNext();) {
			kyLinkMember mbr = new kyLinkMember();
			Element ele = (Element) ie.next();
			mbr.setName(ele.getText());
			for(Iterator<Attribute> ia = ele.attributeIterator(); ia.hasNext();) {
				Attribute attribute = (Attribute) ia.next();
				if(attribute.getName().equals("type")) { // find Attribute for "type"
					String typ = (String) attribute.getData();
					int off = mbr.checkType(typ);
					if(off != -1) {
						mbr.setType(typ);
						mbr.setOffset(Offset);
						group.addMember(mbr);
						Offset += off;
					}
				}
			}
		}
	}
}
