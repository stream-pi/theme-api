/*
Theme.java

Contributor(s): Debayan Sutradhar (@dubbadhar)

Check 'Theme Standard.md' if you want to understand the hierarchy.
This reads a theme folder.
 */

package com.stream_pi.theme_api;


import com.stream_pi.util.exception.MinorException;
import com.stream_pi.util.version.Version;
import com.stream_pi.util.xmlconfighelper.XMLConfigHelper;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class Theme
{

    private String fullName, shortName, author, website;
    private Version version, themePlatformVersion;
    private final File path;
    private Document document;

    public Theme(File path) throws MinorException {
        this.path = path;

        if(!path.isDirectory())
        {
            throw new MinorException("Theme", "Theme path "+path.getName()+" is not a folder.");
        }

        File themeFile = new File(path.getAbsolutePath() + "/theme.xml");
        if(!themeFile.isFile())
        {
            throw new MinorException("Theme", "Theme folder "+path.getName()+" has no theme.xml");
        }


        try {
            
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            document = dBuilder.parse(themeFile);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MinorException("Theme", "ConfigurationException occurred for theme folder "+path.getName());
        }

        loadUpThemeXMLContents();
    }

    private List<String> stylesheets = null;
    private List<String> fonts = null;

    public Version getThemePlatformVersion()
    {
        return themePlatformVersion;
    }

    private void loadUpThemeXMLContents() throws MinorException
    {
        //info
        fullName = path.getName();

        try
        {
            themePlatformVersion = new Version(document.getElementsByTagName("theme-platform-version").item(0).getTextContent());
        }
        catch (Exception e)
        {
            throw new MinorException("Invalid theme-platform-version ("+fullName+")");
        }

        Node infoElement = document.getElementsByTagName("info").item(0);

        shortName = XMLConfigHelper.getStringProperty(infoElement, "short-name", "Unknown", false);
        author = XMLConfigHelper.getStringProperty(infoElement, "author", "Unknown", false);
        website = XMLConfigHelper.getStringProperty(infoElement, "website", null, false);

        try
        {
            version = new Version(XMLConfigHelper.getStringProperty(infoElement, "version"));
        }
        catch (Exception e)
        {
            throw new MinorException("Invalid theme-version ("+fullName+")");
        }

        //theme

        if(XMLConfigHelper.doesElementExist(document, "theme"))
        {
            //Stylesheets
            Element themeElement = (Element) document.getElementsByTagName("theme").item(0);

            if(XMLConfigHelper.doesElementExist(themeElement, "stylesheets"))
            {
                Element stylesheetsElement = (Element) themeElement.getElementsByTagName("stylesheets").item(0);

                
                NodeList ss = stylesheetsElement.getChildNodes();
                if(ss.getLength() > 0)
                {
                    stylesheets = new ArrayList<>();

                    for(int i = 0;i<ss.getLength();i++)
                    {
                        Node n = ss.item(i);
                        if(n.getNodeType() != Node.ELEMENT_NODE)
                            continue;

                        if(n.getNodeName() != "stylesheet")
                            continue;

                        Element stylesheetElement = (Element) n;

                        stylesheets.add(stylesheetElement.getTextContent());
                    }
                    
                }
            }

            if(XMLConfigHelper.doesElementExist(themeElement, "fonts"))
            {
                Element fontsElement = (Element) themeElement.getElementsByTagName("fonts").item(0);

                
                NodeList ss = fontsElement.getChildNodes();
                if(ss.getLength() > 0)
                {
                    fonts = new ArrayList<>();

                    for(int i = 0;i<ss.getLength();i++)
                    {
                        Node n = ss.item(i);
                        if(n.getNodeType() != Node.ELEMENT_NODE)
                            continue;

                        if(n.getNodeName() != "font")
                            continue;

                        Element fontElement = (Element) n;

                        fonts.add(fontElement.getTextContent());
                    }
                    
                }
            }
        }


        if(stylesheets == null)
        {
            throw new MinorException("No stylesheets found. At least one required. ("+fullName+")");
        }

        for (int i=0;i<stylesheets.size(); i++)
        {
            stylesheets.set(i, Paths.get(path.getAbsolutePath() + "/" + stylesheets.get(i)).toUri().toString());
        }

        if(fonts!=null)
        {
            for (int i=0;i<fonts.size(); i++)
            {
                fonts.set(i, Paths.get( path.getAbsolutePath() + "/" + fonts.get(i)).toUri().toString());
            }
        }
    }

    public List<String> getStylesheets()
    {
        return stylesheets;
    }

    public List<String> getFonts()
    {
        return fonts;
    }

    public String getFullName()
    {
        return fullName;
    }

    public String getShortName()
    {
        return shortName;
    }

    public String getAuthor()
    {
        return author;
    }

    public String getWebsite()
    {
        return website;
    }

    public Version getVersion()
    {
        return version;
    }
}
