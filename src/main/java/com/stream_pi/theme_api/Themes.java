package com.stream_pi.theme_api;


import com.stream_pi.theme_api.i18n.I18N;
import com.stream_pi.util.exception.MinorException;
import com.stream_pi.util.exception.SevereException;
import com.stream_pi.util.version.Version;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class Themes {
    private final List<Theme> themeList;
    private final ArrayList<MinorException> errors;
    private boolean isBadThemeTheCurrentOne = false;

    Logger logger = Logger.getLogger(Themes.class.getName());

    private String themePath;
    private String defaultThemePath;
    private String defaultThemeName;
    private Version minThemeSupportPlatform;

    private boolean isDefaultThemePresent = false;

    public Themes(String defaultThemePath, String themePath, String defaultThemeName, Version minThemeSupportPlatform) throws SevereException {
        this.themePath = themePath;
        this.defaultThemePath = defaultThemePath;
        this.defaultThemeName = defaultThemeName;
        this.minThemeSupportPlatform = minThemeSupportPlatform;

        themeList = new ArrayList<>();
        errors = new ArrayList<>();

        shortDir = new ArrayList();
        loadThemes(defaultThemePath);

        if(!themePath.equals(defaultThemePath))
            loadThemes(themePath);

        shortDir = null;
    }

    public boolean isDefaultThemePresent()
    {
        return isDefaultThemePresent;
    }

    private ArrayList<String> shortDir = null;

    public void loadThemes(String themePath) throws SevereException
    {
        File themeFolder =  new File(themePath);
        if(!themeFolder.isDirectory())
        {
            throw new SevereException(I18N.getString("Themes.themeFolderNotADirectoryOrDoesNotExist"));
        }


        File[] themeFolders = themeFolder.listFiles();
        if(themeFolders == null)
        {
            throw new SevereException(I18N.getString("Themes.themeFoldersIsNull"));
        }

        for(File eachFolder : themeFolders)
        {
            if(eachFolder.isDirectory())
            {
                File themeXML = new File(eachFolder.getAbsolutePath()+"/theme.xml");
                if(themeXML.exists() && themeXML.isFile())
                {
                    try
                    {
                        Theme t = new Theme(eachFolder);

                        if (minThemeSupportPlatform.isBiggerThan(t.getThemePlatformVersion()))
                        {
                            throw new MinorException(I18N.getString("Themes.unsupportedTheme", t.getFullName(), minThemeSupportPlatform));
                        }

                        if(!isDefaultThemePresent())
                        {
                            if(t.getFullName().equals(defaultThemeName))
                            {
                                isDefaultThemePresent = true;
                            }
                        }


                        if(!shortDir.contains(t.getFullName()))
                        {
                            shortDir.add(t.getFullName());

                            themeList.add(t);
                                
                            logger.info("Added "+eachFolder.getName()+" to themes!");
                        }
                        else
                        {
                            logger.info("Skipping "+eachFolder.getName()+" because already added!");
                        }
                    } 
                    catch (MinorException e)
                    {
                        logger.severe("Error adding "+eachFolder.getName()+" to themes");

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

  

    public List<Theme> getThemeList()
    {
        return themeList;
    }
}
