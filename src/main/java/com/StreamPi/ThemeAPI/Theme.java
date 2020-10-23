/*
Theme.java

Contributor(s): Debayan Sutradhar (@dubbadhar)

Check 'Theme Standard.md' if you want to understand the hierarchy.
This reads a theme folder.
 */


package com.StreamPi.ThemeAPI;


import com.StreamPi.Util.Exception.MinorException;
import com.StreamPi.Util.Version.Version;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;


public class Theme {

    private String fullName, shortName, author, website;
    private Version version, themePlatformVersion;
    private int serverIconSize, clientIconSize;
    private String serverIconColorHex, clientIconColorHex;
    private final File path;
    private final XMLConfiguration config;


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


        Configurations configs = new Configurations();
        FileBasedConfigurationBuilder<XMLConfiguration> builder = configs.xmlBuilder(themeFile);

        try {
            config = builder.getConfiguration();
        } catch (ConfigurationException e) {
            e.printStackTrace();
            throw new MinorException("Theme", "ConfigurationException occurred for theme folder "+path.getName());
        }

        loadUpThemeXMLContents();
    }

    private List<String> serverStylesheets;
    private List<String> serverFonts;

    private List<String> clientStylesheets;
    private List<String> clientFonts;

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
            themePlatformVersion = new Version(config.getString("theme-platform-version"));
        }
        catch (MinorException e)
        {
            throw new MinorException("Invalid theme-platform-version ("+fullName+")");
        }

        shortName = config.getString("info.short-name", "Unknown");
        author = config.getString("info.author", "Unknown");
        website = config.getString("info.website", "Unknown");

        try
        {
            version = new Version(config.getString("info.version"));
        }
        catch (MinorException e)
        {
            throw new MinorException("Invalid theme-version ("+fullName+")");
        }

        //theme

        //server

        try
        {
            serverIconSize = config.getInt("theme.server.icon.size");
        }
        catch (NoSuchElementException e)
        {
            throw new MinorException("server.icon.size property missing from theme ("+fullName+")");
        }

        try
        {
            serverIconColorHex = config.getString("theme.server.icon.color");
        }
        catch (NoSuchElementException e)
        {
            throw new MinorException("server.icon.color property missing from theme ("+fullName+")");
        }

        serverStylesheets = config.getList(String.class, "theme.server.stylesheets.stylesheet");

        serverFonts = config.getList(String.class, "theme.server.fonts.font");


        //client

        try
        {
            clientIconSize = config.getInt("theme.client.icon.size");
        }
        catch (NoSuchElementException e)
        {
            throw new MinorException("client.icon.size property missing from theme ("+fullName+")");
        }

        try
        {
            clientIconColorHex = config.getString("theme.client.icon.color");
        }
        catch (NoSuchElementException e)
        {
            throw new MinorException("client.icon.color property missing from theme ("+fullName+")");
        }

        clientStylesheets = config.getList(String.class, "theme.client.stylesheets.stylesheet");


        clientFonts = config.getList(String.class, "theme.client.fonts.font");


        if(serverStylesheets == null && clientStylesheets == null)
        {
            throw new MinorException("No stylesheets found. At least one required. ("+fullName+")");
        }

        if(serverStylesheets != null)
        {
            for (int i=0;i<serverStylesheets.size(); i++)
            {
                serverStylesheets.set(i, Paths.get(path.getAbsolutePath() + "/" + serverStylesheets.get(i)).toUri().toString());
            }
        }


        if(serverFonts!=null)
        {
            for (int i=0;i<serverFonts.size(); i++)
            {
                serverFonts.set(i, Paths.get( path.getAbsolutePath() + "/" + serverFonts.get(i)).toUri().toString());
            }
        }


        if(clientStylesheets!=null)
        {
            for (int i=0;i<clientStylesheets.size(); i++)
            {
                clientStylesheets.set(i, Paths.get(path.getAbsolutePath() + "/" + clientStylesheets.get(i)).toUri().toString());
            }
        }


        if(clientFonts!=null)
        {
            for (int i=0;i<clientFonts.size(); i++)
            {
                clientFonts.set(i, Paths.get(path.getAbsolutePath() + "/" + clientFonts.get(i)).toUri().toString());
            }
        }
    }

    public List<String> getSeverStylesheets()
    {
        return serverStylesheets;
    }

    public List<String> getServerFonts()
    {
        return serverFonts;
    }

    public List<String> getClientStylesheets()
    {
        return serverStylesheets;
    }

    public List<String> getClientFonts()
    {
        return serverFonts;
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

    public int getServerIconSize()
    {
        return serverIconSize;
    }

    public int getClientIconSize()
    {
        return clientIconSize;
    }

    public String getServerIconColorHex()
    {
        return serverIconColorHex;
    }

    public String getClientIconColorHex()
    {
        return clientIconColorHex;
    }
}
