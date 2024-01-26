package pl.gruszm;

public class LicenseInfo
{
    private String LicenceUserName;
    private int ValidationTime;

    public LicenseInfo(String licenceUserName, int validationTime)
    {
        LicenceUserName = licenceUserName;
        ValidationTime = validationTime;
    }

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
}