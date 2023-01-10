package data.restfullbooker;

public class TokenBuilder
{

    public static TokenCreds getToken ()
    {
        return TokenCreds.builder ()
                .username ("admin")
                .password ("password123")
                .build ();
    }

}
