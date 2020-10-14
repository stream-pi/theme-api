package com.StreamPi.ThemeAPI;


import com.StreamPi.Util.Exception.MinorException;
import com.StreamPi.Util.Exception.SevereException;
import com.StreamPi.Util.Version.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Themes {
    private final List<Theme> themeList;
    private final ArrayList<MinorException> errors;
    private boolean isBadThemeTheCurrentOne = false;

    Logger logger = LoggerFactory.getLogger(Themes.class);

    private String themePath;
    private String defaultThemeName;
    private Version minThemeSupportPlatform;

    public Themes(String themePath, String defaultThemeName, Version minThemeSupportPlatform) throws SevereException {
        this.themePath = themePath;
        this.defaultThemeName = defaultThemeName;
        this.minThemeSupportPlatform = minThemeSupportPlatform;

        themeList = new ArrayList<>();
        errors = new ArrayList<>();

        loadThemes();
    }

    public void loadThemes() throws SevereException {
        File themeFolder =  new File(themePath);
        if(!themeFolder.isDirectory())
        {
            throw new SevereException("Themes","Theme folder doesn't exist! Cant continue.");
        }


        File[] themeFolders = themeFolder.listFiles();
        if(themeFolders == null)
        {
            throw new SevereException("Themes","themefolders returned null. Cant continue!");
        }

        for(File eachFolder : themeFolders)
        {
            if(eachFolder.isDirectory())
            {
                File themeXML = new File(eachFolder.getAbsolutePath()+"/theme.xml");
                if(themeXML.exists() && themeXML.isFile())
                {
                    try {
                        Theme t = new Theme(eachFolder.getAbsolutePath());


                        if (minThemeSupportPlatform.isBiggerThan(t.getThemePlatformVersion()))
                        {
                            throw new MinorException("Theme version doesn't match minimum theme support level ("+ minThemeSupportPlatform + ") ("+t.getFullName()+")");
                        }

                        themeList.add(t);
                        logger.info("Added "+eachFolder.getName()+" to themes");
                    } catch (MinorException e) {
                        logger.error("Error adding "+eachFolder.getName()+" to themes");

                        if(eachFolder.getName().equals(defaultThemeName))
                            setBadThemeCurrentOne();

                        errors.add(e);

                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public boolean getIsBadThemeTheCurrentOne()
    {
        return isBadThemeTheCurrentOne;
    }

    private void setBadThemeCurrentOne()
    {
        isBadThemeTheCurrentOne = true;
    }

    public ArrayList<MinorException> getErrors()
    {
        return errors;
    }

    public void refreshThemeList() throws SevereException {
        logger.info("Refreshing themes ...");
        loadThemes();
        logger.info("... Done!");
    }

    public List<Theme> getThemeList()
    {
        return themeList;
    }
}
