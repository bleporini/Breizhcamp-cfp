package service;

import models.Credentials;
import models.User;
import play.Application;
import play.Logger;
import scala.Option;
import scala.Some;
import securesocial.core.*;
import securesocial.core.java.BaseUserService;
import securesocial.core.java.Token;

import java.util.Date;
import java.util.HashMap;

import static play.Logger.info;

/**
 * Classe utilisée par SecureSocial pour la gestion des Identity
 *
 */
public class MockCfpUserServiceDelegate extends CfpUserServiceDelegate {

    private static final String PROVIDER_ID = "userpass";

    public MockCfpUserServiceDelegate(Application application) {
        super(application);
        info("Loading the MockPasswordHasher");
        new MockPasswordHasher(application.getWrappedApplication()).onStart();
    }
    public static final Identity defaultIdentity = new Identity() {
        private final IdentityId slash = new IdentityId("slash", PROVIDER_ID);

        @Override
        public IdentityId identityId() {
            return slash;
        }

        @Override
        public String firstName() {
            return "Saul";
        }

        @Override
        public String lastName() {
            return "Hudson";
        }

        @Override
        public String fullName() {
            return "Saul 'Slash' Hudson";
        }

        @Override
        public Option<String> email() {
            return Some.apply("slash@gnr.fr");
        }

        @Override
        public Option<String> avatarUrl() {
            return Option.apply(null);
        }

        @Override
        public AuthenticationMethod authMethod() {
            return AuthenticationMethod.UserPassword();
        }

        @Override
        public Option<OAuth1Info> oAuth1Info() {
            return Option.apply(null);
        }

        @Override
        public Option<OAuth2Info> oAuth2Info() {
            return Option.apply(null);
        }

        @Override
        public Option<PasswordInfo> passwordInfo() {
            return Option.apply(new PasswordInfo("nocheck", "no pass", Option.apply("")));
        }
    };

    @Override
    public Identity doFind(IdentityId identityId) {
        Logger.debug("returning default identity for : {} / {}",identityId.userId(),identityId.providerId());

        return defaultIdentity ;
    }



}
