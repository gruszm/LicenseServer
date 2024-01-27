package pl.gruszm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Response
{
    private String LicenceUserName;
    private boolean Licence;
    private String Description;
    private String Expired;

    public Response(String licenceUserName, boolean licence, String description, Date expired)
    {
        LicenceUserName = licenceUserName;
        Licence = licence;
        Description = description;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        this.Expired = (expired != null) ? sdf.format(expired) : null;
    }

    public String getLicenceUserName()
    {
        return LicenceUserName;
    }

    public void setLicenceUserName(String licenceUserName)
    {
        LicenceUserName = licenceUserName;
    }

    public boolean isLicenseValid()
    {
        return Licence;
    }

    public void setLicence(boolean licence)
    {
        Licence = licence;
    }

    public String getDescription()
    {
        return Description;
    }

    public void setDescription(String description)
    {
        Description = description;
    }

    public String getExpired()
    {
        return Expired;
    }

    public void setExpired(String expired)
    {
        Expired = expired;
    }

    public long getExpiryTime()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        try
        {
            Date expiryDate = sdf.parse(Expired);
            return (expiryDate != null) ? expiryDate.getTime() : -1;
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean isValid()
    {
        long expiryTime = getExpiryTime();
        return expiryTime > System.currentTimeMillis();
    }
}