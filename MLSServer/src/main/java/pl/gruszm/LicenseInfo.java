package pl.gruszm;

import java.util.Date;

public class LicenseInfo
{
    private String LicenceUserName;
    private int ValidationTime;
    private Date expiryTime;
    private boolean isUsed;

    public String getLicenceUserName()
    {
        return LicenceUserName;
    }

    public void setLicenceUserName(String licenceUserName)
    {
        LicenceUserName = licenceUserName;
    }

    public int getValidationTime()
    {
        return ValidationTime;
    }

    public void setValidationTime(int validationTime)
    {
        ValidationTime = validationTime;
    }

    public Date getExpiryTime()
    {
        return expiryTime;
    }

    public void setExpiryTime(Date expiryTime)
    {
        this.expiryTime = expiryTime;
    }

    public boolean isUsed()
    {
        return isUsed;
    }

    public void setUsed(boolean used)
    {
        isUsed = used;
    }
}