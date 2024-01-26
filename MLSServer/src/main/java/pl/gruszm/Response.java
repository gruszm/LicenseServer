package pl.gruszm;

import java.text.SimpleDateFormat;
import java.util.Date;

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
}