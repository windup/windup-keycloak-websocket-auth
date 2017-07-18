package org.jboss.windup.web.accounts.common;

/**
 * @author <a href="mailto:dklingenberg@gmail.com">David Klingenberg</a>
 */
public class AccessTokenData
{
    private String id;
    private String subject;
    private String userName;
    private int issuedAt;
    private int expiration;

    public AccessTokenData(String id, String subject, String userName, int issuedAt, int expiration)
    {
        this.id = id;
        this.subject = subject;
        this.userName = userName;
        this.issuedAt = issuedAt;
        this.expiration = expiration;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public int getIssuedAt()
    {
        return issuedAt;
    }

    public void setIssuedAt(int issuedAt)
    {
        this.issuedAt = issuedAt;
    }

    public int getExpiration()
    {
        return expiration;
    }

    public void setExpiration(int expiration)
    {
        this.expiration = expiration;
    }
}
